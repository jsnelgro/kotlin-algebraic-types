package my.project

import jsnelgro.utility.type.annotations.Omit
import jsnelgro.utility.type.annotations.Pick

// generate SimplePerson class with only name and age fields
@Pick("SimplePerson", fields = ["name", "age"])
// generates an AgelessPerson class without the age field
@Omit("AgelessPerson", fields = ["age"])
@Omit("UnsavedPerson", fields = ["id"])
data class Person(val id: Long, val name: String, val age: Int, val heightCm: Int, val weight: Int)

@Omit("NamelessTransmogrifier", fields = ["name"])
data class Transmogrifier<T, R>(val name: String, val dialSetting: T, val subject: R)

fun main() {
    listOf(
        Person(id = 1, name = "Brian", age = 28, heightCm = 170, weight = 175),
        UnsavedPerson(name = "Brian", age = 28, heightCm = 170, weight = 175),
        AgelessPerson(1, "Johnny", 170, 175),
        SimplePerson("Johnny", 33),
        Transmogrifier("hi", "hello", "world"),
        NamelessTransmogrifier(SimplePerson("Calvin", 8), "Calvin"),
    ).forEach { println("$it") }
}