package org.myrobotlab.kotlin.codec.json

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase

/**
 * A factory class to configure a [SimpleModule] for Jackson
 * [ObjectMapper]s that adds polymorphic support.
 *
 * @author AutonomicPerfectionist
 */
object JacksonPolymorphicModule {
//Need a BeanDeserializer to instantiate ours//Need a ResolvableDeserializer to instantiate our polymorphic one
    /**
     * Generate a new SimpleModule and add deserialization and
     * serialization modifiers to enable polymorphic support.
     * @return A new module with polymorphic support
     */
    val polymorphicModule: SimpleModule
        get() {
            val module = SimpleModule()
            module.setDeserializerModifier(object : BeanDeserializerModifier() {
                override fun modifyDeserializer(
                    config: DeserializationConfig,
                    beanDesc: BeanDescription,
                    deserializer: JsonDeserializer<*>
                ): JsonDeserializer<*> {

                    //Need a ResolvableDeserializer to instantiate our polymorphic one
                    return if (deserializer is ResolvableDeserializer) {
                        JacksonPolymorphicDeserializer(deserializer, beanDesc.beanClass)
                    } else deserializer
                }
            })
            module.setSerializerModifier(object : BeanSerializerModifier() {
                override fun modifySerializer(
                    config: SerializationConfig,
                    beanDesc: BeanDescription,
                    serializer: JsonSerializer<*>
                ): JsonSerializer<*> {

                    //Need a BeanDeserializer to instantiate ours
                    return if (serializer is BeanSerializerBase) JacksonPolymorphicSerializer(
                        serializer
                    ) else serializer
                }
            })
            return module
        }
}