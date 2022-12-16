package org.myrobotlab.android.service

import android.Manifest
import org.myrobotlab.android.framework.AndroidServiceMeta
import org.myrobotlab.kotlin.annotations.MrlService
import org.myrobotlab.kotlin.framework.Service

@MrlService
class AndroidSpeechRecognition(name: String): Service(name) {

    companion object: AndroidServiceMeta() {
        override val requiredPermissions: List<String> = listOf(
            Manifest.permission.RECORD_AUDIO
        )
    }
}