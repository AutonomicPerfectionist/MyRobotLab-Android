package org.myrobotlab.kotlin.ksp


import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import org.myrobotlab.kotlin.annotations.MrlService
import kotlin.reflect.KClass

class MrlServiceRegistryProcessor(private val environment: SymbolProcessorEnvironment): SymbolProcessor {
    private fun com.google.devtools.ksp.processing.Resolver.findAnnotations(
        kClass: KClass<*>,
    ) = getSymbolsWithAnnotation(
        kClass.qualifiedName.toString(), true).filterIsInstance<KSClassDeclaration>()


    override fun process(resolver: com.google.devtools.ksp.processing.Resolver): List<KSAnnotated> {
        val annotatedClasses: Sequence<KSClassDeclaration> =
            resolver.findAnnotations(MrlService::class)
        if(!annotatedClasses.iterator().hasNext()) return emptyList()
        val sourceFiles = annotatedClasses.mapNotNull { it.containingFile }
        val imports = annotatedClasses.mapNotNull {
            if (it.qualifiedName != null)
                "import ${it.qualifiedName!!.asString()}"
            else null
        }
        val filePreamble = buildString {
            appendLine("package org.myrobotlab.kotlin.framework.generated.services")
            appendLine("import kotlin.reflect.KClass")
            appendLine("import org.myrobotlab.kotlin.framework.ServiceInterface")
            imports.forEach { appendLine(it) }

            appendLine("val serviceRegistry = listOf<KClass<out ServiceInterface>>(")
        }

        val fileText = buildString {

            appendLine(annotatedClasses.map { "${it.simpleName.asString()}::class" }.joinToString(", "))
            append(")")
        }
        try {
            val file = environment.codeGenerator.createNewFile(
                Dependencies(
                    false,
                    *sourceFiles.toList().toTypedArray(),
                ),
                "org.myrobotlab.kotlin.framework.generated.services",
                "registry"
            )

            file.write((filePreamble + fileText).toByteArray())
        } catch (f: FileAlreadyExistsException) {

            f.file.appendBytes((" + listOf<KClass<*>>($fileText").toByteArray())
        }
        return annotatedClasses.filterNot { it.validate() }.toList()
    }
}