package com.smartex.latexcompiler

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit

class LatexCompiler(private val projectDir: File, private val mainName: String) {

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

        // 3. Run LaTeX multiple times
        runLatexPass("1st")
        runMakeIndex()
        runLatexPass("2nd")
        runLatexPass("3rd")

        // 4. Copy resulting PDF back to project
        val outputPdf = File(buildDir, "$mainName.pdf")
        val destPdf = File(projectDir, "$mainName.pdf")

        if (outputPdf.exists()) {
            Files.copy(outputPdf.toPath(), destPdf.toPath(), StandardCopyOption.REPLACE_EXISTING)
            println("✅ Build completed. Output PDF: ${destPdf.absolutePath}")
        } else {
            println("❌ PDF not found in build directory.")
        }
    }

    private fun runLatexPass(passName: String) {
        println("====== Running LuaLaTeX: $passName pass ======")
        runCommand(
            listOf("lualatex", "--shell-escape", "--output-directory=.", "$mainName.tex"),
            buildDir
        )
    }

    private fun runMakeIndex() {
        println("====== Running makeindex on all .idx files ======")
        buildDir.listFiles { _, name -> name.endsWith(".idx") }?.forEach {
            println("Processing index file: ${it.name}")
            runCommand(listOf("makeindex", it.name), buildDir)
        }
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
}