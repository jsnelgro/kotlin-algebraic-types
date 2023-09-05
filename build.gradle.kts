plugins {
    kotlin("jvm") apply false
}

subprojects {
    repositories {
        mavenCentral()
    }
}

//dependencies {
//    implementation(kotlin("stdlib"))
//
//    testImplementation(kotlin("test"))
//}
//
//tasks.test {
//    useJUnitPlatform()
//}

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

//kotlin {
//    jvmToolchain(8)
//}
