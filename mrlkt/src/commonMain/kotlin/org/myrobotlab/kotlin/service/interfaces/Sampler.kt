package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.service.data.Hertz

/**
 * A service that samples some data at
 * a given rate and can change that sample
 * rate.
 */
interface Sampler {
    var sampleRate: Hertz
}