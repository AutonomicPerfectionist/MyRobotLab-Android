package org.myrobotlab.kotlin.framework

import kotlinx.coroutines.runBlocking
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.callMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith



class ServiceMethodProviderTest {
    class TestService: Service("test") {
        fun testMethod(): String {
            return "test"
        }

        fun testMethodWithParams(param1: String): String {
            return "test: $param1"
        }
    }

    @Test
    internal fun testExecuteMethodNoParams() {
        val t = TestService()
        runBlocking {
            val r = t.callMethod<String>("testMethod", listOf())
            assertEquals("test", r)
        }
    }

    @Test
    internal fun testExecuteMethodOneParam() {
        val t = TestService()
        runBlocking {
            val r = t.callMethod<String>("testMethodWithParams", listOf("test1"))
            assertEquals("test: test1", r)
        }
    }

    @Test
    internal fun testExecuteMethodOneParamWithNull() {
        val t = TestService()
        runBlocking {
            assertFailsWith(RuntimeException::class) {
                t.callMethod<String>("testMethodWithParams", listOf(null))
            }
        }
    }
}