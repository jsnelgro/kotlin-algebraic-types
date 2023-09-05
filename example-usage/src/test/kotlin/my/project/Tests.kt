package my.project

import my.project.my.namespaced.SimpleImmutableScoredValue
import org.junit.jupiter.api.Assertions.assertNotNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Tests {
    val examplePlayer = Player("Johnny", 33, 100, "sword", listOf("$100"))

    @Test
    fun `Player class pick test`() {
        assertNotNull(examplePlayer)
        assertNotNull(NameAndInventory("Johnny", listOf(1, 2, 3)))
        assertNotNull(NameAndHealthViaPick("Johnny", 500))
        assertNotNull(NameAndHealthViaOmit("Johnny", 500))
    }

    @Test
    fun `Pick generated classes contain companion object for adding extension methods`() {
        assertEquals(NameAndHealthViaPick.prettyPrint("Johnny", 120), "Johnny: 120")
        assertEquals(NameAndHealthViaOmit.prettyPrint("Johnny", 120), "Johnny: 120")
    }

    @Test
    fun `single annotation`() {
        assertNotNull(SingleAnnotationExample(1, 2, 3))
        assertNotNull(Coordinate2D(1, 2))
    }

    @Test
    fun `multiple omit annotations and namespaces`() {
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

    @Test
    fun `generated Pick classes contain a toXYZ method to convert back to the target class`() {
        // TODO: implement me!
//        val actual = WeaponAndInventory("sword", listOf("$100")).toPlayer(
//            name = "Johnny",
//            age = 33,
//            health = 100,
//        )
//        assertEquals(examplePlayer, actual)
    }

    @Test
    fun `generated Pick classes contain a from method for converting from the target class`() {
        val expected = WeaponAndInventory("sword", listOf("$100"))
        val actual = WeaponAndInventory.from(examplePlayer)
        assertEquals(
            expected,
            actual,
        )
    }
}
