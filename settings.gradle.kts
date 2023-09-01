pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    plugins {
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

//plugins {
//    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
//}

rootProject.name = "kotlin-algebraic-types"
include("processor")
include("example-usage")
