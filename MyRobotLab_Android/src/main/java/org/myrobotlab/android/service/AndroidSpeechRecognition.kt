package org.myrobotlab.android.service

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.myrobotlab.android.framework.AndroidServiceMeta
import org.myrobotlab.kotlin.annotations.MrlService
import org.myrobotlab.kotlin.framework.Service
import org.myrobotlab.kotlin.service.data.ListeningEvent
import org.myrobotlab.kotlin.service.interfaces.SpeechRecognizer
import org.myrobotlab.kotlin.service.interfaces.SpeechSynthesis
import org.myrobotlab.kotlin.service.interfaces.TextListener

@MrlService
class AndroidSpeechRecognition(name: String): Service(name), SpeechRecognizer, RecognitionListener {

    companion object: AndroidServiceMeta() {
        override val requiredPermissions: List<String> = listOf(
            Manifest.permission.RECORD_AUDIO
        )
    }

    val recognizer: android.speech.SpeechRecognizer by inject()
    val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
    }

    init {
        recognizer.setRecognitionListener(this)

    }

    override fun addMouth(mouth: SpeechSynthesis) {
        TODO("Not yet implemented")
    }

    override fun addTextListener(listener: TextListener) {
        TODO("Not yet implemented")
    }

    override fun attachSpeechSynthesis(mouth: SpeechSynthesis) {
        TODO("Not yet implemented")
    }

    override fun clearLock() {
        TODO("Not yet implemented")
    }

    override val isListening: Boolean
        get() = TODO("Not yet implemented")

    override fun listeningEvent(event: Boolean) {
        TODO("Not yet implemented")
    }

    override fun pauseListening() {
        TODO("Not yet implemented")
    }

    override fun publishListening(event: Boolean): Boolean {
        return event
    }

    override fun publishRecognized(text: String): String {
        return text
    }

    override fun publishListeningEvent(result: ListeningEvent): ListeningEvent {
        TODO("Not yet implemented")
    }

    override fun recognized(word: String): String {
        TODO("Not yet implemented")
    }

    override fun resumeListening() {
        TODO("Not yet implemented")
    }

    override fun startListening() {
        serviceScope.launch(Dispatchers.Main) {
            recognizer.startListening(recognizerIntent)
            serviceScope.launch {
                invoke<Boolean>("publishListening", true)
            }
        }
    }

    override fun stopListening() {
        recognizer.stopListening()

        serviceScope.launch {
            invoke<Boolean>("publishListening", false)
        }
    }

    override fun startRecording() {
        TODO("Not yet implemented")
    }

    override fun stopRecording() {
        TODO("Not yet implemented")
    }

    override var wakeWord: String? = null

    override fun unsetWakeWord() {
        wakeWord = null
    }

    override fun lockOutAllGrammarExcept(lockPhrase: String) {
        TODO("Not yet implemented")
    }

    override fun publishText(text: String): String {
        return text
    }

    override fun detachTextListener(name: String) {
        TODO("Not yet implemented")
    }

    override fun onStartSpeaking(utterance: String?) {

    }

    override fun onEndSpeaking(utterance: String?) {

    }

    override fun onReadyForSpeech(p0: Bundle?) {

    }

    override fun onBeginningOfSpeech() {
        Log.i(name, "Speech has begun")
    }

    override fun onRmsChanged(p0: Float) {

    }

    override fun onBufferReceived(p0: ByteArray?) {
//        TODO("Not yet implemented")
    }

    override fun onEndOfSpeech() {
//        TODO("Not yet implemented")
    }

    override fun onError(p0: Int) {
//        TODO("Not yet implemented")
    }

    override fun onResults(p0: Bundle) {
        val matches = p0.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
        matches?.joinToString(" ")?.let {
            serviceScope.launch {
                invoke<String>("publishText", it)
            }
            serviceScope.launch {
                invoke<String>("publishRecognized", it)
            }
        }

    }

    override fun onPartialResults(p0: Bundle?) {
//        TODO("Not yet implemented")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
//        TODO("Not yet implemented")
    }
}