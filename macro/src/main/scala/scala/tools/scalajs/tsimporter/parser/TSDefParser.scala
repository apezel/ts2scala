/* TypeScript importer for Scala.js
 * Copyright 2013 LAMP/EPFL
 * @author  Sbastien Doeraene
 */

package scala.tools.scalajs.tsimporter.parser

import scala.tools.scalajs.tsimporter.Trees._
import java.io.File
import scala.collection.mutable.ListBuffer
import scala.util.parsing.combinator._
import scala.util.parsing.combinator.token._
import scala.util.parsing.combinator.syntactical._
import scala.util.parsing.input._

class TSDefParser extends StdTokenParsers with ImplicitConversions {

  type Tokens = StdTokens
  val lexical: TSDefLexical = new TSDefLexical

  lexical.reserved ++= List(
      // Value keywords
      "true", "false", "null", "undefined",

      // Current JavaScript keywords
      "break", "case", "catch", "continue", "debugger", "default", "delete",
      "do", "else", "finally", "for", "function", "if", "in", "instanceof",
      "new", "return", "switch", "this", "throw", "try", "typeof", "var",
      "void", "while", "with",

      // Future reserved keywords - some used in TypeScript
      "class", "const", "enum", "export", "extends", "import", "super",

      // Future reserved keywords in Strict mode - some used in TypeScript
      "implements", "interface", "let", "package", "private", "protected",
      "public", "static", "yield",

      // Additional keywords of TypeScript
      "declare", "module"
  )

  lexical.delimiters ++= List(
      "{", "}", "(", ")", "[", "]",
      ".", ";", ",",
      "<", ">", "<=", ">=", "==", "!=", "===", "!==",
      "+", "-", "*", "/", "%", "++", "--",
      "<<", ">>", ">>>", "&", "|",
      "^", "!", "~",
      "&&", "||",
      "?", ":",
      "=", "+=", "-=", "*=", "/=", "%=",
      "<<=", ">>=", ">>>=", "&=", "|=", "^=",

      // TypeScript-specific
      "...", "=>"
  )

  def parseDefinitions(input: Reader[Char]) =
    phrase(ambientDeclarations)(new lexical.Scanner(input))

  lazy val ambientDeclarations: Parser[List[DeclTree]] =
    rep(ambientDeclaration)

  lazy val ambientDeclaration: Parser[DeclTree] =
    opt("declare") ~> ambientDeclaration1

  lazy val ambientDeclaration1 = (
      ambientModuleDecl | ambientVarDecl | ambientFunctionDecl
    | ambientInterfaceDecl | ambientClassDecl
  )

  lazy val ambientModuleDecl: Parser[DeclTree] =
    "module" ~> rep1sep(identifier | stringLiteral , ".") ~ moduleBody ^^ {
      case nameParts ~ body =>
        nameParts.init.foldRight(ModuleDecl(Ident(nameParts.last.name), body)) {
          (name, inner) => ModuleDecl(Ident(name.name), inner :: Nil)
        }
    }

  lazy val moduleBody: Parser[List[DeclTree]] =
    "{" ~> rep(moduleElementDecl) <~ "}"

  lazy val moduleElementDecl: Parser[DeclTree] =
    opt("export") ~> moduleElementDecl1

  lazy val moduleElementDecl1: Parser[DeclTree] = (
      ambientModuleDecl | ambientVarDecl | ambientFunctionDecl
    | ambientInterfaceDecl | ambientClassDecl | ambientExportRefDecl
  )

  lazy val ambientVarDecl: Parser[DeclTree] =
    "var" ~> identifier ~ optTypeAnnotation <~ opt(";") ^^ VarDecl

  lazy val ambientFunctionDecl: Parser[DeclTree] =
    staticModifier ~ ("function" ~> identifier) ~ functionSignature <~ opt(";") ^^ FunctionDecl

  lazy val ambientInterfaceDecl: Parser[DeclTree] =
    "interface" ~> typeName ~ tparams ~ intfInheritance ~ memberBlock <~ opt(";") ^^ InterfaceDecl
    
  lazy val ambientClassDecl: Parser[DeclTree] =
    "class" ~> typeName ~ tparams ~ intfInheritance ~ intfImplementation ~ memberBlock <~ opt(";") ^^ ClassDecl

  lazy val tparams = (
      "<" ~> rep1sep(typeParam, ",") <~ ">"
    | success(Nil)
  )
 
  lazy val ambientExportRefDecl: Parser[DeclTree] =
  	"=" ~> identifier <~ opt(";") ^^ ExportRefDecl

  lazy val typeParam: Parser[TypeParam] =
    typeName ~ opt("extends" ~> typeRef) ^^ TypeParam

  lazy val intfInheritance = (
      "extends" ~> repsep(typeRef, ",")
    | success(Nil)
  )

  lazy val intfImplementation = (
      "implements" ~> repsep(typeRef, ",")
    | success(Nil)
  )

  lazy val functionSignature =
    tparams ~ ("(" ~> repsep(functionParam, ",") <~ ")") ~ optResultType ^^ FunSignature

  lazy val functionParam =
    repeatedParamMarker ~ identifier ~ optionalMarker ~ optParamType ^^ {
      case false ~ i ~ o ~ t =>
        FunParam(i, o, t)
      case _ ~ i ~ o ~ Some(ArrayType(t)) =>
        FunParam(i, o, Some(RepeatedType(t)))
      case _ ~ i ~ o ~ t =>
        Console.err.println(
            s"Warning: Dropping repeated marker of param $i because its type $t is not an array type")
        FunParam(i, o, t)
    }

  lazy val staticModifier = 
    opt("static") ^^ (_.isDefined)

  lazy val repeatedParamMarker =
    opt("...") ^^ (_.isDefined)

  lazy val optionalMarker =
    opt("?") ^^ (_.isDefined)

  lazy val optParamType =
    opt(":" ~> paramType)

  lazy val paramType: Parser[TypeTree] = (
      typeDesc
    | stringLiteral ^^ ConstantType
  )

  lazy val optResultType =
    opt(":" ~> resultType)

  lazy val resultType: Parser[TypeTree] = (
      ("void" ^^^ TypeRef(CoreType("void")))
    | typeDesc
  )

  lazy val optTypeAnnotation =
    opt(typeAnnotation)

  lazy val typeAnnotation =
    ":" ~> typeDesc

  lazy val typeDesc: Parser[TypeTree] = (
      typeRef
    | objectType
    | functionType
  )

  lazy val typeRef: Parser[TypeRef] =
    baseTypeRef ~ opt(typeArgs) ~ rep("[" ~ "]") ^^ {
      case base ~ optTargs ~ arrayDims =>
        val withArgs = TypeRef(base, optTargs getOrElse Nil)
        (withArgs /: arrayDims) {
          (elem, _) => ArrayType(elem)
        }
    }

  lazy val baseTypeRef: Parser[BaseTypeRef] =
    rep1sep("void" | ident, ".") ^^ { parts =>
      if (parts.tail.isEmpty) typeNameToTypeRef(parts.head)
      else QualifiedTypeName(parts.init map Ident, TypeName(parts.last))
    }

  lazy val typeArgs: Parser[List[TypeRef]] =
    "<" ~> rep1sep(typeRef, ",") <~ ">"

  lazy val functionType: Parser[TypeTree] =
    tparams ~ ("(" ~> repsep(functionParam, ",") <~ ")") ~ ("=>" ~> resultType) ^^ {
      case tparams ~ params ~ resultType =>
        FunctionType(FunSignature(tparams, params, Some(resultType)))
    }

  lazy val objectType: Parser[TypeTree] =
    memberBlock ^^ ObjectType

  lazy val memberBlock: Parser[List[MemberTree]] =
    "{" ~> rep(typeMember <~ opt(";")) <~ "}"

  lazy val typeMember: Parser[MemberTree] =
    callMember | constructorMember | indexMember | namedMember

  lazy val callMember: Parser[MemberTree] =
    opt(scopeModifier) ~> staticModifier ~ functionSignature ^^ CallMember

  lazy val constructorMember: Parser[MemberTree] =
    "new" ~> functionSignature ^^ ConstructorMember

  lazy val indexMember: Parser[MemberTree] =
    ("[" ~> identifier ~ typeAnnotation <~ "]") ~ typeAnnotation ^^ IndexMember

  lazy val namedMember: Parser[MemberTree] =
    opt(scopeModifier) ~> staticModifier ~ propertyName ~ optionalMarker >> {
      case isStatic ~ name ~ optional => (
          functionSignature ^^ (FunctionMember(isStatic, name, optional, _))
        | optTypeAnnotation ^^ (PropertyMember(isStatic, name, optional, _))
      )
    }

  lazy val scopeModifier = "public" //TODO : detect others

  lazy val identifier =
    identLike ^^ Ident

  lazy val typeName =
    identLike ^^ TypeName

  lazy val identLike =
    "declare" | "module" | "delete" | "continue" | "default" | ident

  lazy val propertyName: Parser[PropertyName] =
    identifier | stringLiteral

  lazy val stringLiteral: Parser[StringLiteral] =
    stringLit ^^ StringLiteral

  private val isCoreTypeName =
    Set("any", "void", "number", "bool", "boolean", "string")

  def typeNameToTypeRef(name: String): BaseTypeRef =
    if (isCoreTypeName(name)) CoreType(name)
    else TypeName(name)

  object ArrayType {
    def apply(elem: TypeRef): TypeRef =
      TypeRef(TypeName("Array"), List(elem))

    def unapply(typeRef: TypeRef) = typeRef match {
      case TypeRef(TypeName("Array"), List(elem)) => Some(elem)
      case _ => None
    }
  }
}
