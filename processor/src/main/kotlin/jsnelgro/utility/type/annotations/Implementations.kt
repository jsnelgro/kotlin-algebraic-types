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
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.kspDependencies
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import com.squareup.kotlinpoet.withIndent

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

fun TypeName.collectGenerics(): Set<TypeVariableName> {
    return when (this) {
        is TypeVariableName -> setOf(this)
        is ParameterizedTypeName -> this.typeArguments.flatMap { it.collectGenerics() }.toSet()
        else -> emptySet()
    }
}

fun ADTProcessor.simpleDataClassOf(
    name: String,
    params: List<KSPropertyDeclaration>,
    typeParams: List<KSTypeParameter> = emptyList(),
    annotatedClass: KSClassDeclaration,
): TypeSpec {

    val props = params.map {
        PropertySpec.builder(
            it.simpleName.getShortName(),
            it.type.toTypeName(typeParams.toTypeParameterResolver())
        ).build()
    }

    val allGenerics = typeParams.map { it.toTypeVariableName() }
    val usedGenerics = props.flatMap { it.type.collectGenerics() }
    val annotatedClassClassname = annotatedClass.toClassName().let {
        if (allGenerics.isNotEmpty()) it.parameterizedBy(allGenerics) else it
    }
    val generatedClassName = ClassName.bestGuess(name).let {
        if (usedGenerics.isNotEmpty()) it.parameterizedBy(usedGenerics) else it
    }

    val generatedClass = TypeSpec.classBuilder(name).also { klass ->
        klass.addModifiers(KModifier.DATA)
        klass.primaryConstructor(*props.toTypedArray())
        // adds generics if needed
        klass.addTypeVariables(usedGenerics)
    }.build()

    // add a companion object
    // TODO: add some helper methods to this class to make conversion easier
    val companion = TypeSpec.companionObjectBuilder().apply {
        addFunction(FunSpec.builder("from").apply {
            addTypeVariables(allGenerics)
            addParameter("source", annotatedClassClassname)
            returns(generatedClassName)
            addCode(buildCodeBlock {
                add("return ${ClassName.bestGuess(name)}(\n")
                withIndent {
                    props.forEach { add("${it.name} = source.${it.name},\n") }
                }
                add(")")
            })
        }.build())
    }.build()

    return generatedClass.toBuilder(TypeSpec.Kind.CLASS).apply {
        addType(companion)
    }.build()
}

/**
 * example of what generated helper should look like:
 *
 * fun <T, E> WeaponAndInventory.Companion.from(it: Player<T, E>): WeaponAndInventory<T, E> {
 *         return WeaponAndInventory(
 *             weapon = it.weapon,
 *             inventory = it.inventory,
 *         )
 *     }
 */

class ADTProcessor(
    private val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
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
            val dataClass = simpleDataClassOf(
                className,
                node.getAllProperties().filter { it.simpleName.getShortName() !in fieldsToOmit }.toList(),
                node.typeParameters,
                node,
            )
            addType(dataClass)
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
                    node,
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
