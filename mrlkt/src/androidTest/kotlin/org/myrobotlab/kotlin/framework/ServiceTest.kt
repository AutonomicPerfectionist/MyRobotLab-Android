package org.myrobotlab.kotlin.framework

import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.myrobotlab.kotlin.framework.MrlClient.eventBus
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.callMethod
import kotlin.test.Test



class ServiceTest {
    init {
        mockkObject(MrlClient)
        coEvery { MrlClient.sendCommand(any(), any(), any()) } returns Unit
    }

    class TestService(name: String): Service(name) {
        fun testMethod(): String {
            return "test"
        }

        fun testMethodWithParams(param1: String): String {
            return "test: $param1"
        }
    }

    @Test
    fun testMessagePassing() {
        val listener = MRLListener("testMethod", "callbackService", "callbackMethod")
        val t = spyk(TestService("test"))

        t.mrlListeners.getOrPut(listener.topicMethod) { mutableListOf(listener) }
        runBlocking {
            t.runInbox(this)
            println("Inbox running")
            delay(100)
            eventBus.emit(Message("test", "testMethod", listOf()))
            eventBus.emit(Message("test", "shutdown", listOf()))

        }

        verify { t.testMethod() }
    }

    @Test
    fun testMessagePassingMultipleServices() {
        val t = spyk(TestService("test"))
        val t2 = spyk(TestService("test2"))
        runBlocking {
            t.runInbox(this)
            t2.runInbox(this)
            println("Inbox running")
            delay(100)
            eventBus.emit(Message("test", "testMethod", listOf()))
            eventBus.emit(Message("test", "shutdown", listOf()))
            eventBus.emit(Message("test2", "shutdown", listOf()))

        }

        verify { t.testMethod() }
    }

    @Test
    fun testMRLListener() {
        val listener = MRLListener("testMethod", "callbackService", "callbackMethod")
        val t = spyk(TestService("test"))

        t.mrlListeners.getOrPut(listener.topicMethod) { mutableListOf(listener) }
        runBlocking {
            t.runInbox(this)
            println("Inbox running")
            delay(100)
            eventBus.emit(Message("test", "testMethod", listOf()))
            eventBus.emit(Message("test", "shutdown", listOf()))

        }

        coVerify { MrlClient.sendCommand("callbackService", "callbackMethod", listOf("test")) }
    }

}