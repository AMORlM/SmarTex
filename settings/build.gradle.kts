plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the JavaFX plugin to add support for building JavaFX applications.
    id("org.openjfx.javafxplugin") version "0.1.0"
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.+")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.+")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.+")
}


javafx {
    version = "21"
    modules = listOf("javafx.controls")
}