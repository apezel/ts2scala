# ts2scala

Typescript importer based on « Importer from TypeScript type definitions to Scala.js »

This tool reads type definitions files written for
[TypeScript](http://www.typescriptlang.org/) (.d.ts files) and use macros to generate the corresponding scala tree

Thank to Sébastien Doeraene for the parser's code and to Eugene Burmako for his precious help

Example of use :
----------------

@TypeScripted(file="/Users/home/scala/projects/ts2scala/typescripts/global.d.ts")
object Global { }

@TypeScripted(file="/Users/home/scala/projects/ts2scala/typescripts/jquery-ori.d.ts")
object JQuery { 
	import com.apyx.scala.ts2scala.Global._
}

@TypeScripted(file="/Users/home/scala/projects/ts2scala/typescripts/angular-1.0.d.ts")
object Angular { 
	import com.apyx.scala.ts2scala.Global._
	import com.apyx.scala.ts2scala.JQuery._
}

@TypeScripted(file="/Users/home/scala/projects/ts2scala/typescripts/bootstrap.d.ts")
object Bootstrap { 
	import com.apyx.scala.ts2scala.Global._
	import com.apyx.scala.ts2scala.JQuery._
}

@TypeScripted(file="/Users/home/scala/projects/ts2scala/typescripts/backbone.d.ts")
object Backbone { 
	import com.apyx.scala.ts2scala.Global._
	import com.apyx.scala.ts2scala.JQuery._
}


object Main {
	
	def main(args:Array[String]) {
		import com.apyx.scala.ts2scala.JQuery.jquery._
		import com.apyx.scala.ts2scala.Angular._
		import com.apyx.scala.ts2scala.Bootstrap._
		import com.apyx.scala.ts2scala.Backbone._
		
		$("div").blur()
	}
	
}


Arnaud PEZEL
[Apyx](http://www.apyx.fr/)
