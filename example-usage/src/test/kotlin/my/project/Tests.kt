package my.project

import jsnelgro.utility.type.annotations.Omit
import jsnelgro.utility.type.annotations.Pick
import my.project.my.namespaced.SimpleImmutableScoredValue
import org.junit.jupiter.api.Assertions.assertNotNull
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertIs

class Tests {
    @Pick("NameAndInventory", fields = ["name", "inventory"])
    // TODO: unnecessary generics are not removed in the generated class
    @Pick("NameAndHealthViaPick", fields = ["name", "health"])
    @Omit("NameAndHealthViaOmit", fields = ["age", "inventory"])
    data class Player<E>(val name: String, val age: Int, val health: Int, val inventory: List<E>) {
        // TODO: methods and dynamic getters that are able to be copied over currently are not
        fun formattedNameAndHealth() = "$name: $health"
    }

    @Test
    fun `Player class pick test`() {
        assertNotNull(Player("Johnny", 33, 100, emptyList<String>()))
        assertNotNull(NameAndInventory("Johnny", listOf(1, 2, 3)))
        assertNotNull(NameAndHealthViaPick("Johnny", 500))
        assertNotNull(NameAndHealthViaOmit("Johnny", 500))
    }

    @Test
    fun `Pick generated classes contain companion object for adding extension methods`() {
        // TODO
        fun NameAndHealthViaPick.Companion.prettyPrint() = "$name: $health"
        fun NameAndHealthViaOmit.Companion.prettyPrint() = "$name: $health"

    }

    // simple single annotation
    @Omit("Coordinate2D", fields = ["z"])
    data class SingleAnnotationExample(val x: Int, val y: Int, val z: Int)

    @Test
    fun `single annotation`() {
        assertNotNull(SingleAnnotationExample(1, 2, 3))
        assertNotNull(Coordinate2D(1, 2))
    }

    // multiple annotations including namespaced
    @Omit("my.namespaced.Coordinate2D", fields = ["z"])
    @Omit("OnlyX", ["y", "z"])
    @Omit("OnlyY", ["x", "z"])
    data class Coordinate3D(val x: Int, val y: Int, val z: Int)

    @Test
    fun `multiple omit annotations`() {
        assertIs<Coordinate3D>(Coordinate3D(1, 2, 3))
        assertIs<my.project.my.namespaced.Coordinate2D>(my.project.my.namespaced.Coordinate2D(2, 3))
        assertIs<OnlyX>(OnlyX(x = 1))
        assertIs<OnlyY>(OnlyY(y = 1))
    }

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

    @Test
    fun `complex type with multiple annotations`() {
        assertNotNull(ScoredValue("foo", 42, "foo-1") { it, t -> it + t.nano })
        assertNotNull(ImmutableScoredValue("foo", 42, "foo-2"))
        assertNotNull(SimpleImmutableScoredValue("foo", 42))
    }
}
