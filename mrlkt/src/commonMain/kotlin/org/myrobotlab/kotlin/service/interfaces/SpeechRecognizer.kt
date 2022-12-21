package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.framework.interfaces.NameProvider
import org.myrobotlab.kotlin.service.data.ListeningEvent

@MrlClassMapping("org.myrobotlab.service.interfaces.SpeechRecognizer")
interface SpeechRecognizer : NameProvider,
    TextPublisher, SpeechListener {
    /**
     * This method should listen for Mouth events
     *
     * FIXME - should be deprecated - use Attach Pattern !
     *
     * @param mouth
     * m
     */
    @Deprecated("") /* use attachSpeechSynthesis(SpeechSynthesis mouth) */
    fun addMouth(mouth: SpeechSynthesis)

    @Deprecated("") /* use attachTextListener(TextListener listener) */
    fun addTextListener(listener: TextListener)

    /**
     * This typically will suppress listening to itself when it speaks creating an
     * endless self dialog :P
     *
     * @param mouth
     * the speech synthesis to attach
     */
    fun attachSpeechSynthesis(mouth: SpeechSynthesis)


    /**
     * This will unlock lockOutAllGrammarExcept(lockPhrase)
     */
    @Deprecated("legacy pre-wake word")
    fun clearLock()

    /**
     * track the state of listening process
     *
     * @return true if listening
     */
    val isListening: Boolean

    /**
     * @param event
     * Event is sent when the listening Service is actually listening or
     * not.
     */
    @Deprecated("use publishListening(boolean event)")
    fun listeningEvent(event: Boolean)

    /**
     * method to suppress recognition listening events This is important when a
     * Speech Recognizer is listening --&gt; then Speaking, typically you don't
     * want the STT to listen to its own speech, it causes a feedback loop and
     * with STT not really very accurate, it leads to weirdness -- additionally it
     * does not recreate the speech processor - so its not as heavy handed
     */
    @Deprecated("legacy sphinx - use stop/start listening")
    fun pauseListening()

    /**
     * Publish event when listening or not listening ...
     *
     * @param event
     * e
     * @return the event
     */
    fun publishListening(event: Boolean): Boolean

    /**
     * the recognized text
     *
     * @param text
     * text to be published
     * @return the text
     */
    fun publishRecognized(text: String): String

    /**
     * the text in addition to any meta data like confidence rating
     *
     * @param result
     * r
     * @return listening event
     */
    fun publishListeningEvent(result: ListeningEvent): ListeningEvent

    @Deprecated("should use standard publishRecognized")
    fun recognized(word: String): String

    @Deprecated("use stopListening() and startListening()")
    fun resumeListening()

    /**
     * Start recognizing allows recognized events to be published
     */
    fun startListening()

    /**
     * Stop recognizing continues listening and recording audio, but will not
     * publish recognized events
     */
    fun stopListening()

    /**
     * Start recording begins recording and initially starts recognizing unless a
     * wake word is used. If a wake word is used - recording starts but listening
     * and publishing recognized speech is prevented from publishing until the
     * wake word is recognized
     */
    fun startRecording()

    /**
     * Stop listening stops the recording and and any possibility of recognizing
     * incoming audio
     */
    fun stopRecording()
    /**
     * @return Get the current wake word
     */
    /**
     * Setting the wake word - wake word behaves as a switch to turn on "active
     * listening" similar to "hey google"
     *
     * @param word
     * wake word to set
     */
    var wakeWord: String?

    /**
     * Stop wake word functionality .. after being called stop and start
     */
    fun unsetWakeWord()
    fun lockOutAllGrammarExcept(lockPhrase: String)
}
