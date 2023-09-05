plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":processor"))
    ksp(project(":processor"))

    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

ksp {
    arg("option1", "value1")
    arg("option2", "value2")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}