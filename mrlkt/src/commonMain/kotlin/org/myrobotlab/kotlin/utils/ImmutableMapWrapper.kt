package org.myrobotlab.kotlin.utils

import kotlin.jvm.JvmInline


/**
 * Provides a simple wrapper around any map that only
 * exposes the immutable methods of the wrapped map.
 * This is different from [Map.toMap] in that no copy is
 * made, and it's different from simply
 * setting the variable type as [Map] since
 * it could still be downcasted back to the mutable variant.
 */
@JvmInline
value class ImmutableMapWrapper<K, V>(private val map: Map<K, V>): Map<K, V> by map