package org.myrobotlab.kotlin.framework

import okhttp3.internal.toImmutableMap
import org.myrobotlab.kotlin.framework.MappingInitializer.mutableMapping
import org.myrobotlab.kotlin.utils.BiMap
import org.myrobotlab.kotlin.utils.HashBiMap
import org.myrobotlab.kotlin.utils.MutableBiMap
import org.reflections.Reflections
import kotlin.reflect.full.findAnnotation


actual val mappings: BiMap<String, String>
    get() = mutableMapping



private object MappingInitializer {
    val mutableMapping = HashBiMap<String, String>()

    init {
        val reflections = Reflections("org.myrobotlab")
        val annotated = reflections.getTypesAnnotatedWith(MrlClassMapping::class.java)
        annotated.forEach {
            mutableMapping[it.kotlin.qualifiedName!!] = it.kotlin.findAnnotation<MrlClassMapping>()!!.javaClass
        }
    }
}