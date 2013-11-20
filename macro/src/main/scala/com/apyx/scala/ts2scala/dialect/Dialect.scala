/* Typescript to scala macro tree builder
 * Copyright 2013 Apyx
 * @author  Arnaud PEZEL
 */

package com.apyx.scala.ts2scala.dialect

import com.apyx.scala.ts2scala.definition.{QualifiedName, Name}

trait Dialect {
	
	def dictionnary():Map[Name, QualifiedName] = ???
	
}

object DefaultDialect extends Dialect {
	
	override def dictionnary():Map[Name, QualifiedName] = {
		return Map()
	}
	
}

	
