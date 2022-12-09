package org.myrobotlab.kotlin.service.data

import kotlin.jvm.JvmInline

/**
 * Represents a number of microseconds
 */
@JvmInline
value class MicroSecond(val value: Int)

val Int.us: MicroSecond
    get() = MicroSecond(this)


/**
 * Represents a number of milliseconds
 */
@JvmInline
value class Millisecond(val value: Double)

val Number.ms: Millisecond
    get() = Millisecond(this.toDouble())


/**
 * Converts a number of milliseconds to their
 * equivalent number of microseconds, truncating
 * any leftover decimal places.
 */
fun Millisecond.toMicroseconds() = MicroSecond((this.value * 1000).toInt())


@JvmInline
value class Second(val value: Double)

/**
 * Convert seconds to the equivalent milliseconds
 */
fun Second.toMilliseconds() = Millisecond(this.value * 1000)

/**
 * Convert seconds to the equivalent microseconds, truncating
 * any leftover decimal places.
 */
fun Second.toMicroseconds() = MicroSecond((this.toMilliseconds().value * 1000).toInt())