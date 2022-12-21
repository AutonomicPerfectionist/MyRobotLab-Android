package org.myrobotlab.kotlin.service.data

import org.myrobotlab.kotlin.annotations.MrlClassMapping

@MrlClassMapping("org.myrobotlab.service.abstracts.AbstractSpeechRecognizer.ListeningEvent")
data class ListeningEvent(
    var ts: Long = 0,
    var confidence: Double? = null,
    var isSpeaking: Boolean? = null,
    var isListening: Boolean? = null,
    var isRecording: Boolean? = null,
    var isFinal: Boolean? = null,
    var text: String? = null,
    var isAwake // assume awake
            : Boolean? = null,

    /**
     * determines if the listening event will trigger a publishText - default is
     * false because there are a variety of meta-messages that would not be
     * suitable to publish
     */
    var publishText: Boolean = false

)