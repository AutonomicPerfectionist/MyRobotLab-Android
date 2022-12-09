package org.myrobotlab.kotlin.service.data

import kotlin.jvm.JvmInline

/**
 * Represents some value in Hertz,
 * or cycles per second.
 */
@JvmInline
value class Hertz(val value: Double)

/**
 * Represents a number as [Hertz],
 * or cycles per second.
 */
val Number.hz: Hertz
    get() = Hertz(this.toDouble())

/**
 * Returns the reciprocal of this rate,
 * i.e. the period, in [Second]s per cycle.
 */
val Hertz.period: Second
    get() = Second(1.0 / this.value)