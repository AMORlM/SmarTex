package com.smartex.ui.components.compilation

import com.smartex.ui.components.PdfViewer
import java.io.File

class PdfViewLoader {
    private lateinit var navigationController: NavigationController
    lateinit var viewer: PdfViewer
    val isReady get() = ::viewer.isInitialized

    fun loadViewer(file: File) {
        viewer = PdfViewer(file, navigationController::PDFToTex)
    }

    fun setNavigationController(navigationController: NavigationController) {
        this.navigationController = navigationController
    }
}
