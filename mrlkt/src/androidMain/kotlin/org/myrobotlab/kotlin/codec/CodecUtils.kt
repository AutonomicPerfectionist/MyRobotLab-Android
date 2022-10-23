package org.myrobotlab.kotlin.codec

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.myrobotlab.kotlin.codec.json.JacksonPolymorphicModule


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
        private val mapper = ObjectMapper()

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
    }
}

