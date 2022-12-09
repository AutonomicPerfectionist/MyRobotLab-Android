package org.myrobotlab.android.service

import org.myrobotlab.kotlin.annotations.MrlService
import org.myrobotlab.kotlin.framework.Service

/**
 * Simple template service that doesn't do anything and doesn't
 * provide any additional methods to call.
 *
 * TODO Remove [MrlService] to remove it from the list
 *  of available services but keep as a template for new services.
 */
@MrlService
class TestKotlinService(name: String): Service(name) {
    override fun toString(): String {
        return name
    }
}