package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.framework.ServiceInterface
import org.myrobotlab.kotlin.service.data.SerializableImage

@MrlClassMapping("org.myrobotlab.service.interfaces.VideoSource")
interface VideoSource: ServiceInterface {

    fun publishDisplay(image: SerializableImage): SerializableImage
}