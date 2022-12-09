package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.framework.interfaces.NameProvider
import org.myrobotlab.kotlin.service.data.Voice


/**
 * SpeechSynthesis - This is the interface that services that provide text to
 * speech should implement.
 *
 */
@MrlClassMapping("org.myrobotlab.service.interfaces.SpeechSynthesis")
interface SpeechSynthesis : NameProvider, TextListener {
    val lastUtterance: String?

    /**
     * set the speaker voice
     *
     * @param v
     *          name of voice to set.
     * @return success or failure
     *
     */
    fun setVoice(v: String)

    /**
     * Begin speaking something and return immediately
     *
     * @param toSpeak
     * - the string of text to speak.
     * @return TODO
     * @throws Exception
     * e
     */
    @Throws(Exception::class)
    fun speak(toSpeak: String?): List<Any>

    /**
     * Begin speaking and wait until all speech has been played back/
     *
     * @param toSpeak
     * - the string of text to speak.
     * @throws Exception
     * e
     * @return true/false
     */
    @Throws(Exception::class)
    fun speakBlocking(toSpeak: String): List<Any>
    /**
     * Get audioData volume
     *
     * @return double
     */
    /**
     * Change audioData volume
     *
     * @param volume
     * - double between 0 and 1.
     */
    var volume: Double

    /**
     * Get current voice
     *
     * @return Voice
     */
    val voice: Voice

    /**
     * start callback for speech synth. (Invoked when speaking starts)
     *
     * @param utterance
     * text
     * @return the same text
     */
    fun publishStartSpeaking(utterance: String): String

    /**
     * stop callback for speech synth. (Invoked when speaking stops.)
     *
     * @param utterance
     * text
     * @return text
     */
    fun publishEndSpeaking(utterance: String?): String?

    /**
     * silence the service
     */
    @Deprecated("") /* use setMute */
    fun mute()

    /**
     * un-silence the service
     */
    @Deprecated("") /* use setMute */
    fun unmute()

    /**
     * mute or unmute
     *
     * @param mute
     * true to mute
     */
    fun setMute(mute: Boolean)

//    @Deprecated("") /*
//               * this should be type specific named - use attachSpeechRecognizer
//               */
//    fun addEar(ear: SpeechRecognizer?)

    // FIXME - is this in the wrong place ??? - this seems like bot logic ...
    fun onRequestConfirmation(text: String?)

    /**
     * @return get a list of voices this speech synthesis supports
     */
    val voices: List<Any?>?

    /**
     * puts all speaking into blocking mode - default is false
     *
     * @param b
     * true to block
     * @return blocking value
     */
    fun setBlocking(b: Boolean?): Boolean?

//    /**
//     * This attach subscribes the the SpeechRecognizer to the SpeechSynthesizer so
//     * the bot won't incorrectly recognize itself when its speaking ... otherwise
//     * silly things can happen when talking to self...
//     *
//     * @param ear
//     * to attach
//     */
//    fun attachSpeechRecognizer(ear: SpeechRecognizer?)

//    /**
//     * Speech control controls volume, setting the voice, and of course "speak"
//     *
//     * @param control
//     * the speech synth to attach
//     */
//    fun attachSpeechControl(control: SpeechSynthesisControl?)

    /**
     * Attach a speech listener which gets on started/stopped speaking callbacks.
     *
     * @param name
     */
    fun attachSpeechListener(name: String) {
        for (method in publishSpeechListenerMethods) {
            addListener(method, name)
        }
    }

    /**
     * Detach a speech listener that will remove the listeners for the speech
     * listener methods.
     *
     * @param name
     */
    fun detachSpeechListener(name: String) {
        for (method in publishSpeechListenerMethods) {
            removeListener(method, name)
        }
    }

    // All services implement this.
    fun addListener(topicMethod: String, callbackName: String)

    // All services implement this.
    fun removeListener(topicMethod: String, callbackName: String)

    /**
     * replace one word with another - instead of "biscuit" say "cookie"
     *
     * @param key
     * lookup word
     * @param replacement
     * replacement word.
     */
    fun replaceWord(key: String?, replacement: String?)

//    /**
//     * replace one word with another - instead of "biscuit" say "cookie"
//     *
//     * @param filter
//     * word filter to use
//     */
//    fun replaceWord(filter: WordFilter?)

    companion object {
//        val log: Logger = LoggerFactory.getLogger(SpeechSynthesis::class.java)

        /**
         * These are the methods that a speech listener should subscribe to.
         */
        val publishSpeechListenerMethods = arrayOf("publishStartSpeaking", "publishEndSpeaking")
    }
}