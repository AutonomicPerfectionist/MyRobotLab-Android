package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.service.data.Orientation

/**
 * Listener for [Orientation] data published
 * by an [OrientationPublisher] service.
 * Use [OrientationPublisher.attach] to attach
 * an object of this type to a publishing service.
 */
@MrlClassMapping("org.myrobotlab.service.interfaces.OrientationListener")
interface OrientationListener {
    /**
     * Name of the service implementing this interface.
     */
    val name: String

    /**
     * Method to call when orientation data is available from
     * the publisher. This method will usually be called by
     * by the framework once a subscription is setup via [OrientationPublisher.attach]
     */
    suspend fun onOrientation(data: Orientation): Orientation
}