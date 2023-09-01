plugins {
    kotlin("jvm")
}

subprojects {
    repositories {
        mavenCentral()
    }
}

// // attempt 1
//plugins {
//    kotlin("jvm") version "1.9.0"
////    application
//}
//
//buildscript {
//    dependencies {
//        classpath(kotlin("gradle-plugin", version = "1.9.0"))
//    }
//}

//subprojects {
//    repositories {
//        mavenCentral()
//    }
//}

//group = "com.jsnelgro"
//version = "1.0-SNAPSHOT"

//repositories {
//    mavenCentral()
//}

//dependencies {
//    testImplementation(kotlin("test"))
//}

//tasks.test {
//    useJUnitPlatform()
//}

//kotlin {
//    jvmToolchain(8)
//}

//application {
//    mainClass.set("MainKt")
//}