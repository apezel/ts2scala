package com.apyx.scala.ts2scala

import com.apyx.scala.ts2scala.macros.TypeScripted

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

@TypeScripted(file="/Users/home/scala/projects/ts2scala/typescripts/mongodb.d.ts")
object MongoDB { 
	import com.apyx.scala.ts2scala.Global._
}


object Main {
	
	def main(args:Array[String]) {
		import com.apyx.scala.ts2scala.JQuery.jquery._
		import com.apyx.scala.ts2scala.Angular._
		import com.apyx.scala.ts2scala.Bootstrap._
		import com.apyx.scala.ts2scala.Backbone._
		import com.apyx.scala.ts2scala.MongoDB._
		$("div")
	}
	
	def myFunc[U](args:Any*):JQuery.JQueryGenericPromise[U] = { null }
	
}