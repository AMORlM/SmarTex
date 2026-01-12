plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application

    // Apply the JavaFX plugin to add support for building JavaFX applications.
    id("org.openjfx.javafxplugin") version "0.1.0"

    // Apply to make uber jar
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    // Kotlin coroutines core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Optional: for JavaFX integration (Dispatchers.Main)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.7.3")
    implementation("org.fxmisc.richtext:richtextfx:0.11.0")
    implementation("org.apache.pdfbox:pdfbox:2.0.31")

    implementation(project(":syntaxHighlighter"))
    implementation(project(":latexCompiler"))
    implementation(project(":PDFViewer"))
    implementation(project(":settings"))
    // Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.ui.AppKt`.)
    mainClass = "com.smartex.ui.AppLauncherKt"
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.swing", "javafx.fxml")
}

tasks {
    shadowJar {
        archiveBaseName.set("Smartex")
        archiveClassifier.set("") // so the jar is not named *-all.jar
        mergeServiceFiles()       // fixes JavaFX META-INF issues
    }
}
