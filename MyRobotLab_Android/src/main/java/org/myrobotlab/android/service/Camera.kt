package org.myrobotlab.android.service

import android.Manifest
import org.myrobotlab.android.framework.AndroidServiceMeta
import org.myrobotlab.kotlin.framework.Service
import org.myrobotlab.kotlin.service.data.SerializableImage
import org.myrobotlab.kotlin.service.interfaces.VideoSource

// TODO Needs GUI support since you can't record from a camera without a preview window
class Camera(name: String): Service(name), VideoSource {
    companion object: AndroidServiceMeta() {
        override val requiredPermissions: List<String> = listOf(
            Manifest.permission.CAMERA
        )
    }

    override fun publishDisplay(image: SerializableImage): SerializableImage = image
}