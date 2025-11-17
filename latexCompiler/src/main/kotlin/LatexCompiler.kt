package com.smartex.latexcompiler

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit

class LatexCompiler(private val projectDir: File, private val settings: LatexCompilerSettings) {

    private val buildDir = File(projectDir, "../_build").canonicalFile

    fun compile() {
        println("=== LaTeX Compilation Started ===")

        // 1. Clean and recreate build directory
        if (buildDir.exists()) {
            println("Cleaning old build directory: ${buildDir.absolutePath}")
            buildDir.deleteRecursively()
        }
        buildDir.mkdirs()

        // 2. Copy project to build dir
        println("Copying project files to ${buildDir.absolutePath}")
        projectDir.copyRecursively(buildDir)

        // 3. Run Compilation sequence
        var count = 1
        for (step in settings.executionOrder) {
            when (step) {
                "compile" -> runLatexPass(numberToOrder(count++))
                "index"   -> runMakeIndex()
                "bib"     -> runBib()
            }
        }

        // 4. Copy resulting PDF back to project
        val outputPdf = File(buildDir, settings.outputFile)
        val destPdf = File(projectDir, settings.outputFile)

        if (outputPdf.exists()) {
            Files.copy(outputPdf.toPath(), destPdf.toPath(), StandardCopyOption.REPLACE_EXISTING)
            println("✅ Build completed. Output PDF: ${destPdf.absolutePath}")
        } else {
            println("❌ PDF not found in build directory.")
        }
    }

    private fun runLatexPass(passName: String) {
        println("====== Running LuaLaTeX: $passName pass ======")
        val output = settings.outputFile.replace(".pdf", "")
        runCommand(
            listOf(settings.compiler, "--shell-escape", "--job-name=$output", settings.mainFile!!),
            buildDir
        )
    }

    private fun runMakeIndex() {
        println("====== Running makeindex on all .idx files ======")
        buildDir.listFiles { _, name -> name.endsWith(".idx") }?.forEach {
            println("Processing index file: ${it.name}")
            runCommand(
                listOf("makeindex", it.name),
                buildDir)
        }
    }

    private fun runBib() {
        println("====== Running makeindex on all .idx files ======")
        runCommand(
            listOf("bibtex", settings.mainFile!!.replace(".tex", "")),
            buildDir)
    }

    private fun runCommand(command: List<String>, workingDir: File) {
        try {
            val process = ProcessBuilder(command)
                .directory(workingDir)
                .redirectErrorStream(true)
                .start()

            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { println(it) }
            }

            if (!process.waitFor(5, TimeUnit.MINUTES)) {
                process.destroy()
                println("⚠️ Command timed out: ${command.joinToString(" ")}")
            }

        } catch (e: IOException) {
            println("❌ Failed to run command: ${command.joinToString(" ")}\n${e.message}")
        }
    }

    private fun numberToOrder(number: Int): String {
        return when(number%10) {
            1 -> "${number}st"
            2 -> "${number}nd"
            3 -> "${number}rd"
            else -> "${number}th"
        }
    }
}