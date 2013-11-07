/* TypeScript importer for Scala.js
 * Copyright 2013 LAMP/EPFL
 * @author  Sebastien Doeraene
 */

package scala.tools.scalajs.tsimporter

import java.io.{ Console => _, Reader => _, _ }
import scala.reflect.macros.Context
import scala.collection.immutable.PagedSeq
import Trees._
import scala.util.parsing.input._
import parser.TSDefParser
import scala.tools.scalajs.tsimporter.sc.PackageSymbol

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
