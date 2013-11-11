/* TypeScript importer for Scala.js
 * Copyright 2013 LAMP/EPFL
 * @author  S��bastien Doeraene
 */

package scala.tools.scalajs.tsimporter

import scala.util.parsing.input.Positional

object Trees {
  // Tree

  abstract sealed class Tree extends Positional {
    /*override def toString() = {
      val baos = new java.io.ByteArrayOutputStream()
      val writer = new java.io.PrintWriter(baos)
      val printer = new TreePrinter(writer)
      printer.printTree(this)
      writer.close()
      baos.toString()
    }*/
  }

  sealed trait DeclTree extends Tree
  sealed trait TermTree extends Tree
  sealed trait TypeTree extends Tree
  sealed trait MemberTree extends Tree

  // Identifiers and properties

  sealed trait PropertyName extends TermTree {
    def name: String
  }

  object PropertyName {
    def apply(name: String): PropertyName = {
      if (Ident.isValidIdentifier(name)) Ident(name)
      else StringLiteral(name)
    }

    def unapply(tree: PropertyName): Some[String] =
      Some(tree.name)
  }

  case class Ident(name: String) extends Tree with PropertyName {
    Ident.requireValidIdent(name)
  }

  object Ident extends (String => Ident) {
    final def isValidIdentifier(name: String): Boolean = {
      val c = name.head
      (c == '$' || c == '_' || c.isUnicodeIdentifierStart) &&
          name.tail.forall(c => c == '$' || c.isUnicodeIdentifierPart)
    }

    @inline final def requireValidIdent(name: String) {
      require(isValidIdentifier(name), s"${name} is not a valid identifier")
    }
  }

  // Declarations

  case class ModuleDecl(name: Ident, members: List[DeclTree]) extends DeclTree

  case class VarDecl(name: Ident, tpe: Option[TypeTree]) extends DeclTree

  case class FunctionDecl(isStatic:Boolean, name: Ident, signature: FunSignature) extends DeclTree
  
  case class ExportRefDecl(name: Ident) extends DeclTree

  // Function signature

  case class FunSignature(tparams: List[TypeParam], params: List[FunParam],
      resultType: Option[TypeTree]) extends Tree

  case class FunParam(name: Ident, optional: Boolean, tpe: Option[TypeTree]) extends Tree

  // Type parameters

  case class TypeParam(name: TypeName, upperBound: Option[TypeRef]) extends Tree

  // Literals

  sealed trait Literal extends TermTree

  case class Undefined() extends Literal

  case class Null() extends Literal

  case class BooleanLiteral(value: Boolean) extends Literal

  case class NumberLiteral(value: Double) extends Literal

  case class StringLiteral(value: String) extends Literal with PropertyName {
    override def name = value
  }

  // Type descriptions

  case class TypeDecl(name: TypeName, tpe: TypeTree) extends DeclTree

  case class InterfaceDecl(name: TypeName, tparams: List[TypeParam],
      inheritance: List[TypeRef], members: List[MemberTree]) extends DeclTree

  case class ClassDecl(name: TypeName, tparams: List[TypeParam],
      inheritance: List[TypeRef], implementation: List[TypeRef], members: List[MemberTree]) extends DeclTree

  case class TypeRef(name: BaseTypeRef, tparams: List[TypeRef] = Nil) extends TypeTree

  sealed abstract class BaseTypeRef extends Tree

  case class CoreType(name: String) extends BaseTypeRef

  case class TypeName(name: String) extends BaseTypeRef {
    Ident.requireValidIdent(name)
  }

  case class QualifiedTypeName(qualifier: List[Ident], name: TypeName) extends BaseTypeRef

  case class ConstantType(literal: Literal) extends TypeTree

  case class ObjectType(members: List[MemberTree]) extends TypeTree

  case class FunctionType(signature: FunSignature) extends TypeTree

  case class RepeatedType(underlying: TypeTree) extends TypeTree

  // Type members

  case class CallMember(isStatic:Boolean, signature: FunSignature) extends MemberTree

  case class ConstructorMember(signature: FunSignature) extends MemberTree

  case class IndexMember(indexName: Ident, indexType: TypeTree, valueType: TypeTree) extends MemberTree

  case class PropertyMember(isStatic:Boolean, name: PropertyName, optional: Boolean, tpe: Option[TypeTree]) extends MemberTree

  case class FunctionMember(isStatic:Boolean, name: PropertyName, optional: Boolean, signature: FunSignature) extends MemberTree
}
