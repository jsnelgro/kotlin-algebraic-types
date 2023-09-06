val kspVersion: String by project

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

// see here for more info on naming:
// https://central.sonatype.org/publish/requirements/coordinates/
// seems easiest to use Github as the coordinate
group = "io.github.jsnelgro"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
//    implementation("com.squareup:javapoet:1.12.1")
    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation("com.squareup:kotlinpoet-ksp:1.14.2")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")

    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

publishing {
    publications {
        create<MavenPublication>("kotlinUtilityTypes") {
            artifactId = "utility-type-annotations"
            from(components["java"])
        }
    }

    repositories {
        // TODO: uncomment me to start investigating publishing to maven central
        //  tutorial on publishing to maven central: https://central.sonatype.org/publish/
//        mavenCentral()

        maven {
            name = "myRepo"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
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