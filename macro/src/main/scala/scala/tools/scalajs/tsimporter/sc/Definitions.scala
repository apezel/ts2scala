/* TypeScript importer for Scala.js
 * Copyright 2013 LAMP/EPFL
 * @author  Sbastien Doeraene
 * Modified by Arnaud PEZEL, Apyx, 2013
 */
package scala.tools.scalajs.tsimporter.sc

import scala.language.implicitConversions

import scala.collection.mutable._

import scala.tools.scalajs.tsimporter.Utils


object ClassRegister {
	
	protected val register:Map[String, ClassSymbol] = Map()
	
	def put(sym:ClassSymbol) = {
		println("put "+sym.qualifiedName.toString)
		register.put(sym.qualifiedName.toString, sym)
	}
	
	def get(lookupPath:QualifiedName, name:QualifiedName):Option[ClassSymbol] = {
		
		var sumPath = ""
		lookupPath.parts.foreach { x => sumPath += x+"."; println("get "+sumPath+name.toString+" = "+register.get(sumPath+name.toString)); register.get(sumPath+name.toString) match {
				case y @ Some(sym) => return y
				case _ => /* nothing */
			}
		}
		
		None
	}
	
	put(AnyClassSymbol())
	
	object AnyClassSymbol {
	  
  		def apply():ClassSymbol = {
  			var sym:ClassSymbol = new ClassSymbol(Name("Any"))
  			
			sym.members ++= ("clone" :: "notify" :: "notifyAll" :: "toString" :: "wait" :: Nil).map {
  				x => val m = new MethodSymbol(Name(x));
  					m.exported = true;
  					m
  				}

  			sym.members ++= ("wait" :: Nil).map {
  				x => val m = new MethodSymbol(Name(x));
  					val param = new ParamSymbol(Name("param"));
  					param.tpe = TypeRef(QualifiedName(Name("Long")))
  					m.params ++= param :: Nil
  					m.exported = true;
  					m
  				}

  			sym.exported = true
  			
  			sym
  		}
  		
  }
	
}


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

	def init = QualifiedName(parts.init:_*)
	def last = parts.last
	
	def dot(name: Name) = QualifiedName((parts :+ name):_*)
	
	def +(qn:QualifiedName):QualifiedName = {
		QualifiedName((parts ++ qn.parts):_*)
	}
	
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

class Symbol(var name: Name) {

	protected var _owner:ContainerSymbol = _
	def owner:ContainerSymbol = _owner
	def owner_=(v:ContainerSymbol) = { _owner = v }
	
	var exported:Boolean = false

	def qualifiedName = {
		
		var o = owner
		var parts:Seq[Name] = Seq()

		if (owner != null)
		{
			while (o != null)
			{
				parts =  o.name +: parts
				o = o.owner
			}
		}
		else
			parts = Seq(Name(""))
		
		QualifiedName((parts :+ name):_*)
	}
	
  override def toString() =
    s"${this.getClass.getSimpleName}($name)}"
}

class CommentSymbol(val text: String) extends Symbol(Name("<comment>")) {
  override def toString() =
    s"/* $text */"
}

class ContainerSymbol(nme: Name) extends Symbol(nme) {
  var members = new ListBuffer[Symbol]

  private var _anonMemberCounter = 0
  def newAnonMemberName() = {
    _anonMemberCounter += 1
    "anon$" + _anonMemberCounter
  }
  
  def addSymbol(sym:Symbol) = {
	sym.owner = this
	members += sym

  }
  
  def replaceSymbol(sym:Symbol, nsym:Symbol) = {
  		nsym.owner = this
  		members.update(members.indexOf(sym), nsym)
  }
  
  def dropSymbol(sym:Symbol) = {
  	members -= (sym)
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
  
  def hasSymbolWithSameSignatureThan(sym:DoubleErasure):TypeMatch = { //None should be interpreted as maybe => depends of type erasure
  	
  	members.foreach { _ match {
  		case x:DoubleErasure if !(x eq sym) && x.exported =>
  			sym hasSameSignatureThan x match {
  				case y @ (TypeMatch.YES | TypeMatch.MAYBE) => return y
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
  var isTrait: Boolean = false
  
  override def owner_=(v:ContainerSymbol) = { _owner = v; ClassRegister.put(this) }
  
  protected def inheritedMembers():ListBuffer[Symbol] = {
		
		val members0 = new ListBuffer[Symbol] ++ members
		
		if (owner != null)
		{
			(parents :+ TypeRef(Name("Any"))).foreach { tpe => 
		  		ClassRegister.get(owner.qualifiedName, tpe.typeName) match {
		  			case Some(sym:ClassSymbol) => members0 ++= sym.inheritedMembers
		  			case None => /* nothing */
		  		}
			}
		}
		
		members0
  }
  
  override def hasSymbolWithSameSignatureThan(sym:DoubleErasure):TypeMatch = {
  	
  	inheritedMembers.foreach { _ match {
  		case x:DoubleErasure if !(x eq sym) && x.exported =>
  			sym hasSameSignatureThan x match {
  				case y @ (TypeMatch.YES | TypeMatch.MAYBE) => return y
  				case _ => /* nothing */
  			}
  		case _ => /* nothing*/
  		}
  	}
  	
  	TypeMatch.NO
  	
  }

  override def toString() = (
      (if (isTrait) s"trait $name" else s"class $name") +
      (if (tparams.isEmpty) "" else tparams.mkString("<", ", ", ">")))
}

class ModuleSymbol(nme: Name) extends ContainerSymbol(nme) {
  var companionClass: ClassSymbol = _

  override def toString() = s"object $name"
}

trait DoubleErasure extends Symbol {

	type T
	type U <: DoubleErasure
	
	def doubleSafeSymbol():Option[T]

	def hasSameSignatureThan[U](sym0:U):TypeMatch
	
}

class FieldSymbol(nme: Name) extends Symbol(nme) with DoubleErasure {
	
	type T = FieldSymbol
	
	var tpe: TypeRef = TypeRef.Any

	def doubleSafeSymbol():Option[FieldSymbol] = {
			owner hasSymbolWithSameSignatureThan this match {
				case TypeMatch.MAYBE | TypeMatch.YES => None
				case TypeMatch.NO => Some(this)
			}
	}

	def hasSameSignatureThan[U](sym0:U):TypeMatch = {

			return sym0 match {
				case x:FieldSymbol => TypeMatch(name == x.name) :+ (tpe hasSameSignatureThan x.tpe)
				case x:MethodSymbol => TypeMatch(name == x.name)
				case _ => TypeMatch.NO
			}
	}

	override def toString() = s"var $name: $tpe"

}

class MethodSymbol(nme: Name) extends Symbol(nme) with DoubleErasure {
	
	type T = DoubleErasure
	
	val tparams = new ListBuffer[TypeParamSymbol]
	val params = new ListBuffer[ParamSymbol]
	var resultType: TypeRef = TypeRef.Dynamic
	var isStatic:Boolean = false

	var jsName: Option[String] = None
	var isBracketAccess: Boolean = false
	
	def doubleSafeSymbol():Option[MethodSymbol] = {
		owner hasSymbolWithSameSignatureThan this match {
	      	case TypeMatch.MAYBE =>
	      		this.name = Name(this.name.name+"_")
	      		this.doubleSafeSymbol()
	      	case TypeMatch.YES => None 
	      	case TypeMatch.NO => Some(this)
	      	case _ => throw new Exception("unable to compare methods")
		}
	}

	def hasSameSignatureThan[U](sym0:U):TypeMatch = {

		return sym0 match {
				case x:MethodSymbol => 
					
					TypeMatch(name == x.name && params.size == x.params.size) match {
	
						case y @ TypeMatch.YES =>
							var ret = y
							for (i <- 0 to params.size - 1) 
								ret = ret :+ (params(0) hasSameSignatureThan x.params(0))
							ret
						case y @ _ => y
				
					}
					
				case _ => TypeMatch.NO
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


