package org.myrobotlab.kotlin.utils

import kotlin.jvm.JvmInline

data class Url(val host: String, val port: Int) {
    override fun toString(): String = "$host:$port"
}