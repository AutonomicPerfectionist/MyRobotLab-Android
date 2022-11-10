package org.myrobotlab.kotlin.codec

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.myrobotlab.kotlin.codec.CodecUtils.Companion.fromJson
import org.myrobotlab.kotlin.codec.json.JacksonPolymorphicModule
import org.myrobotlab.kotlin.framework.Message
import org.myrobotlab.kotlin.framework.MrlClient
import kotlin.reflect.KClass
import kotlin.reflect.typeOf


const val CLASS_META_KEY = "class"



class CodecUtils {

    data class ApiDescription(
        /**
         * The string to use after [.PARAMETER_API]
         * in URIs to select this API.
         */
        val key: String,
        /**
         * The path to reach this API
         */
        val path // {scheme}://{host}:{port}/api/messages
        : String, val exampleUri: String, val description: String
    )

    companion object {

        /**
         * The Jackson [ObjectMapper] used for JSON operations when
         * the selected backend is Jackson.
         *
         * @see .USING_GSON
         */
        val mapper = ObjectMapper()

        /**
         * The [TypeFactory] used to generate type information for
         * [.mapper] when the selected backend is Jackson.
         *
         *
         * No analogue exists for Gson, as it uses a different mechanism
         * to represent types.
         *
         * @see .USING_GSON
         */
        private val typeFactory = TypeFactory.defaultInstance()

        init {

            mapper.registerModule(KotlinModule())

            //Actually add our polymorphic support
            mapper.registerModule(JacksonPolymorphicModule.polymorphicModule)

            //Disables Jackson's automatic property detection
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.ANY)

            //Make jackson behave like gson in that unknown properties are ignored
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        fun <T> T.toJson(): String {
            if (this is Message) {
                val newMsg = this.copy()
                for (index in newMsg.data.indices) {
                    newMsg.data[index] = mapper.writeValueAsString(newMsg.data[index])
                }
            }
            return mapper.writeValueAsString(this)
        }
        fun <T : Any> String.fromJson(type: KClass<T>): T = mapper.readValue(this, type.java)
        inline fun <reified T> String.fromJson(): T {
            val obj: T = mapper.readValue(this)
            if (obj is Message) {
                for(i in obj.data.indices) {
                    val dataStr = obj.data[i]
                    if (dataStr is String) {
                        MrlClient.logger.info("Converting data $dataStr")
                        obj.data[i] = dataStr.fromJson(Any::class)
                    }
                }
            }
            return obj
        }
    }
}

