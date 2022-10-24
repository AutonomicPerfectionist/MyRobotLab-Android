package org.myrobotlab.kotlin.framework

import org.myrobotlab.kotlin.utils.BiMap


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MrlClassMapping(val javaClass: String)

expect val mappings: BiMap<String, String>