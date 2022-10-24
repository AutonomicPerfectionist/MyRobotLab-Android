package org.myrobotlab.kotlin.codec.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase
import org.myrobotlab.kotlin.codec.CLASS_META_KEY
import org.myrobotlab.kotlin.framework.MrlClassMapping
import java.io.IOException
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * A Jackson serializer that injects [CLASS_META_KEY]
 * into the generated JSON. The value of this key is the object's
 * fully qualified class name. The class name enables other deserializers to
 * choose the correct type to deserialize the json into.
 *
 * @author AutonomicPerfectionist
 */
class JacksonPolymorphicSerializer : BeanSerializerBase {
    //Have to override a bunch of creation methods from the abstract class, which
    //we delegate to our constructors which delegate to the superclass constructors.
    //Basically, a lot of boilerplate.
    constructor(source: BeanSerializerBase?) : super(source)
    constructor(
        source: JacksonPolymorphicSerializer,
        objectIdWriter: ObjectIdWriter
    ) : super(source, objectIdWriter)

    constructor(
        source: JacksonPolymorphicSerializer,
        toIgnore: Array<String>
    ) : super(source, toIgnore)


    constructor(
        jacksonPolymorphicSerializer: JacksonPolymorphicSerializer,
        objectIdWriter: ObjectIdWriter,
        o: Any
    ) : super(jacksonPolymorphicSerializer, objectIdWriter, o)

    constructor(
        jacksonPolymorphicSerializer: JacksonPolymorphicSerializer?,
        beanPropertyWriters: Array<BeanPropertyWriter>?,
        beanPropertyWriters1: Array<BeanPropertyWriter>?
    ) : super(jacksonPolymorphicSerializer, beanPropertyWriters, beanPropertyWriters1)

    constructor(
        jacksonPolymorphicSerializer: JacksonPolymorphicSerializer,
        set: Set<String>,
        set1: Set<String>
    ):
        super(jacksonPolymorphicSerializer, set, set1)



    override fun withByNameInclusion(
        set: Set<String>,
        set1: Set<String>
    ): BeanSerializerBase {
        return JacksonPolymorphicSerializer(this, set, set1)
    }

    override fun withObjectIdWriter(
        objectIdWriter: ObjectIdWriter
    ): BeanSerializerBase {
        return JacksonPolymorphicSerializer(this, objectIdWriter)
    }

    override fun withIgnorals(toIgnore: MutableSet<String>): BeanSerializerBase {
        return JacksonPolymorphicSerializer(this, toIgnore.toTypedArray())
    }

    override fun asArraySerializer(): BeanSerializerBase {
        /* Cannot:
         *
         * - have Object Id (may be allowed in future)
         * - have "any getter"
         * - have per-property filters
         */
        return if (_objectIdWriter == null
            && _anyGetterWriter == null
            && _propertyFilterId == null
        ) {
            JacksonPolymorphicSerializer(this)
        } else this
        // already is one, so:
    }

    override fun withFilterId(o: Any): BeanSerializerBase {
        return JacksonPolymorphicSerializer(this, _objectIdWriter, o)
    }

    override fun withProperties(
        beanPropertyWriters: Array<BeanPropertyWriter>,
        beanPropertyWriters1: Array<BeanPropertyWriter>
    ): BeanSerializerBase {
        return JacksonPolymorphicSerializer(this, beanPropertyWriters, beanPropertyWriters1)
    }

    //This is the meat of the class
    @Throws(IOException::class)
    override fun serialize(
        o: Any,
        jsonGenerator: JsonGenerator,
        serializerProvider: SerializerProvider
    ) {
        jsonGenerator.writeStartObject()
        serializeFields(o, jsonGenerator, serializerProvider)
        val typeKey = o::class.findAnnotation<MrlClassMapping>()?.javaClass ?: "kt:${o::class.qualifiedName}"
        jsonGenerator.writeStringField(CLASS_META_KEY, typeKey)
        jsonGenerator.writeEndObject()
    }
}