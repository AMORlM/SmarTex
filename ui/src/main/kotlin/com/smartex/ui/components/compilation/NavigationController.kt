package com.smartex.ui.components.compilation

import com.smartex.latexcompiler.LatexCompiler

class NavigationController(
    private val compiler: LatexCompiler,
    private val openFileCallback: (String, Int?, Int ) -> Unit
)
{
    // lateinit var pdfViewer: PdfViewer

    fun PDFToTex(page: Int, x: Int, y: Int) {
        compiler.runPDFToTex(page, x, y).let {
            openFileCallback(it.file, it.line, it.col)
        }
    }

    fun texToPDF(file: String, line: Int, col: Int) {
        //pdfViewer.goToPage(compiler.runTexToPDF(file, line, col))
    }

    fun openFile(filename: String, line: Int?, col: Int) = openFileCallback(filename, line, col)
}