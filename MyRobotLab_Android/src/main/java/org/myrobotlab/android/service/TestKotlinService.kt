package org.myrobotlab.android.service

import org.myrobotlab.kotlin.annotations.MrlService
import org.myrobotlab.kotlin.framework.Service

@MrlService
class TestKotlinService(name: String): Service(name) {
    override fun toString(): String {
        return name
    }
}