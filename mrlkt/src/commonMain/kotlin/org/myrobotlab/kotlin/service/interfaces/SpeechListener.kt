package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.framework.interfaces.NameProvider


/**
 * This interface listens to speech
 *
 * @author GroG
 */
@MrlClassMapping("org.myrobotlab.service.interfaces.SpeechListener")
interface SpeechListener : NameProvider {
    /**
     * speech has begun with the this utterance
     *
     * @param utterance
     * - the speech that fragment was started in text form
     */
    fun onStartSpeaking(utterance: String?)

    /**
     * speech has ended with the this utterance
     *
     * @param utterance
     * - the speech fragement that was finished
     */
    fun onEndSpeaking(utterance: String?)
}