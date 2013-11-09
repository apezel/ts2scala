package com.apyx.scala.ts2scala

import com.apyx.scala.ts2scala.macros.TypeScripted

@TypeScripted(file="/Users/home/scala/projects/ts2scala/typescripts/jquery-mod.d.ts")
object JQuery { }

@TypeScripted(file="/Users/home/scala/projects/ts2scala/typescripts/angular-1.0.d.ts")
object Angular { 
	import com.apyx.scala.ts2scala.JQuery._
}


object Main {
	
	def main(args:Array[String]) {
		var a = JQuery.jquery.$.ajax("hello").___then( myFunc[Int] )
	}
	
	def myFunc[U](args:Any*):JQuery.JQueryGenericPromise[U] = { null }
	
}