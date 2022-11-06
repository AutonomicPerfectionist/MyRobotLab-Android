package org.myrobotlab.kotlin.ksp


import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import org.myrobotlab.kotlin.annotations.MrlClassMapping
import kotlin.reflect.KClass

class MrlClassMappingProcessor(private val environment: SymbolProcessorEnvironment): SymbolProcessor {
    private fun com.google.devtools.ksp.processing.Resolver.findAnnotations(
        kClass: KClass<*>,
    ) = getSymbolsWithAnnotation(
        kClass.qualifiedName.toString(), true).filterIsInstance<KSClassDeclaration>()


    override fun process(resolver: com.google.devtools.ksp.processing.Resolver): List<KSAnnotated> {
        val annotatedClasses: Sequence<KSClassDeclaration> =
            resolver.findAnnotations(MrlClassMapping::class)
        if(!annotatedClasses.iterator().hasNext()) return emptyList()
        val classMappings = annotatedClasses.map {
            it.qualifiedName?.asString() to it.annotations.first { annotation ->
                annotation.shortName.getShortName() == "MrlClassMapping"
            }.arguments.first { arg ->
                arg.name?.getShortName() == "javaClass"
            }
        }

        val sourceFiles = annotatedClasses.mapNotNull { it.containingFile }
        val fileText = buildString {
            append("// ")
            appendLine(classMappings.joinToString(", "))
            appendLine("package org.myrobotlab.kotlin.framework")
            appendLine("import org.myrobotlab.kotlin.utils.BiMap")
            appendLine("import org.myrobotlab.kotlin.utils.HashBiMap")

            append("\n")
            append("val classMappings: BiMap<String, String> = HashBiMap.create(mapOf(")
            append("${classMappings.map { "\"${it.first}\" to \"${it.second.value}\"" }.joinToString ( ", " )}))")
        }
        val file = environment.codeGenerator.createNewFile(
            Dependencies(
                false,
                *sourceFiles.toList().toTypedArray(),
            ),
            "org.myrobotlab.kotlin.framework",
            "mappings"
        )

        file.write(fileText.toByteArray())
        return (annotatedClasses).filterNot { it.validate() }.toList()
    }
}