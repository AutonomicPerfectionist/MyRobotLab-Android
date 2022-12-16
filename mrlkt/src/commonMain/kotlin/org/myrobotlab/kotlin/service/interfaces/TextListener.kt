package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.framework.interfaces.NameProvider

/**
 * A service that can listen for
 * and respond to text events.
 */
@MrlClassMapping("org.myrobotlab.service.interfaces.TextListener")
interface TextListener : NameProvider {
    @Throws(Exception::class)
    fun onText(text: String?)

    /**
     * Attach a text listener
     *
     * @param service
     */
    fun attachTextPublisher(service: TextPublisher) {
        attachTextPublisher(service.name)
    }

    /**
     * Default way to attach an utterance listener so implementing classes need
     * not worry about these details.
     *
     * @param name
     */
    fun attachTextPublisher(name: String?) {
        send(name, "attachTextListener", name)
    }

    /**
     * Unknown usage
     */
    fun send(name: String?, method: String?, vararg data: Any?)
}