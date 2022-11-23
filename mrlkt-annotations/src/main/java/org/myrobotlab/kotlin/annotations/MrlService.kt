package org.myrobotlab.kotlin.annotations


/**
 * Marks a class as being an MRL service and available
 * for construction by the mrlkt framework. A class with
 * this annotation *must* implement ServiceInterface and *must*
 * have a primary constructor with a single String argument (`name`).
 *
 * A class without this annotation but which still implements ServiceInterface
 * can still be manually created via `Runtime.start()` but it will not
 * be present in the service registry and therefore not automatically
 * discovered at system startup.
 *
 * Runtime is *not* marked with this annotation to prevent
 * users from attempting to start a second instance by accident.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MrlService()
