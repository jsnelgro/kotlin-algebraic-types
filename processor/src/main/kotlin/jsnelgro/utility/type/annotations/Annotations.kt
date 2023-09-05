package jsnelgro.utility.type.annotations

/**
 * Generate a new data class with the given [name], omitting any properties listed in [fields]
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Omit(
    val name: String,
    val fields: Array<String>,
)

/**
 * Generate a new data class with the given [name], containing only the properties listed in [fields]
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Pick(
    val name: String,
    val fields: Array<String>,
)

// TODO: these probably are a little harder to get right. Also questionable how useful they are.
//  Can use Kotlin's sealed classes for Unions.
//annotation class Union

//  TODO: maybe Intersection is a better one to tackle next.
//    Think you could pretty much just reuse Pick/Omit logic to dump all the fields from both into a new class
//    e.g. @Intersection(ClassB::class, pick = [], omit = ["y"]) data class ClassA(val x: Int)
//    pick and omit args are optional. probs Omit takes precedence to avoid accidental PII leakage?
//annotation class Intersection
