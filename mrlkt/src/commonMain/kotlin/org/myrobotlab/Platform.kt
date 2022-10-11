package org.myrobotlab

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform