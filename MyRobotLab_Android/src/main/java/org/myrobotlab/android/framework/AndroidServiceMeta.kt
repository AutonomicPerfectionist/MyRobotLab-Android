package org.myrobotlab.android.framework

import android.Manifest
import org.myrobotlab.kotlin.framework.ServiceMeta

/**
 * Metadata specific to Android services.
 * Contains information about required permissions.
 * Services that require permissions
 * or other details present in the metadata
 * should have a companion object that extends this class
 * and overrides the applicable metadata.
 */
open class AndroidServiceMeta: ServiceMeta() {
    /**
     * List of Android permissions this service requires to
     * function. Ensure the permissions are added to the manifest as well.
     */
    open val requiredPermissions: List<String> = listOf()

    /**
     * List of Android permissions that this service does not require
     * in order to function but will be used for some feature of the service
     * if available.
     */
    open val optionalPermissions: List<String> = listOf()
}