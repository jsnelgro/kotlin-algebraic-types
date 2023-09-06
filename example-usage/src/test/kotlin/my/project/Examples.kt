package my.project

import io.github.jsnelgro.utility.type.annotations.Omit
import io.github.jsnelgro.utility.type.annotations.Pick
import java.sql.Timestamp
import java.time.Instant
import javax.print.attribute.standard.Media

// simple single annotation
@Omit("Coordinate2D", fields = ["z"])
data class SingleAnnotationExample(val x: Int, val y: Int, val z: Int) {
    fun norm(): Triple<Float, Float, Float> {
        val sum = (x + y + z).toFloat()
        return Triple(x / sum, y / sum, z / sum)
    }
}

@Omit("UnknownBirdSpecies", ["species"])
data class Bird(val name: String, val species: String, val speak: Bird.() -> Unit)

// multiple annotations including namespaced
@Omit("some.namespaced.Coordinate2D", fields = ["z"])
@Pick("some.namespaced.OnlyX", fields = ["x"])
@Omit("OnlyX", ["y", "z"])
@Omit("OnlyY", ["x", "z"])
data class Coordinate3D(val x: Int, val y: Int, val z: Int)


// multiple with generic param and function type
@Omit("ImmutableScoredValue", ["calcScore"])
@Omit("my.namespaced.SimpleImmutableScoredValue", ["calcScore", "name"])
data class ScoredValue<T, R : Number>(
    val value: T,
    // MAYBE TODO: mutable fields are currently converted to immutable fields...
    //  but I might leave this as is since it keeps things simpler and the dev
    //  can always convert back to the source class to do some mutation
    var score: R, val name: String,
    val calcScore: (R, Instant) -> R
)


@Pick("NameAndInventory", fields = ["name", "inventory"])
@Pick("NameAndHealthViaPick", fields = ["name", "health"])
@Pick("WeaponAndInventory", ["weapon", "inventory"])
@Omit("NameAndHealthViaOmit", fields = ["age", "inventory", "weapon"])
data class Player<T, E>(val name: String, val age: Int, val health: Int, val weapon: T, val inventory: List<E>) {
    // TODO: copy over methods and dynamic getters that are able to be copied
    fun formattedNameAndHealth() = "$name: $health"
}

fun NameAndHealthViaPick.prettyPrint() = "$name: $health"
fun NameAndHealthViaOmit.prettyPrint() = "$name: $health"

fun NameAndHealthViaPick.Companion.prettyPrint(name: String, health: Int) =
    NameAndHealthViaPick(name, health).prettyPrint()

fun NameAndHealthViaOmit.Companion.prettyPrint(name: String, health: Int) =
    NameAndHealthViaOmit(name, health).prettyPrint()


// Example from the motivating SO question:
// https://stackoverflow.com/questions/68009117/kotlin-equivalent-of-omit-typescript-utility-type/75189565#75189565
data class Comment(val body: String, val ts: Timestamp)
class RoutePoint(val id: Int) {
    fun doIO() {
        println("IO from RoutePoint class #$id")
    }
}

class TrackInfo(val title: String)

@Omit("Route", ["id"])
data class RouteWithId(
    val id: String,
    val name: String,
    val description: String,
    val comments: List<Comment>,
    val media: List<Media>,
    val points: List<RoutePoint>,
    val userId: String,
    val status: RouteState,
    val tracks: List<TrackInfo>,
) {
    enum class RouteState(val value: String) {
        IN_REVIEW("in-review"),
        PUBLISHED("published");
    }
}