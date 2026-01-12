package com.smartex.ui.components

import com.smartex.latexcompiler.LatexCompiler
import com.smartex.latexcompiler.LatexCompilerSettings
import com.smartex.ui.components.compilation.CompilationWorker
import com.smartex.ui.components.compilation.CompileToolbar
import com.smartex.ui.components.compilation.LatexLog
import com.smartex.ui.components.compilation.PdfViewLoader
import javafx.application.Platform
import javafx.scene.layout.BorderPane
import java.io.File

class CompilationPane : BorderPane() {
    private val compilerSettings = LatexCompilerSettings()
    private val pdfLoader = PdfViewLoader()
    private val logView = LatexLog()

    private val toolbar = CompileToolbar(
        onCompile = { compile() },
        onToggleView = { toggleView() },
        onSavePdf = { savePdf() }
    )

    private lateinit var compiler: LatexCompiler
    private lateinit var projectRoot: File

    init {
        top = toolbar
        center = logView.rawLog
    }

    fun setProjectRoot(projectRoot: File) {
        compiler = LatexCompiler(projectRoot, compilerSettings, logView)
        this.projectRoot = projectRoot
    }

    private fun compile() {
        logView.clear()

        CompilationWorker(
            compiler = compiler,
            outputPdf = File(projectRoot, compilerSettings.outputFile),
            onPdfReady = { pdfFile ->
                pdfLoader.loadViewer(pdfFile)
                Platform.runLater { center = pdfLoader.viewer }
            }
        ).start()
    }

    private fun toggleView() {
        if (!pdfLoader.isReady) {
            logView.onOut("PDF Viewer not initialized yet\n")
            return
        }
        center = if (center == logView) pdfLoader.viewer else logView.richLog
    }

    private fun savePdf() {
        logView.onOut("Copy PDF to other directory\n")
        // TODO: add file chooser & actual save
    }
}
