import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.validate
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import java.io.OutputStream

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

class ADTProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
) : SymbolProcessor {
    class ClassVisitor(val fn: (node: KSClassDeclaration, data: Unit) -> Unit) : KSEmptyVisitor<Unit, Unit>() {
        override fun defaultHandler(node: KSNode, data: Unit) = error("only class declarations supported")
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) =
            fn(classDeclaration, data)
    }

    private fun processClassDeclarations(
        annotationPath: String,
        resolver: Resolver,
        visitorFn: (node: KSClassDeclaration) -> Unit
    ): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(annotationPath)
        val ret = symbols.filter { !it.validate() }.toList()
        symbols
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(ClassVisitor { node, _ -> visitorFn(node) }, Unit) }
        return ret
    }

    private fun processOmit(resolver: Resolver): List<KSAnnotated> =
        processClassDeclarations("Omit", resolver) { node ->
            val packageName = node.containingFile!!.packageName.asString()
            val className = "${node.simpleName.asString()}Omit"
            val file = codeGenerator.createNewFile(Dependencies(true, node.containingFile!!), packageName, className)
            if (packageName.isNotBlank()) {
                file.appendText("package $packageName\n\n")
            }
            file.appendText("data class $className(val todo: String = \"implement me!\")\n")
            file.close()
        }

    private fun processPick(resolver: Resolver): List<KSAnnotated> =
        processClassDeclarations("Pick", resolver) { node ->
            val packageName = node.containingFile!!.packageName.asString()
            val className = "${node.simpleName.asString()}Pick"
            val file = codeGenerator.createNewFile(Dependencies(true, node.containingFile!!), packageName, className)
            if (packageName.isNotBlank()) {
                file.appendText("package $packageName\n\n")
            }
            file.appendText("data class $className(val todo: String = \"implement me!\")\n")
            file.close()
        }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        return processOmit(resolver) + processPick(resolver)
    }
}

class ADTProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ADTProcessor(environment.codeGenerator, environment.logger)
    }
}

//class JavaBuilderProcessor : AbstractProcessor() {
//    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
//        TODO("Not yet implemented")
//    }
//}
