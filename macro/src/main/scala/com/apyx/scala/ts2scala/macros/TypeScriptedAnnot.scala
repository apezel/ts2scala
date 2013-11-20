package com.apyx.scala.ts2scala.macros

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.Context
import com.apyx.scala.ts2scala.ts.importer._
import com.apyx.scala.ts2scala.dialect._

object TS2Scala {
	
	def impl(c:Context)(annottees:c.Expr[Any]*): c.Expr[Any] = _impl(DefaultDialect)(c)(annottees:_*)
	
	def _impl(dialect:Dialect)(c:Context)(annottees:c.Expr[Any]*): c.Expr[Any] = {
		import c.universe._
		
		Importer.dialect = dialect
		
		def extractArgValue(args:List[Tree], name:String):Option[Tree] = {

				for (arg <- args) arg match {
					case x:AssignOrNamedArg if (x.lhs.toString == name) => return Some(x.rhs)
					case _ => None
				}
				
				None
		}
		
	    val inputFileName = c.macroApplication.children(0) match {
			case q"new $name(..$args).macroTransform" => extractArgValue(args, "file") match {
				case None => c.abort(c.enclosingPosition, "Please specify a file argument")
				case Some(x:Tree) => x.toString.replaceAll("\"", "") 
			}
		}
	    
	    def insertTree(moddef:ModuleDef):Tree = {
			
	    	val q"object $name extends ..$ext { ..$body }" = moddef
			
			val rootPackage = Main.typescript2scala(inputFileName)
			
			var oBody = body ++ new TreeBuilder(c).symbolToTree(rootPackage).asInstanceOf[List[Tree]]
	    
			q"object $name extends ..$ext { ..$oBody }"
			
		}
	    	
	    val inputs = annottees.map(_.tree).toList
	    val (annottee, expandees) = inputs match {
			case (x:ModuleDef) :: rest => (EmptyTree, insertTree(x) +: rest)
			case _ => c.abort(c.enclosingPosition, "Typescript annotation is only supported on objects")
	    }
	    
		return c.Expr[Any](Block(expandees, Literal(Constant(()))))
		
	}
	
}

class TS2Scala(file:String) extends StaticAnnotation {
	def macroTransform(annottees: Any*) = macro TS2Scala.impl
}