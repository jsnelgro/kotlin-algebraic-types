plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":processor"))
    ksp(project(":processor"))
}

ksp {
    arg("option1", "value1")
    arg("option2", "value2")
}

//plugins {
//    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
//    kotlin("jvm")
//}
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    implementation(kotlin("stdlib-jdk8"))
//    implementation(project(":processor"))
//    ksp(project(":processor"))
//}

//plugins {
//    id("java")
//}
//
//group = "com.jsnelgro"
//version = "unspecified"
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    testImplementation(platform("org.junit:junit-bom:5.9.1"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
//}
//
//tasks.test {
//    useJUnitPlatform()
//}