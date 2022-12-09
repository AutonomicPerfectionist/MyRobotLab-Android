package org.myrobotlab.kotlin.framework

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import org.myrobotlab.kotlin.annotations.MrlClassMapping

/**
 * This class provides information on service methods
 * and handles execution of said methods. The implementation
 * is dependent on the target platform.
 *
 * @author AutonomicPerfectionist
 */
expect object ServiceMethodProvider {
    /**
     * An extension property that lists the available
     * methods to call on a service
     */
    val ServiceInterface.methods:List<ServiceMethod>

    /**
     * The list of interfaces implemented
     * by the service that have a Java-land equivalent
     * (denoted by the [MrlClassMapping] annotation on the interface class).
     *
     */
    val ServiceInterface.mrlInterfaceNames: List<String>

    /**
     * Construct a new service from this class with the given name.
     * This class must have a primary constructor with a single
     * String parameter called `name` for this method to work.
     *
     * @param name The name of the new service
     */
    fun <T: ServiceInterface> KClass<T>.constructService(name: String): T


    /**
     * An extension method that allows any available method from a service
     * to be called by its name (in String form) and its parameters in a list.
     * The first compatible method overload will be chosen if multiple
     * exist. Nullability is checked so null values may not be used in place
     * of non-null parameters. Methods with the format `getX()` or `setX()` should be
     * mapped to the equivalent accessor or mutator of the respective property.
     *
     * @param method: The name of the method to call, case sensitive
     * @param data: A list of parameters to call the method with
     * @return The return value of the called method, Unit if method does not return anything
     * @throws RuntimeException if no compatible method found
     */
    suspend fun <R> ServiceInterface.callMethod(method: String, data:List<Any?>): R?
}