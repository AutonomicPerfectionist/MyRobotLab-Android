package org.myrobotlab.kotlin.codec.json

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.myrobotlab.kotlin.codec.CLASS_META_KEY
import org.myrobotlab.kotlin.codec.ForeignProcessUtils.isForeignTypeKey
import org.myrobotlab.kotlin.codec.ForeignProcessUtils.languageId
import org.myrobotlab.kotlin.codec.ForeignProcessUtils.languageSpecificTypeKey
import org.myrobotlab.kotlin.framework.mappings
import java.io.IOException

/**
 * A Jackson deserializer that handles polymorphic operations.
 * This class will look at JSON objects and check if they have a field
 * called [CLASS_META_KEY], if they do it will
 * use the value of that field as the target type to deserialize into,
 * unless the requested type is not a superclass of the embedded type.
 * The requested type may be outside the embedded type's set of superclasses
 * when the user wishes to interpret the value in a different form, such as
 * a [Map].
 *
 * @param <T> Specifies which type (and its subclasses) this deserializer will operate on.
 * * @param deserializer [.originalDeserializer], must also be a [ResolvableDeserializer]
 * @param clazz [.clazz]
 * @throws IllegalArgumentException if deserializer is not also a [ResolvableDeserializer]
 */
class JacksonPolymorphicDeserializer<T>(deserializer: JsonDeserializer<T>, clazz: Class<*>) :
    StdDeserializer<T>(clazz), ResolvableDeserializer {
    /**
     * The original deserializer to dispatch to in
     * order to avoid infinite loops.
     */
    var originalDeserializer: JsonDeserializer<T>

    /**
     * The class that was requested to deserialize into.
     */
    var clazz: Class<*>

    /**
     * Create a new deserializer with the delegate deserializer
     * and the requested type.
     *

     */
    init {
        require(deserializer is ResolvableDeserializer) { "Deserializer must also be a ResolvableDeserializer, got $deserializer" }
        this.clazz = clazz
        originalDeserializer = deserializer
    }

    //Default implementation delegates to the other overload, causing infinite loop.
    //We delegate to the original deserializer which then fills the object correctly
    @Throws(IOException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext,
        o: T
    ): T {
        return originalDeserializer.deserialize(jsonParser, deserializationContext, o)
    }

    /**
     * Deserialize the given node without attempting
     * polymorphic operations.
     *
     * @param jsonParser The parser to use to deserialize
     * @param deserializationContext The context used to deserialize
     * @param node The node to deserialize
     * @return The fully deserialized object, using the original deserializer
     * @throws IOException if deserialization fails
     */
    @Throws(IOException::class)
    fun deserializeNoPolymorphic(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext?,
        node: JsonNode
    ): T {
        val it: MutableIterator<MutableMap.MutableEntry<String, JsonNode>> = node.fields()
        while (it.hasNext()) {
            val field: MutableMap.MutableEntry<String, JsonNode> = it.next()
            if (field.key == CLASS_META_KEY) it.remove()
        }
        val parser = resetNode(node, jsonParser.codec)
        return originalDeserializer.deserialize(parser, deserializationContext)
    }

    /**
     * Reset the node to the beginning of the token stream,
     * using the specified codec.
     *
     * @param node The node to be reset
     * @param codec The codec to use
     * @return A new JsonParser that is reset to the beginning of the node's token stream
     * @throws IOException If a new parser cannot be created
     */
    @Throws(IOException::class)
    private fun resetNode(node: JsonNode, codec: ObjectCodec): JsonParser {
        val parser = JsonFactory().createParser(node.toString())
        parser.codec = codec
        parser.nextToken()
        return parser
    }

    @Throws(IOException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): T {

        //This is a mess and needs cleaned up
        val node = jsonParser.readValueAsTree<JsonNode>()
        //Need to save the json string because
        //part of it will be consumed
        //by getting the type
        val json = node.toString()
        if (!node.has(CLASS_META_KEY)) return deserializeNoPolymorphic(
            jsonParser,
            deserializationContext,
            node
        )
        val typeAsString: String = node.get(CLASS_META_KEY).asText()
        val type: Class<*> = try {
            val normalizedType = if (typeAsString.isForeignTypeKey && typeAsString.languageId == "kt")
                typeAsString.languageSpecificTypeKey
            else
                mappings.inverse[typeAsString] ?: typeAsString
            Class.forName(normalizedType)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }


        //If we are deser to correct target type
        //or if requested type is not a superclass of embedded type,
        //delegate to default deserialization
        if (type == clazz || clazz.isAssignableFrom(type)) {
            deserializeNoPolymorphic(jsonParser, deserializationContext, node)
        }
        return deserializationContext.readValue(resetNode(node, jsonParser.codec), type) as T
    }

    // for some reason you have to implement ResolvableDeserializer when modifying BeanDeserializer
    // otherwise deserializing throws JsonMappingException??
    @Throws(JsonMappingException::class)
    override fun resolve(ctxt: DeserializationContext) {
        (originalDeserializer as ResolvableDeserializer).resolve(ctxt)
    }
}