package my.project

import jsnelgro.utility.type.annotations.Omit
import jsnelgro.utility.type.annotations.Pick
import java.time.Instant

// simple single annotation
@Omit("Coordinate2D", fields = ["z"])
data class SingleAnnotationExample(val x: Int, val y: Int, val z: Int)

// multiple annotations including namespaced
@Omit("some.namespaced.Coordinate2D", fields = ["z"])
@Omit("OnlyX", ["y", "z"])
@Omit("OnlyY", ["x", "z"])
data class Coordinate3D(val x: Int, val y: Int, val z: Int)


// multiple with generic param and function type
@Omit("ImmutableScoredValue", ["calcScore"])
@Omit("my.namespaced.SimpleImmutableScoredValue", ["calcScore", "name"])
data class ScoredValue<T, R : Number>(
    val value: T,
    // TODO: mutable fields are currently converted to immutable fields
    var score: R,
    val name: String,
    // TODO: function types are currently ignored
    val calcScore: (R, Instant) -> R
)


@Pick("NameAndInventory", fields = ["name", "inventory"])
@Pick("NameAndHealthViaPick", fields = ["name", "health"])
@Pick("WeaponAndInventory", ["weapon", "inventory"])
@Omit("NameAndHealthViaOmit", fields = ["age", "inventory", "weapon"])
data class Player<T, E>(val name: String, val age: Int, val health: Int, val weapon: T, val inventory: List<E>) {
    // TODO: methods and dynamic getters that are able to be copied over currently are not
    fun formattedNameAndHealth() = "$name: $health"
}

fun NameAndHealthViaPick.prettyPrint() = "$name: $health"
fun NameAndHealthViaOmit.prettyPrint() = "$name: $health"

fun NameAndHealthViaPick.Companion.prettyPrint(it: NameAndHealthViaPick) = it.prettyPrint()
fun NameAndHealthViaOmit.Companion.prettyPrint(it: NameAndHealthViaOmit) = it.prettyPrint()
