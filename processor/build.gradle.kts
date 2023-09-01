val kspVersion: String by project

plugins {
    kotlin("jvm")
}

group = "com.jsnelgro"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.squareup:javapoet:1.12.1")
    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}


//plugins {
//    kotlin("jvm")
//}
//
////group = "com.jsnelgro"
////version = "1.0-SNAPSHOT"
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.0-1.0.11")
//
//    testImplementation(platform("org.junit:junit-bom:5.9.1"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
//}
//
//tasks.test {
//    useJUnitPlatform()
//}