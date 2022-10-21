package org.myrobotlab.kotlin.framework

import org.myrobotlab.kotlin.framework.ServiceMethodProvider.callMethod
import kotlin.test.Test
import kotlin.test.assertEquals

class TestService: Service("test") {
    fun testMethod(): String {
        return "test"
    }
}

class ServiceMethodProviderTest {
    @Test
    internal fun testExecuteMethod() {
        val t = TestService()
        val r = t.callMethod("testMethod", listOf())
        assertEquals("test", r)
    }
}