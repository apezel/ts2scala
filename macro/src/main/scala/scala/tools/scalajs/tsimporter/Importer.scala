/* Modified by Arnaud PEZEL, Apyx, 2013*/
package scala.tools.scalajs.tsimporter

import Trees.{ TypeRef => TypeRefTree, _ }
import sc._
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
  
  /* added : need to parse every current level declaration before to go deeper. Flattenize process */
  private object ProcessQueue
  {
	  	private var processDeclQueue:List[() => Unit] = List()
		def push(f:() => Unit) = { processDeclQueue = processDeclQueue :+ f;  }
  	
		def run():Unit = {
			while (processDeclQueue.size > 0)
			{
				processDeclQueue(0)() //processDecl(processDeclQueue(0)._1, processDeclQueue(0)._2);
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
          ProcessQueue.push { () => /*println(innerDecl);*/ processDecl(sym, innerDecl) }

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
        processMembersDecls(owner, sym, members)

      case VarDecl(IdentName(name), TypeOrAny(tpe)) =>
        val sym = owner.newField(name.asInstanceOf[Name])
        sym.tpe = typeToScala(tpe)

      case FunctionDecl(IdentName(name), signature) =>
        processDefDecl(owner, Name(name), signature)
        
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
      case CallMember(signature) =>
        processDefDecl(owner, Name("apply"), signature)

      case ConstructorMember(sig @ FunSignature(tparamsIgnored, params, Some(resultType)))
      if owner.isInstanceOf[ModuleSymbol] && resultType == companionClassRef =>
        val classSym = enclosing.getClassOrCreate(owner.name)
        classSym.isTrait = false
        processDefDecl(classSym, Name.CONSTRUCTOR,
            FunSignature(Nil, params, Some(TypeRefTree(CoreType("void")))))

      case PropertyMember(PropertyNameName(name), opt, tpe) =>
        if (name.asInstanceOf[Name].name != "prototype") {
          val sym = owner.newField(name.asInstanceOf[Name])
          sym.tpe = typeToScala(tpe)
        }

      case FunctionMember(PropertyNameName(name), opt, signature) =>
        processDefDecl(owner, name.asInstanceOf[Name], signature)

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

  private def processDefDecl(owner: ContainerSymbol, name: Name,
      signature: FunSignature) {
    // Discard specialized signatures
    if (signature.params.exists(_.tpe.exists(_.isInstanceOf[ConstantType])))
      return

    for (sig <- makeAlternatives(signature)) {
      
    	val sym = new MethodSymbol(name)

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
      
      def appendMethodSymbol(ms:MethodSymbol):Unit = {
	      owner hasMethodWithSameSignatureThan ms match {
	      	case TypeMatch.NO => owner.addSymbol(ms)
	      	case TypeMatch.MAYBE =>
	      		val nms = new MethodSymbol(Name("_"+ms.name.toString))
	      		nms.params ++= ms.params
	      		nms.tparams ++= ms.tparams
	      		nms.resultType = ms.resultType
	      		appendMethodSymbol(nms)
	      	case TypeMatch.YES => /* nothing */
	      	case _ => throw new Exception("unable to compare methods")
	      }
      }
      
      appendMethodSymbol(sym)
      		
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
        val baseTypeRef = base match {
          case TypeName("Array") => QualifiedName.Array
          case TypeName("Function") => QualifiedName.FunctionBase
          case TypeName("Element") => QualifiedName(Name("Any")) //todo
          case TypeNameName(name) => QualifiedName(name.asInstanceOf[Name])
          case QualifiedTypeName(qualifier, TypeNameName(name)) =>
            val qual1 = qualifier map (x => Name(x.name))
            QualifiedName((qual1 :+ Name(name)): _*)
          case _: CoreType => throw new MatchError(base)
        }
        TypeRef(baseTypeRef, targs map typeToScala)

      case ObjectType(members) =>
        // ???
        TypeRef.Any

      case FunctionType(FunSignature(tparams, params, Some(resultType))) =>
        if (!tparams.isEmpty) {
          // Type parameters in function types are not supported
          TypeRef.Function
        //} else if (params.exists(_.tpe.exists(_.isInstanceOf[RepeatedType]))) {
          // Repeated params in function types are not supported
        //  TypeRef.Function
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
