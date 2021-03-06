/* Modified by Arnaud PEZEL, Apyx, 2013*/
package com.apyx.scala.ts2scala.ts.importer

import Trees.{ TypeRef => TypeRefTree, _ }
import com.apyx.scala.ts2scala.definition._
import java.lang.Exception

/** The meat and potatoes: the importer
 *  It reads the TypeScript AST and produces (hopefully) equivalent Scala
 *  code.
 */
class Importer {
  import Importer._

  var rootPackage:PackageSymbol = null
  
  /** Entry point */
  def apply(declarations: List[DeclTree]) = {
  	
    rootPackage = new PackageSymbol(Name.EMPTY)

    for (declaration <- declarations)
    	ProcessQueue.push { () => processDecl(rootPackage, declaration) }
    	
    ProcessQueue.run()

    this
  }
  
  /* added : need to parse every declaration before to go deeper. Flattenize process */
  private object ProcessQueue
  {
	  	private var processDeclQueue:List[() => Unit] = List()
		def push(f:() => Unit) = { processDeclQueue = processDeclQueue :+ f;  }
  	
		def run():Unit = {
			while (processDeclQueue.size > 0)
			{
				processDeclQueue(0)()
				processDeclQueue = processDeclQueue.drop(1)
			}
		}
  }

  private def processDecl(owner: ContainerSymbol, declaration: DeclTree) {
    declaration match {
      case ModuleDecl(IdentName(name), innerDecls) =>
        assert(owner.isInstanceOf[PackageSymbol],
            s"Found package $name in non-package $owner")
        val sym = owner.asInstanceOf[PackageSymbol].getPackageOrCreate(name.asInstanceOf[Name])

        for (innerDecl <- innerDecls)
          ProcessQueue.push { () => processDecl(sym, innerDecl) }

      case VarDecl(IdentName(name), Some(tpe @ ObjectType(members))) =>
        val sym = owner.getModuleOrCreate(name.asInstanceOf[Name])
        processMembersDecls(owner, sym, members)

      case TypeDecl(TypeNameName(name), tpe @ ObjectType(members)) =>
        val sym = owner.getClassOrCreate(name.asInstanceOf[Name])
        processMembersDecls(owner, sym, members)

      case InterfaceDecl(TypeNameName(name), tparams, inheritance, members) =>
        val sym = owner.getClassOrCreate(name.asInstanceOf[Name])
        sym.parents ++= inheritance.map(typeToScala)
        sym.tparams ++= typeParamsToScala(tparams)
        sym.isTrait = true
        processMembersDecls(owner, sym, members)

      case ClassDecl(TypeNameName(name), tparams, inheritance, implementation, members) =>
        val sym = owner.getClassOrCreate(name.asInstanceOf[Name])
        sym.parents ++= (inheritance ++ implementation).map(typeToScala)
        sym.tparams ++= typeParamsToScala(tparams)
        processMembersDecls(owner, sym, members)

      case VarDecl(IdentName(name), TypeOrAny(tpe)) =>
        val sym = new FieldSymbol(name.asInstanceOf[Name])
        sym.tpe = typeToScala(tpe)
        owner.addSymbol(sym)

      case FunctionDecl(isStatic:Boolean, IdentName(name), signature) =>
        processDefDecl(isStatic, owner, name.asInstanceOf[Name], signature)
        
      case ExportRefDecl(IdentName(name)) =>
      	val sym = owner.getExportFieldRefOrAbort(name.asInstanceOf[Name])
      	//println("expoooort "+sym)

      case _ =>
        owner.members += new CommentSymbol("??? "+declaration)
    }
  }

  private def processMembersDecls(enclosing: ContainerSymbol,
      owner: ContainerSymbol, members: List[MemberTree]) {

    val OwnerName = owner.name

    lazy val companionClassRef = {
      val tparams = enclosing.findClass(OwnerName) match {
        case Some(clazz) =>
          clazz.tparams.toList.map(tp => TypeRefTree(TypeNameName(tp.name), Nil))
        case _ => Nil
      }
      TypeRefTree(TypeNameName(OwnerName), tparams)
    }

    for (member <- members) member match {
      case CallMember(isStatic, signature) =>
        processDefDecl(isStatic, owner, Name("apply"), signature)

      case ConstructorMember(sig @ FunSignature(tparamsIgnored, params, Some(resultType)))
      if owner.isInstanceOf[ModuleSymbol] && resultType == companionClassRef =>
        val classSym = enclosing.getClassOrCreate(owner.name)
        classSym.isTrait = false
        processDefDecl(false, classSym, Name.CONSTRUCTOR,
            FunSignature(Nil, params, Some(TypeRefTree(CoreType("void")))))

      case PropertyMember(isStatic, PropertyNameName(name), opt, tpe) =>
        if (name.asInstanceOf[Name].name != "prototype") {
          val sym = new FieldSymbol(name.asInstanceOf[Name])
          sym.isStatic = isStatic
          sym.tpe = tpe.map(typeToScala) getOrElse TypeRef(QualifiedName(Name("Any")))
          owner.addSymbol(sym)
        }

      case FunctionMember(isStatic, PropertyNameName(name), opt, signature) =>
        processDefDecl(isStatic, owner, name.asInstanceOf[Name], signature)

      case IndexMember(IdentName(indexName), indexType, valueType) =>
        val indexTpe = typeToScala(indexType)
        val valueTpe = typeToScala(valueType)

        val getterSym = owner.newMethod(Name("apply"))
        getterSym.params += new ParamSymbol(indexName.asInstanceOf[Name], indexTpe)
        getterSym.resultType = valueTpe
        getterSym.isBracketAccess = true

        val setterSym = owner.newMethod(Name("update"))
        setterSym.params += new ParamSymbol(indexName.asInstanceOf[Name], indexTpe)
        setterSym.params += new ParamSymbol(Name("v"), valueTpe)
        setterSym.resultType = TypeRef.Unit
        setterSym.isBracketAccess = true

      case _ =>
        owner.members += new CommentSymbol("??? "+member)
    }
  }

  private def processDefDecl(isStatic:Boolean, owner: ContainerSymbol, name: Name,
      signature: FunSignature) {
    // Discard specialized signatures
    if (signature.params.exists(_.tpe.exists(_.isInstanceOf[ConstantType])))
      return

    for (sig <- makeAlternatives(signature)) {
      
    	val sym = new MethodSymbol(name)
      sym.isStatic = isStatic

      sym.tparams ++= typeParamsToScala(sig.tparams)

      for (FunParam(IdentName(paramName), opt, TypeOrAny(tpe)) <- sig.params) {
        val paramSym = new ParamSymbol(paramName.asInstanceOf[Name])
        tpe match {
          case RepeatedType(tpe0) =>
            paramSym.tpe = TypeRef.Repeated(typeToScala(tpe0))
          case _ =>
            paramSym.tpe = typeToScala(tpe)
        }
        sym.params += paramSym
      }

      sym.resultType = typeToScala(signature.resultType.orDynamic, true)
      
      owner.addSymbol(sym)
      		
    }
  }

  private def makeAlternativeParamss(
      params: List[FunParam]): List[List[FunParam]] = {
    if (params.isEmpty || !params.last.optional) params :: Nil
    else params :: makeAlternativeParamss(params.init)
  }

  private def makeAlternatives(signature: FunSignature): List[FunSignature] = {
    for (params <- makeAlternativeParamss(signature.params))
      yield FunSignature(signature.tparams, params, signature.resultType)
  }

  private def typeParamsToScala(tparams: List[TypeParam]): List[TypeParamSymbol] = {
    for (TypeParam(TypeNameName(tparam), upperBound) <- tparams) yield
      new TypeParamSymbol(tparam.asInstanceOf[Name], upperBound map typeToScala)
  }

  private def typeToScala(tpe: TypeTree): TypeRef =
    typeToScala(tpe, false)

  private def typeToScala(tpe: TypeTree, anyAsDynamic: Boolean): TypeRef = {
    tpe match {
      case TypeRefTree(tpe: CoreType, Nil) =>
        coreTypeToScala(tpe, anyAsDynamic)

      case TypeRefTree(base, targs) =>
        val baseTypeRef:QualifiedName = base match {
          case TypeName("Array") => QualifiedName.Array
          case TypeName("Function") => QualifiedName.FunctionBase
          case TypeNameName(name) => Importer.dialect.dictionnary.get(name.asInstanceOf[Name]) getOrElse QualifiedName(name.asInstanceOf[Name])
          case QualifiedTypeName(qualifier, TypeNameName(name)) =>
            val qual1 = qualifier map (x => Name(x.name))
            QualifiedName((qual1 :+ name.asInstanceOf[Name]): _*)
          case _: CoreType => throw new MatchError(base)
        }
        TypeRef(baseTypeRef, targs map typeToScala)

      case ObjectType(members) =>
        // ???
        TypeRef.Any

      case FunctionType(FunSignature(tparams, params, Some(resultType))) =>
        if (!tparams.isEmpty) {
          TypeRef.Function
        } else {
          val paramTypes =
            for (FunParam(_, _, TypeOrAny(tpe)) <- params)
              yield typeToScala(tpe)
          val targs = paramTypes :+ typeToScala(resultType)

          TypeRef(QualifiedName.Function(params.size), targs)
        }

      case RepeatedType(underlying) => 
        TypeRef(Name.REPEATED, List(typeToScala(underlying)))

      case _ =>
        // ???
        TypeRef.Any
    }
  }

  private def coreTypeToScala(tpe: CoreType,
      anyAsDynamic: Boolean = false): TypeRef = {

    tpe.name match {
      case "any"     => if (anyAsDynamic) TypeRef.Dynamic else TypeRef.Any
      case "dynamic" => TypeRef.Dynamic
      case "void"    => TypeRef.Unit
      case "number"  => TypeRef.Number
      case "bool"    => TypeRef.Boolean
      case "boolean" => TypeRef.Boolean
      case "string"  => TypeRef.String
    }
  }
}

object Importer {
	
	import com.apyx.scala.ts2scala.dialect._
	
	var dialect:Dialect = DefaultDialect
	
	private val AnyType = TypeRefTree(CoreType("any"))
	private val DynamicType = TypeRefTree(CoreType("dynamic"))

	private implicit class OptType(val optType: Option[TypeTree]) extends AnyVal {
		@inline def orAny: TypeTree = optType.getOrElse(AnyType)
		@inline def orDynamic: TypeTree = optType.getOrElse(DynamicType)
	}

	private object TypeOrAny {
		@inline def unapply(optType: Option[TypeTree]) = Some(optType.orAny)
	}

	private object IdentName {
		@inline def unapply(ident: Ident) =
				Some(Name(ident.name))
	}

	private object TypeNameName {
		@inline def apply(typeName: Name) =
				TypeName(typeName.name)
		@inline def unapply(typeName: TypeName) =
				Some(Name(typeName.name))
	}
	
	private object PropertyNameName {
		@inline def unapply(propName: PropertyName) =
				Some(Name(escapeApply(propName.name)))
	}

	private def escapeApply(ident: String) =
			if (ident == "apply") "$apply" else ident
}
