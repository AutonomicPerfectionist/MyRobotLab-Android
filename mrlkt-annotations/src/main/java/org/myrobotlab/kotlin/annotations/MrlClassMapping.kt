package org.myrobotlab.kotlin.annotations

/**
 * Marks the annotated class as being equivalent
 * to the specified Java MyRobotLab class.
 *
 * Marked classes will automatically be serialized
 * and deserialized into the correct representation
 * so long as both the Kotlin and Java versions
 * of the class have compatible properties. This is handled
 * by [JsonSerde].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MrlClassMapping(val javaClass: String)