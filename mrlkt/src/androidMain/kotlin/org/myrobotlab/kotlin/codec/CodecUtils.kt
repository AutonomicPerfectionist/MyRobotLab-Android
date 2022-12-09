package org.myrobotlab.kotlin.codec

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
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
        val mapper: JsonMapper = JsonMapper.builder()
            .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
            .build()

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

            mapper.registerModule(KotlinModule.Builder().build())

            //Actually add our polymorphic support
            mapper.registerModule(JacksonPolymorphicModule.polymorphicModule)

            //Disables Jackson's automatic property detection
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
            mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
            mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)

            //Make jackson behave like gson in that unknown properties are ignored
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        fun <T> T.toJson(): String {
            return if (this is Message) {
                val params = this.data.toMutableList()
                for (index in params.indices) {
                    params[index] = mapper.writeValueAsString(params[index])
                }
                mapper.writeValueAsString(this.copy(data = params))
            } else {
                mapper.writeValueAsString(this)
            }
        }
        fun <T : Any> String.fromJson(type: KClass<T>): T = mapper.readValue(this, type.java)
        inline fun <reified T> String.fromJson(): T {
            val obj: T = mapper.readValue(this)
            if (obj is Message) {
                val params = obj.data.toMutableList()
                for(i in params.indices) {
                    val dataStr = params[i]
                    if (dataStr is String) {
                        MrlClient.logger.info("Converting data $dataStr")
                        try {
                            params[i] = dataStr.fromJson(Any::class)
                        } catch (e: Exception) {
                            MrlClient.logger.info("EXCEPTION: $e")
                        }
                    }
                }
                return obj.copy(data = params) as T
            }
            return obj
        }
    }
}

