package com.smartex.ui.components.compilation

import com.smartex.latexcompiler.LatexCompiler
import java.io.File
import java.io.PrintStream

class CompilationWorker(
    private val compiler: LatexCompiler,
    private val logStream: PrintStream,
    private val outputPdf: File,
    private val onPdfReady: (File) -> Unit
) : Thread() {

    init {
        isDaemon = true
    }

    override fun run() {
        System.setOut(logStream)
        compiler.compile()
        onPdfReady(outputPdf)
    }
}
