package com.apyx.scala.ts2scala

import com.apyx.scala.ts2scala.macros.TS2Scala

@TS2Scala(file="typescripts/global.d.ts")
object Global { }

@TS2Scala(file="typescripts/jquery-ori.d.ts")
object JQuery {
	import Global._
}

@TS2Scala(file="typescripts/angular-1.0.d.ts")
object Angular { 
	import Global._
	import JQuery._
}

@TS2Scala(file="typescripts/bootstrap.d.ts")
object Bootstrap { 
	import Global._
	import JQuery._
}

@TS2Scala(file="typescripts/backbone.d.ts")
object Backbone { 
	import Global._
	import JQuery._
}

@TS2Scala(file="typescripts/mongodb.d.ts")
object MongoDB { 
	import Global._
}


object Main {
	
	def main(args:Array[String]) {
		import JQuery.jquery._

		$("div").blur()
	}
	
	def myFunc[U](args:Any*):JQuery.JQueryGenericPromise[U] = { null }
	
}
