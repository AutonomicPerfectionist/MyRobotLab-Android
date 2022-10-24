package org.myrobotlab.kotlin.framework

import org.myrobotlab.kotlin.utils.BiMap
import org.myrobotlab.kotlin.utils.HashBiMap
import org.reflections.Reflections
import kotlin.reflect.full.findAnnotation


actual val mappings: BiMap<String, String> = HashBiMap<String, String>().apply {
    val reflections = Reflections("org.myrobotlab")
    val annotated = reflections.getTypesAnnotatedWith(MrlClassMapping::class.java, true)
    annotated.forEach {
        val qualifiedName = it.kotlin.qualifiedName ?: return@forEach
        val javaClass = it.kotlin.findAnnotation<MrlClassMapping>()?.javaClass ?: return@forEach
        this[qualifiedName] = javaClass
    }
}