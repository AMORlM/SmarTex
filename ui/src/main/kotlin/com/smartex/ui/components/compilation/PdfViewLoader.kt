package com.smartex.ui.components.compilation

import com.smartex.ui.components.PdfViewer
import java.io.File

class PdfViewLoader {
    lateinit var viewer: PdfViewer
    val isReady get() = ::viewer.isInitialized

    fun loadViewer(file: File) {
        viewer = PdfViewer(file)
    }
}
