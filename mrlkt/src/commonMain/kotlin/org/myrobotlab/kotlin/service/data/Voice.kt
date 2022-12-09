package org.myrobotlab.kotlin.service.data

import org.myrobotlab.kotlin.annotations.MrlClassMapping

@MrlClassMapping("org.myrobotlab.service.abstracts.AbstractSpeechSynthesis.Voice")
class Voice(
    /**
     * unique name of the voice
     */
    var name: String?, gender: String?, lang: String?,
    /**
     * Serializable key of voice implementation - to be used to map this MRL
     * Voice to a voice implementation
     */
    var voiceProvider: Any
) {

    /**
     * gender of the voice either male or female
     */
    var gender: String? = null

    /**
     * description
     */
    var description: String? = null

    // TODO - age ? child youth adult senior
//    var locale: Locale? = null
    var locale: Any? = null

    /**
     * Installed means the voice is ready without any additional components
     */
    var isInstalled = true

    init {
        voiceProvider = voiceProvider
        if (gender != null) {
            val g = gender.lowercase()
            this.gender = g
        }
        if (lang != null) {
            val l = lang.split("-").toTypedArray()
//            if (l.size > 1) {
//                locale = Locale(l[0], l[1])
//            } else {
//                locale = Locale(l[0])
//            }
        }
    }


    /**
     * Java does regions string codes differently than other systems en_US vs
     * en-US ... seems like there has been a lot of confusion on which delimiter
     * to use This function is used to simplify all of that - since we are
     * primarily interested in language and do not usually need the distinction
     * between regions in this context
     *
     * @return the string language name
     */
    val language: String
        get() = "en-us"
//
//    fun getLocale(): Locale? {
//        return locale
//    }

    val languageCode: String?
        get() = if (locale == null) {
            null
        } else locale.toString()

    companion object {
        private const val serialVersionUID = 1L
    }
}