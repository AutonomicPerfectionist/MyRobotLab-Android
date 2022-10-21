package org.myrobotlab.kotlin.framework

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MrlClassMapping(val javaClass: String)