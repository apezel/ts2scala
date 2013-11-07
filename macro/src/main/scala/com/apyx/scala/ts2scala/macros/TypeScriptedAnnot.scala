package com.apyx.scala.ts2scala.macros

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.Context
import scala.tools.scalajs.tsimporter.Main


object TypeScriptedMacro {
	def annot_impl(c:Context)(annottees:c.Expr[Any]*): c.Expr[Any] = {
		import c.universe._
			
	    def extractArgValue(args:List[Tree], name:String):Any = {
				
				for (arg <- args) arg match {
					case x:AssignOrNamedArg if (x.lhs.toString == name) => return x.rhs.toString
					case _ => None
				}
				
				None
		}
		
	    val inputFileName = c.macroApplication.children(0) match {
			case q"new TypeScripted(..$args).macroTransform" => extractArgValue(args, "file") match {
				case None => throw new Exception("please specify a file argument")
				case x:String => x.replaceAll("\"", "") 
			}
		}
	    
	    def insertTree(moddef:ModuleDef):Tree = {
			
	    	val q"object $name extends ..$ext { ..$body }" = moddef
			
			val rootPackage = Main.typescript2scala(inputFileName)
			
			var oBody = body ++ new TreeBuilder(c).symbolToTree(rootPackage).asInstanceOf[List[Tree]]
			
			println(oBody)
	    
			q"object $name extends ..$ext { ..$oBody }"
			
		}
	    	
	    val inputs = annottees.map(_.tree).toList
	    val (annottee, expandees) = inputs match {
			case (x:ModuleDef) :: rest => (EmptyTree, insertTree(x) +: rest)
			case x:Any => println("other"); (EmptyTree, x)
	    }
	    
		return c.Expr[Any](Block(expandees, Literal(Constant(()))))
		
	}
	
}

class TypeScripted extends StaticAnnotation {
	def macroTransform(annottees: Any*) = macro TypeScriptedMacro.annot_impl
}