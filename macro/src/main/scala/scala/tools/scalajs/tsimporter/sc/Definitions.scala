/* TypeScript importer for Scala.js
 * Copyright 2013 LAMP/EPFL
 * @author  Sbastien Doeraene
 * Modified by Arnaud PEZEL, Apyx, 2013
 */
package scala.tools.scalajs.tsimporter.sc

import scala.language.implicitConversions

import scala.collection.mutable._

import scala.tools.scalajs.tsimporter.Utils

case class Name(name: String) {
  override def toString() = Utils.scalaEscape(name)
}

object Name {
  val scala = Name("scala")
  val js = Name("js")

  val EMPTY = Name("")
  val CONSTRUCTOR = Name("<init>")
  val REPEATED = Name("*")
  val FUNCTION = Name("Function")
}

case class QualifiedName(parts: Name*) {
  
	var arity = 0

	def isRoot = parts.isEmpty

	override def toString() =
		if (isRoot) "_root_" else parts.mkString(".")

	def dot(name: Name) = QualifiedName((parts :+ name):_*)
	def init = QualifiedName(parts.init:_*)
	def last = parts.last
	
}

object QualifiedName {
  implicit def fromName(name: Name) = QualifiedName(name)

  val Root = QualifiedName()
  val scala = Root// dot Name.scala
  val scala_js = Root//scala dot Name.js

  val Array = scala_js dot Name("Array")
  val FunctionBase = scala_js dot Name("Function")
  
  def Function(arity: Int) = { var ret = QualifiedName(Name("Function")); ret.arity = arity; ret }
}

class Symbol(val name: Name) {

	var owner:ContainerSymbol = null
	
  override def toString() =
    s"${this.getClass.getSimpleName}($name)}"
}

class CommentSymbol(val text: String) extends Symbol(Name("<comment>")) {
  override def toString() =
    s"/* $text */"
}

class ContainerSymbol(nme: Name) extends Symbol(nme) {
  val members = new ListBuffer[Symbol]

  private var _anonMemberCounter = 0
  def newAnonMemberName() = {
    _anonMemberCounter += 1
    "anon$" + _anonMemberCounter
  }

  def findClass(name: Name): Option[ClassSymbol] = {
    members.collectFirst {
      case sym: ClassSymbol if sym.name == name => sym
    }
  }

  def findModule(name: Name): Option[ModuleSymbol] = {
    members.collectFirst {
      case sym: ModuleSymbol if sym.name == name => sym
    }
  }
  
  def hasMethodWithSameSignatureThan(method0:MethodSymbol):TypeMatch = { //None should be interpreted as maybe => depends of type erasure
  	
  	members.foreach { _ match {
  		case sym:MethodSymbol =>
  			sym.hasSameSignatureThan(method0) match {
  				case x @ (TypeMatch.YES | TypeMatch.MAYBE) => return x
  				case _ => /* nothing */
  			}
  		case _ => /* nothing*/
  		}
  	}
  	
  	TypeMatch.NO
  	
  }
  
  def findFieldRef(name: Name): Option[FieldSymbol] = {
  	var s = this
  	while (s != null)
  	{
	  	s.members.collectFirst {
	  		case sym: FieldSymbol if sym.name == name => return Some(sym)
	  	}
	  	
	  	s = s.owner
  	}
  	
  	return None
  	
  }

  def getClassOrCreate(name: Name): ClassSymbol = {
    findClass(name) getOrElse {
      val result = new ClassSymbol(name)
      result.owner = this
      members += result
      findModule(name) foreach { companion =>
        result.companionModule = companion
        companion.companionClass = result
      }
      result
    }
  }

  def getModuleOrCreate(name: Name): ModuleSymbol = {
    findModule(name) getOrElse {
      val result = new ModuleSymbol(name)
      result.owner = this
      members += result
      findClass(name) foreach { companion =>
        result.companionClass = companion
        companion.companionModule = result
      }
      result
    }
  }
  
  def getExportFieldRefOrAbort(name: Name): FieldSymbol = {
  	val fieldSymbol = findFieldRef(name) getOrElse {
  		throw new Exception("Unable to find declared field "+name.name)
  	}
  	
  	if (!members.contains(fieldSymbol))
  		members += fieldSymbol
  		
  	fieldSymbol
  }
  

  def newField(name: Name): FieldSymbol = {
    val result = new FieldSymbol(name)
    result.owner = this
    members += result
    result
  }

  def newMethod(name: Name): MethodSymbol = {
    val result = new MethodSymbol(name)
    result.owner = this
    members += result
    result
  }
  
  def addSymbol(sym:Symbol) = {
	sym.owner = this
	members += sym
  }
}

class PackageSymbol(nme: Name) extends ContainerSymbol(nme) {
  override def toString() = s"package $name"

  def findPackage(name: Name): Option[PackageSymbol] = {
    members.collectFirst {
      case sym: PackageSymbol if sym.name == name => sym
    }
  }

  def getPackageOrCreate(name: Name): PackageSymbol = {
    findPackage(name) getOrElse {
      val result = new PackageSymbol(name)
      result.owner = this
      members += result
      result
    }
  }
}

class ClassSymbol(nme: Name) extends ContainerSymbol(nme) {
  val tparams = new ListBuffer[TypeParamSymbol]
  val parents = new ListBuffer[TypeRef]
  var companionModule: ModuleSymbol = _
  var isTrait: Boolean = true

  override def toString() = (
      (if (isTrait) s"trait $name" else s"class $name") +
      (if (tparams.isEmpty) "" else tparams.mkString("<", ", ", ">")))
}

class ModuleSymbol(nme: Name) extends ContainerSymbol(nme) {
  var companionClass: ClassSymbol = _

  override def toString() = s"object $name"
}

class FieldSymbol(nme: Name) extends Symbol(nme) {
  var tpe: TypeRef = TypeRef.Any

  override def toString() = s"var $name: $tpe"
}


class MethodSymbol(nme: Name) extends Symbol(nme) {
  val tparams = new ListBuffer[TypeParamSymbol]
  val params = new ListBuffer[ParamSymbol]
  var resultType: TypeRef = TypeRef.Dynamic

  var jsName: Option[String] = None
  var isBracketAccess: Boolean = false
  
  def hasSameSignatureThan(method0:MethodSymbol):TypeMatch = {
  	
		  TypeMatch(name == method0.name && params.size == method0.params.size) match {
		  	
		  	case x @ TypeMatch.YES =>
		  		var ret = x
		  		for (i <- 0 to params.size - 1) 
		  			ret = ret :+ (params(0) hasSameSignatureThan method0.params(0))
		  		ret
		  	case x @ _ => x
		  	
		  }
  }

}

class TypeParamSymbol(nme: Name, val upperBound: Option[TypeRef]) extends Symbol(nme) {
  override def toString() = {
    nme.toString + upperBound.fold("")(bound => s" <: $bound")
  }
}


case class TypeMatch(v:Int) {
	
	def :+(p:TypeMatch):TypeMatch = {
		(p, this) match {
			case (TypeMatch.NO, _) => TypeMatch.NO
			case (_, TypeMatch.NO) => TypeMatch.NO
			case (TypeMatch.YES, TypeMatch.YES) => TypeMatch.YES 
			case (_, _) => p
		}
	}
}

object TypeMatch {
	
	def apply(b:Boolean):TypeMatch = {
		if (b) return TypeMatch.YES else return TypeMatch.NO 
	}
	
	val UNKNOW = TypeMatch(-1)
	val NO = TypeMatch(0)
	val YES = TypeMatch(1)
	val MAYBE = TypeMatch(2)
	
}

class ParamSymbol(nme: Name) extends Symbol(nme) {
  def this(nme: Name, tpe: TypeRef) = {
    this(nme)
    this.tpe = tpe
  }

  var optional: Boolean = false
  var tpe: TypeRef = TypeRef.Any
  
  def hasSameSignatureThan(param0:ParamSymbol):TypeMatch = {
  	
  	return tpe hasSameSignatureThan param0.tpe
  	
  }
  
  override def toString() =
    s"$name: $tpe" + (if (optional) " = _" else "")
}

case class TypeRef(var typeName: QualifiedName, var targs: List[TypeRef] = Nil) {
  
	def hasSameSignatureThan(tpe0:TypeRef):TypeMatch = {
		
		(this, tpe0) match {
			case (x, y) if x.toExtendedString == y.toExtendedString => TypeMatch.YES //extactly match
			case (x, y) if x.toString == y.toString => TypeMatch.MAYBE //match after type erasure
			case (TypeRef.Any, _) | (_, TypeRef.Any) => TypeMatch.MAYBE //any match every type
			case (TypeRef.Repeated(u1), TypeRef.Repeated(u2)) => TypeMatch.MAYBE //repeated types match
			case (_, _) => TypeMatch.NO //don't match
		}
	}
	
	def toExtendedString:String = {
		
		this match {
			
			case TypeRef(qn, tagrs0) if qn.toString == "Function" && qn.arity > 0 =>
				s"Function${qn.arity}[${targs.mkString(", ")}]"
	  				
	  		case TypeRef(typeName, Nil) =>
	  			s"$typeName"
	
	  		case TypeRef.Repeated(underlying) =>
	  			s"$underlying"
	  			
	  		case _ =>
	  			s"$typeName[${targs.mkString(", ")}]"
	    }
	
	}
	
	override def toString():String = {

	  	this match {
	  		
	  		case TypeRef(typeName, Nil) =>
	  			s"$typeName"
	
	  		case TypeRef.Repeated(underlying) =>
	  			s"$underlying"
	  			
	  		case TypeRef(qn, targs) if typeName == "Function" =>
	  			s"$typeName"+qn.arity
	  			
	  		case _ =>
	  			s"$typeName"
	    }

  }
  
}

object TypeRef {
  
  val Any = TypeRef(Name("Any"))
  val Dynamic = TypeRef(Name("Dynamic"))
  val Number = TypeRef(Name("Number"))
  val Boolean = TypeRef(Name("Boolean"))
  val String = TypeRef(Name("String"))
  val Object = TypeRef(Name("Object"))
  val Unit = TypeRef(Name("Unit"))
  val Function = TypeRef(Name("Function"))

  object Repeated {
	  def apply(underlying: TypeRef): TypeRef =
			  TypeRef(QualifiedName(Name.REPEATED), List(underlying))
	  
	  def unapply(typeRef: TypeRef) = typeRef match {
	  	case TypeRef(QualifiedName(Name.REPEATED), List(underlying)) =>
	  	Some(underlying)
	  
	  	case _ => None
	  }
  }
  
}
