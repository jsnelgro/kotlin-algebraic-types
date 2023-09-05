package jsnelgro.utility.type.annotations

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.validate
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.kspDependencies
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import java.io.OutputStream

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

// shorthand for generating a constructor of properties (i.e. data class constructor)
fun TypeSpec.Builder.primaryConstructor(vararg properties: PropertySpec): TypeSpec.Builder {
    val propertySpecs = properties.map { it.toBuilder().initializer(it.name).build() }
    val parameters = propertySpecs.map { ParameterSpec.builder(it.name, it.type).build() }
    val constructor = FunSpec.constructorBuilder()
        .addParameters(parameters)
        .build()

    return this
        .primaryConstructor(constructor)
        .addProperties(propertySpecs)
}

@OptIn(KspExperimental::class)
inline fun <reified T : Annotation> processClassesWithAnnotation(
    resolver: Resolver,
    crossinline visitorFn: (node: KSClassDeclaration, annotationData: T) -> Unit
): List<KSAnnotated> {
    val symbols = resolver.getSymbolsWithAnnotation(T::class.qualifiedName!!)
    val ret = symbols.filter { !it.validate() }.toList()
    symbols
        .filter { it is KSClassDeclaration && it.validate() }
        .forEach {
            it.accept(ClassVisitor { node, _ ->
                node.getAnnotationsByType(T::class).forEach { ann ->
                    visitorFn(node, ann)
                }
            }, Unit)
        }
    return ret
}

class ClassVisitor(val fn: (node: KSClassDeclaration, data: Unit) -> Unit) : KSEmptyVisitor<Unit, Unit>() {
    override fun defaultHandler(node: KSNode, data: Unit) = error("only class declarations supported")
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) =
        fn(classDeclaration, data)
}

fun simpleDataClassOf(
    name: String,
    params: List<KSPropertyDeclaration>,
    typeParams: List<KSTypeParameter> = emptyList(),
): TypeSpec {
    return TypeSpec.classBuilder(name).apply {
        addModifiers(KModifier.DATA)
        // adds generics if needed
        addTypeVariables(typeParams.map { it.toTypeVariableName() })
        val props = params.map {
            PropertySpec.builder(
                it.simpleName.getShortName(),
                it.type.toTypeName(typeParams.toTypeParameterResolver())
            ).build()
        }.toTypedArray()
        primaryConstructor(*props)
    }.build()
}

class ADTProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private fun processOmit(
        resolver: Resolver
    ): List<KSAnnotated> = processClassesWithAnnotation<Omit>(resolver) { node, ann ->
        val fieldsToOmit = ann.fields.toSet()
        val (pkg, className) = ann.name.split(".").let {
            buildString {
                append(node.containingFile!!.packageName.asString())
                append(".")
                append(it.dropLast(1).joinToString("."))
            } to it.last()
        }
        val builder = FileSpec.builder(pkg, className).apply {
            addType(
                simpleDataClassOf(
                    className,
                    node.getAllProperties().filter { it.simpleName.getShortName() !in fieldsToOmit }.toList(),
                    node.typeParameters,
                )
            )
        }.build()

        builder.writeTo(codeGenerator, builder.kspDependencies(true))
    }

    private fun processPick(
        resolver: Resolver
    ): List<KSAnnotated> = processClassesWithAnnotation<Pick>(resolver) { node, ann ->
        val fieldsToPick = ann.fields.toSet()
        val builder = FileSpec.builder(node.containingFile!!.packageName.asString(), ann.name).apply {
            addType(
                simpleDataClassOf(
                    ann.name,
                    node.getAllProperties().filter { it.simpleName.getShortName() in fieldsToPick }.toList(),
                    node.typeParameters,
                )
            )
        }.build()

        builder.writeTo(codeGenerator, builder.kspDependencies(true))
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
