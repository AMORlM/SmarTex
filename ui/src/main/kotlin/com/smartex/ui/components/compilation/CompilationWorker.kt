package com.smartex.ui.components.compilation

import com.smartex.latexcompiler.LatexCompiler
import java.io.File

class CompilationWorker(
    private val compiler: LatexCompiler,
    private val outputPdf: File,
    private val onPdfReady: (File) -> Unit
) : Thread() {

    init {
        isDaemon = true
    }

    override fun run() {
        compiler.compile()
        onPdfReady(outputPdf)
    }
}
