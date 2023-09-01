package my.project

import Omit
import Pick

// would generate SimplePerson class with only name and age fields
@Pick("SimplePerson", fields = ["name", "age"])
// would generate an AgelessPerson class without the age field
@Omit("AgelessPerson", fields = ["age"])
data class Person(val name: String, val age: Int, val heightCm: Int, val weight: Int)

fun main(args: Array<String>) {
    val person1 = Person(name = "Brian", age = 28, heightCm = 170, weight = 170)
    val agelessPerson = PersonOmit("I did it!!!!")
    println("$person1\n$agelessPerson")
}