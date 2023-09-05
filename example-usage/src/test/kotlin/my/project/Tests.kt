package my.project

import my.project.my.namespaced.SimpleImmutableScoredValue
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.assertThrows
import java.sql.Timestamp
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Tests {
    val examplePlayer = Player("Johnny", 33, 100, "sword", listOf("$100"))
    val exampleRoutePoint = RouteWithId(
        "id1",
        "things",
        "stuff",
        listOf(Comment("hello", Timestamp.from(Instant.ofEpochMilli(1693892191946)))),
        listOf(),
        listOf(RoutePoint(1), RoutePoint(42)),
        "userId",
        RouteWithId.RouteState.IN_REVIEW,
        listOf(TrackInfo("Beastie Boys - Brass Monkey")),
    )

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
        assertIs<OnlyX>(OnlyX(x = 1))
        assertIs<OnlyY>(OnlyY(y = 1))
    }

    @Test
    fun `can namespace in packages`() {
        assertNotNull(my.project.some.namespaced.Coordinate2D(2, 3))
        assertThrows<ClassCastException> { my.project.some.namespaced.OnlyX(x = 10) as OnlyX }
    }

    @Test
    fun `complex type with multiple annotations`() {
        assertNotNull(ScoredValue("foo", 42, "foo-1") { it, t -> it + t.nano })
        assertNotNull(ImmutableScoredValue("foo", 42, "foo-2"))
        assertNotNull(SimpleImmutableScoredValue("foo", 42))
    }

    @Test
    fun `generated Pick classes contain an toXYZ method to convert back to the target class`() {
        val actual = WeaponAndInventory("sword", listOf("$100")).toPlayer(
            name = "Johnny",
            age = 33,
            health = 100,
        )
        assertEquals(examplePlayer, actual)
    }

    @Test
    fun `generated Omit classes contain an toXYZ method to convert back to the target class`() {
        val actual = NamelessTransmogrifier(42, "Calvin")
            .toTransmogrifier("Hobbes")
        assertEquals(Transmogrifier("Hobbes", 42, "Calvin"), actual)
    }

    @Test
    fun `function args are copied and retained`() {
        val polly = Bird("Polly", "Conure") { "Squak! I'm $name the $species!" }
        val unknownPolly = UnknownBirdSpecies.from(polly)
        val polly2 = unknownPolly.toBird("Conure")
        assertEquals(
            polly.speak(polly),
            polly2.speak(polly2),
        )
    }

    @Test
    fun `conversion is not lossy`() {
        // TODO: this test fails but the type signatures are the same
//        assertEquals(
//            ScoredValue("test", 1.5, "hello") { x, _ -> x },
//            ImmutableScoredValue("test", 1.5, "hello").toScoredValue { x, _ -> x }
//        )

        assertEquals(
            Coordinate2D(2, 3).toSingleAnnotationExample(4).norm(),
            SingleAnnotationExample(2, 3, 4).norm(),
        )

        assertEquals(
            examplePlayer,
            WeaponAndInventory
                .from(examplePlayer)
                .toPlayer(examplePlayer.name, examplePlayer.age, examplePlayer.health)
        )

        assertEquals(
            examplePlayer,
            NameAndInventory
                .from(examplePlayer)
                .toPlayer(examplePlayer.age, examplePlayer.health, examplePlayer.weapon)
        )

        assertEquals(
            examplePlayer,
            NameAndHealthViaOmit
                .from(examplePlayer)
                .toPlayer(examplePlayer.age, examplePlayer.weapon, examplePlayer.inventory)
        )

        assertEquals(
            examplePlayer,
            NameAndHealthViaPick
                .from(examplePlayer)
                .toPlayer(examplePlayer.age, examplePlayer.weapon, examplePlayer.inventory)
        )
    }

    @Test
    fun `generated Pick classes contain a from method for converting from the target class`() {
        assertEquals(
            WeaponAndInventory("sword", listOf("$100")),
            WeaponAndInventory.from(examplePlayer)
        )

        assertEquals(
            NameAndInventory("Johnny", listOf("$100")),
            NameAndInventory.from(examplePlayer)
        )

        assertEquals(
            NameAndHealthViaOmit("Johnny", 100),
            NameAndHealthViaOmit.from(examplePlayer)
        )

        assertEquals(
            NameAndHealthViaPick("Johnny", 100),
            NameAndHealthViaPick.from(examplePlayer)
        )
    }
}
