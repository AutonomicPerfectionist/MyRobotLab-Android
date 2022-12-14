package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.framework.ServiceInterface
import org.myrobotlab.kotlin.framework.interfaces.NameProvider

@MrlClassMapping("org.myrobotlab.service.interfaces.TextPublisher")
interface TextPublisher : ServiceInterface {
  /**
   * Define the methods that an utterance publisher should have
   *
   * @param text
   * @return
   */
  fun publishText(text: String): String

  /**
   * Attach a text listener
   *
   * @param service
   */
  fun attachTextListener(service: TextListener) {
    attachTextListener(service.name)
  }

  /**
   * Default way to attach an utterance listener so implementing classes need
   * not worry about these details.
   *
   * @param name
   */
  fun attachTextListener(name: String) {
    for (publishMethod in publishMethods) {
      addListener(publishMethod, name)
    }
  }

  fun detachTextListener(service: TextListener) {
    attachTextListener(service.name)
  }

  fun detachTextListener(name: String)
//  {
//    for (publishMethod in publishMethods) {
//      removeListener(publishMethod, name)
//    }
//  }



  companion object {
    /**
     * These are all the methods that the utterance publisher should produce.
     */
    val publishMethods = arrayOf("publishText")
  }
}