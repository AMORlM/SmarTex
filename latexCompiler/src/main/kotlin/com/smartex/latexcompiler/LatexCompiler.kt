package com.smartex.latexcompiler

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class LatexCompiler(
    private val projectDir: File,
    private val settings: LatexCompilerSettings,
    private val log: CompilerLog) {

    private val buildDir = File(projectDir, "../_build").canonicalFile

    fun compile() {
        log.onOut("====== LaTeX Compilation Started ======\n")

        // 1. Clean and recreate build directory
        if (buildDir.exists()) {
            log.onOut("Cleaning old build directory: ${buildDir.absolutePath}\n")
            buildDir.deleteRecursively()
        }
        buildDir.mkdirs()

        // 2. Copy project to build dir
        log.onOut("Copying project files to ${buildDir.absolutePath}\n")
        projectDir.copyRecursively(buildDir)

        // 3. Run Compilation sequence
        var count = 1
        for (step in settings.executionOrder) {
            val exitCode = when (step) {
                "compile" -> runLatexPass(numberToOrder(count++))
                "index"   -> runMakeIndex()
                "bib"     -> runBib()
                else      -> return
            }

            if (exitCode != 0) {
                log.flush()
                return
            }
        }

        // 4. Copy resulting PDF back to project
        val outputPdf = File(buildDir, settings.outputFile)
        val destPdf = File(projectDir, settings.outputFile)

        if (outputPdf.exists()) {
            Files.copy(outputPdf.toPath(), destPdf.toPath(), StandardCopyOption.REPLACE_EXISTING)
            log.onOut("✅ Build completed. Output PDF: ${destPdf.absolutePath}\n")
        } else {
            log.onError("❌ PDF not found in build directory.\n")
        }
        log.flush()
    }

    fun runPDFToTex(page: Int, x: Int, y: Int): InvertedSearch {
        val process = ProcessBuilder(listOf("synctex", "edit", "-o", "$page:$x:$y:${settings.outputFile}"))
            .directory(buildDir)
            .redirectErrorStream(true)
            .start()

        var line = 0
        var col = 0
        var input = ""

        process.inputStream.bufferedReader().useLines {
            it.forEach { entry ->
                val index = entry.indexOf(':')

                if (index < 0)
                    return@forEach

                val key = entry.substring(0, index)
                val value = entry.substring(index+1)
                when (key) {
                    "Line" -> line = value.toInt()
                    "Column" -> col = value.toInt()
                    "Input" -> input = value.substring(value.indexOf('.') + 1)
                }
            }
        }
        return InvertedSearch(line, col, input)
    }

    fun runTexToPDF(file: String, line: Int, col: Int): Int {
        val process = ProcessBuilder(listOf("synctex", "view",
                "-i", "$line:$col:\"$file\"",
                "-o", settings.outputFile)
        )
            .directory(buildDir)
            .redirectErrorStream(true)
            .start()

        process.inputStream.toString()
        return 1
    }

    private fun runLatexPass(passName: String): Int {
        log.onOut("====== Running ${settings.compiler}: $passName pass ======\n")
        val output = settings.outputFile.replace(".pdf", "")
        if (settings.mainFile == null) {
            log.onError("Main file not found. Please define a main file in the compiler settings.")
            return -1
        }
        return runCommand(
            listOf(
                settings.compiler,          // selected latex compiler
                "--synctex=1",              // needed to make wiring from .tex <-> pdf
                "--enable-installer",       // automatically install missing packages
                "--shell-escape",           // enables system commands (needed for some lua script code)
                "--job-name=$output",       // defines pdf name
                settings.mainFile!!         // definned main file
            ),
            buildDir
        )
    }

    private fun runMakeIndex(): Int {
        log.onOut("====== Running makeindex on all .idx files ======\n")
        buildDir.listFiles { _, name -> name.endsWith(".idx") }?.forEach {
            log.onOut("Processing index file: ${it.name}\n")

            val exitCode = runCommand(
                listOf("makeindex", it.name),
                buildDir)

            if (exitCode != 0) {
                return exitCode
            }
        }
        return 0
    }

    private fun runBib(): Int {
        log.onOut("====== Running makeindex on all .idx files ======\n")
        return runCommand(
            listOf("bibtex", settings.mainFile!!.replace(".tex", "")),
            buildDir)
    }

    private fun runCommand(command: List<String>, workingDir: File): Int {
        try {
            val process = ProcessBuilder(command)
                .directory(workingDir)
                .redirectErrorStream(true)
                .start()

            process.inputStream.bufferedReader().useLines {
                it.forEach { line -> log.onOut(line + '\n') }
            }

            return process.waitFor()
        } catch (e: IOException) {
            log.onError("❌ Failed to run command: ${command.joinToString(" ")}\n${e.message}\n")
            return -1
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