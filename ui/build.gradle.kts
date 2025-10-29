plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application

    // Apply the JavaFX plugin to add support for building JavaFX applications.
    id("org.openjfx.javafxplugin") version "0.1.0"
}

dependencies {
    implementation("org.fxmisc.richtext:richtextfx:0.11.0")
    implementation("org.apache.pdfbox:pdfbox:2.0.30")
    implementation(project(":syntaxHighlighter"))
    implementation(project(":PDFViewer"))
    implementation(project(":latexCompiler"))
    // Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.ui.AppKt`.)
    mainClass = "com.smartex.ui.AppLauncherKt"
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.swing", "javafx.web")
}