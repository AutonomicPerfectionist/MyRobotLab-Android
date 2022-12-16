package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.service.data.Hertz

/**
 * A service that samples some data at
 * a given rate and can change that sample
 * rate.
 */
interface Sampler {
    /**
     * The rate at which some device should
     * be sampled.
     */
    var sampleRate: Hertz
}