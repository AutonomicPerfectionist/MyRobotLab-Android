package org.myrobotlab.kotlin.framework

import kotlin.reflect.KCallable
import kotlin.reflect.KClass

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
    val Service.methods:List<ServiceMethod>

    fun <T: ServiceInterface> KClass<T>.constructService(name: String): T


    /**
     * An extension method that allows any available method from a service
     * to be called by its name (in String form) and its parameters in a list.
     * The first compatible method overload will be chosen if multiple
     * exist. Nullability is checked so null values may not be used in place
     * of non-null parameters.
     *
     * @param method: The name of the method to call, case sensitive
     * @param data: A list of parameters to call the method with
     * @return The return value of the called method, Unit if method does not return anything
     * @throws RuntimeException if no compatible method found
     */
    suspend fun <R> Service.callMethod(method: String, data:List<Any?>): R?
}