@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Omit(
    val name: String,
    val fields: Array<String>,
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Pick(
    val name: String,
    val fields: Array<String>,
)

// TODO: these are a little harder
//annotation class Union
//annotation class Intersection
