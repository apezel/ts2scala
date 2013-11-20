/* TypeScript importer for Scala.js
 * Copyright 2013 LAMP/EPFL
 * @author  Sebastien Doeraene
 */

package com.apyx.scala.ts2scala.ts.importer

import java.io.{ Console => _, Reader => _, _ }
import scala.reflect.macros.Context
import scala.collection.immutable.PagedSeq
import Trees._
import scala.util.parsing.input._
import com.apyx.scala.ts2scala.ts.parser.TSDefParser
import com.apyx.scala.ts2scala.definition._

/** Entry point for the TypeScript importer of Scala.js */
object Main {
  
  def typescript2scala(inputFileName:String) = {
      
  	process( parseDefinitions(readerForFile(inputFileName)) )
  			
  }

  private def process(definitions: List[DeclTree]):PackageSymbol = {
    (new Importer()(definitions)).rootPackage
  }

  private def parseDefinitions(reader: Reader[Char]): List[DeclTree] = {
    val parser = new TSDefParser
    parser.parseDefinitions(reader) match {
      case parser.Success(rawCode, _) =>
        rawCode

      case parser.NoSuccess(msg, next) =>
        Console.err.println(
            "Parse error at %s\n".format(next.pos.toString) +
            msg + "\n" +
            next.pos.longString)
        sys.exit(2)
    }
  }

  /** Builds a [[scala.util.parsing.input.PagedSeqReader]] for a file
   *
   *  @param fileName name of the file to be read
   */
  private def readerForFile(fileName: String) = {
    new PagedSeqReader(PagedSeq.fromReader(
        new BufferedReader(new FileReader(fileName))))
  }
  
  
  abstract class Test[U,T] { }
}

object TypeRegister {
	
	import com.apyx.scala.ts2scala.definition._

	protected val register:scala.collection.mutable.Map[String, ClassSymbol] = scala.collection.mutable.Map()
	
	def put(sym:ClassSymbol) = {
		register.put(sym.qualifiedName.toString, sym)
	}
	
	def get(lookupPath:QualifiedName, name:QualifiedName):Option[ClassSymbol] = {
		
		var sumPath = ""
		lookupPath.parts.foreach { x => sumPath += x+"."; register.get(sumPath+name.toString) match {
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
