package org.myrobotlab.android.service

import android.speech.tts.TextToSpeech
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.myrobotlab.kotlin.annotations.MrlService
import org.myrobotlab.kotlin.framework.Service
import org.myrobotlab.kotlin.service.data.Voice
import org.myrobotlab.kotlin.service.interfaces.SpeechSynthesis

/**
 * A [SpeechSynthesis] service that uses the Android [TextToSpeech]
 * system as its backend. The voices available are the voices
 * installed on the target device, and the speech
 * can either be directly output through the device's
 * speakers or saved to a file for use with audio syncing
 * services.
 *
 * TODO requires peer, AudioFile, and AudioData support
 *  for full compatibility
 */
@MrlService
class AndroidTTS(name: String): Service(name), SpeechSynthesis {
    private val tts: TextToSpeech by inject()

    override var lastUtterance: String? = null
        private set

    override fun setVoice(v: String) {
        TODO("Not yet implemented")
    }

    override fun speak(toSpeak: String?): List<Any> {
        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, toSpeak)
        return listOf()
    }

    override fun speakBlocking(toSpeak: String): List<Any> {
        TODO("Not yet implemented")
    }

    override var volume: Double
        get() = TODO("Not yet implemented")
        set(value) {}
    override val voice: Voice
        get() = TODO("Not yet implemented")

    override fun publishStartSpeaking(utterance: String): String {
        TODO("Not yet implemented")
    }

    override fun publishEndSpeaking(utterance: String?): String? {
        TODO("Not yet implemented")
    }

    override fun mute() {
        TODO("Not yet implemented")
    }

    override fun unmute() {
        TODO("Not yet implemented")
    }

    override fun setMute(mute: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onRequestConfirmation(text: String?) {
        TODO("Not yet implemented")
    }

    override val voices: List<Any?>?
        get() = TODO("Not yet implemented")

    override fun setBlocking(b: Boolean?): Boolean? {
        TODO("Not yet implemented")
    }

    override fun removeListener(topicMethod: String, callbackName: String) {
        TODO("Not yet implemented")
    }

    override fun replaceWord(key: String?, replacement: String?) {
        TODO("Not yet implemented")
    }

    override fun onText(text: String?) {
        serviceScope.launch {
            invoke<List<Any>>("speak", text)
        }
    }

    override fun send(name: String?, method: String?, vararg data: Any?) {
        TODO("Not yet implemented")
    }
}