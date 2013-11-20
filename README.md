# ts2scala

Typescript importer based on « Importer from TypeScript type definitions to Scala.js »

This tool reads type definitions files written for
[TypeScript](http://www.typescriptlang.org/) (.d.ts files) and use macros to generate the corresponding scala tree

Thank to Sébastien Doeraene for the parser's code and to Eugene Burmako for his precious help

There's also a [JScala](https://github.com/nau/jscala/) connector

Example of use :
----------------
```
@TS2Scala(file="/path/to/typescripts/global.d.ts")
object Global { }

@TS2Scala(file="/path/to/typescripts/jquery-ori.d.ts")
object JQuery { 
	import Global._
}

@TS2Scala(file="/path/to/typescripts/angular-1.0.d.ts")
object Angular { 
	import Global._
	import JQuery._
}

@TS2Scala(file="/path/to/typescripts/bootstrap.d.ts")
object Bootstrap { 
	import Global._
	import JQuery._
}

@TS2Scala(file="/path/to/typescripts/backbone.d.ts")
object Backbone { 
	import Global._
	import JQuery._
}


object Main {
	
	def main(args:Array[String]) {
		import JQuery.jquery._
		
		$("div").blur()
	}
	
}
```

SBT :
-----
```
libraryDependencies += ("fr.apyx" %% "ts2scala-macros" % "0.2.1")
```

Arnaud PEZEL
[Apyx](http://www.apyx.fr/)
