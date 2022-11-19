package org.myrobotlab.kotlin.codec.json

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Assert
import kotlin.test.Test
import org.myrobotlab.kotlin.codec.CodecUtils
import org.myrobotlab.kotlin.codec.json.JacksonPolymorphicModule.polymorphicModule
import org.myrobotlab.kotlin.framework.Message
import org.myrobotlab.kotlin.framework.Registration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.assertEquals

class JacksonPolymorphicModuleTest {
    @Test
    @Throws(JsonProcessingException::class)
    fun testStringSer() {
        val testString = "this is a test with spaces and \$pecial characters!"
        val jsonString = polymorphicMapper.writeValueAsString(testString)
        log.debug("Encoded test string: $jsonString")
        val decodedString = regularMapper.readValue(
            jsonString,
            String::class.java
        )
        log.debug("Decoded test string: $decodedString")
        Assert.assertEquals("String encoding not correct", testString, decodedString)
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun testStringArraySer() {
        val testStrings = arrayOf("This", "is", "a", "test", "array")
        val jsonString = polymorphicMapper.writeValueAsString(testStrings)
        log.debug("Encoded test string: $jsonString")
        val decodedStrings = regularMapper.readValue(
            jsonString,
            Array<String>::class.java
        )
        log.debug("Decoded test strings: " + Arrays.toString(decodedStrings))
        Assert.assertArrayEquals("String array encoding incorrect", testStrings, decodedStrings)
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun testNumberSer() {
        val testInt = 42
        val jsonString = polymorphicMapper.writeValueAsString(testInt)
        log.debug("Encoded test string: $jsonString")
        val decodedInt = regularMapper.readValue(
            jsonString,
            Int::class.java
        )
        log.debug("Decoded test int: $decodedInt")
        Assert.assertEquals("Int encoding not correct", testInt.toLong(), decodedInt.toLong())
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun testBoolSer() {
        val testBoolean = false
        val jsonString = polymorphicMapper.writeValueAsString(testBoolean)
        log.debug("Encoded test string: $jsonString")
        val decodedBoolean = regularMapper.readValue(
            jsonString,
            Boolean::class.java
        )
        log.debug("Decoded test string: $decodedBoolean")
        Assert.assertEquals("Boolean encoding not correct", testBoolean, decodedBoolean)
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun testStringDeser() {
        val testString = "this is a test with spaces and \$pecial characters!"
        val jsonString = regularMapper.writeValueAsString(testString)
        log.debug("Encoded test string: $jsonString")
        val decodedString = polymorphicMapper.readValue(
            jsonString,
            String::class.java
        )
        log.debug("Decoded test string: $decodedString")
        Assert.assertEquals("String decoding not correct", testString, decodedString)
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun testStringArrayDeser() {
        val testStrings = arrayOf("This", "is", "a", "test", "array")
        val jsonString = regularMapper.writeValueAsString(testStrings)
        log.debug("Encoded test string: $jsonString")
        val decodedStrings = polymorphicMapper.readValue(
            jsonString,
            Array<String>::class.java
        )
        log.debug("Decoded test strings: " + Arrays.toString(decodedStrings))
        Assert.assertArrayEquals("String array decoding incorrect", testStrings, decodedStrings)
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun testNumberDeser() {
        val testInt = 42
        val jsonString = regularMapper.writeValueAsString(testInt)
        log.debug("Encoded test string: $jsonString")
        val decodedInt = polymorphicMapper.readValue(
            jsonString,
            Int::class.java
        )
        log.debug("Decoded test int: $decodedInt")
        Assert.assertEquals("Int decoding not correct", testInt.toLong(), decodedInt.toLong())
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun testBoolDeser() {
        val testBoolean = false
        val jsonString = regularMapper.writeValueAsString(testBoolean)
        log.debug("Encoded test string: $jsonString")
        val decodedBoolean = polymorphicMapper.readValue(
            jsonString,
            Boolean::class.java
        )
        log.debug("Decoded test string: $decodedBoolean")
        Assert.assertEquals("Boolean decoding not correct", testBoolean, decodedBoolean)
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun testApiDescriptionDeser() {
        val description: CodecUtils.ApiDescription = CodecUtils.ApiDescription(
            "key",
            "/path/", "{exampleURI}", "This is a description"
        )
        val jsonString = regularMapper.writeValueAsString(description)
        log.debug("Encoded test string: $jsonString")
        val decodedDescription: CodecUtils.ApiDescription = polymorphicMapper.readValue(
            jsonString,
            CodecUtils.ApiDescription::class.java
        )
        log.debug("Decoded test string: $decodedDescription")
        Assert.assertEquals("ApiDescription decoding not correct", description, decodedDescription)
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun testMessagePolymorphic() {
        val message = Message(
            "runtime", "register",
            mutableListOf(Registration("runtime", "obsidian", "org.myrobotlab.service.Runtime"))
        )
        val jsonString = polymorphicMapper.writeValueAsString(message)
        log.debug("Encoded test string: $jsonString")
        val decodedMessage: Message = polymorphicMapper.readValue(
            jsonString,
            Any::class.java
        ) as Message
        log.debug("Decoded test string: $decodedMessage")
        assertEquals(message, decodedMessage, "Message polymorphism not correct")
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(JacksonPolymorphicModuleTest::class.java)
        private var polymorphicMapper: ObjectMapper = ObjectMapper()
        private var regularMapper: ObjectMapper = ObjectMapper()
        init {
            polymorphicMapper.registerModule(KotlinModule())
            polymorphicMapper.registerModule(polymorphicModule)

            //Disables Jackson's automatic property detection
            polymorphicMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            polymorphicMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            polymorphicMapper.setVisibility(
                PropertyAccessor.SETTER,
                JsonAutoDetect.Visibility.ANY
            )

            //Make jackson behave like gson in that unknown properties are ignored
            polymorphicMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            regularMapper.registerModule(KotlinModule())

            //Disables Jackson's automatic property detection
            regularMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            regularMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            regularMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.ANY)

            //Make jackson behave like gson in that unknown properties are ignored
            regularMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }
}