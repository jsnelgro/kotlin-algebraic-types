package my.project

import my.project.my.namespaced.SimpleImmutableScoredValue
import org.junit.jupiter.api.Assertions.assertNotNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Tests {
    @Test
    fun `Player class pick test`() {
        assertNotNull(Player("Johnny", 33, 100, "sword", emptyList<String>()))
        assertNotNull(NameAndInventory("Johnny", listOf(1, 2, 3)))
        assertNotNull(NameAndHealthViaPick("Johnny", 500))
        assertNotNull(NameAndHealthViaOmit("Johnny", 500))
    }

    @Test
    fun `Pick generated classes contain companion object for adding extension methods`() {
        assertEquals(NameAndHealthViaPick.prettyPrint(NameAndHealthViaPick("Johnny", 120)), "Johnny: 120")
        assertEquals(NameAndHealthViaOmit.prettyPrint(NameAndHealthViaOmit("Johnny", 120)), "Johnny: 120")
    }

    @Test
    fun `single annotation`() {
        assertNotNull(SingleAnnotationExample(1, 2, 3))
        assertNotNull(Coordinate2D(1, 2))
    }

    @Test
    fun `multiple omit annotations`() {
        assertIs<Coordinate3D>(Coordinate3D(1, 2, 3))
        assertIs<my.project.some.namespaced.Coordinate2D>(my.project.some.namespaced.Coordinate2D(2, 3))
        assertIs<OnlyX>(OnlyX(x = 1))
        assertIs<OnlyY>(OnlyY(y = 1))
    }

    @Test
    fun `complex type with multiple annotations`() {
        assertNotNull(ScoredValue("foo", 42, "foo-1") { it, t -> it + t.nano })
        assertNotNull(ImmutableScoredValue("foo", 42, "foo-2"))
        assertNotNull(SimpleImmutableScoredValue("foo", 42))
    }
}
