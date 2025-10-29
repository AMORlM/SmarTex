package com.smartex.ui.components.filetabs

//package com.smartex.ui.components.filetabs
//
//import com.smartex.pdfviewer.PDFViewer
//import java.io.File
//
//class PdfViewerTab(file: File) : FileTab(file) {
//
//    private var viewer: PDFViewer = PDFViewer(file.parentFile)
//
//    init {
//        viewer.loadPdf(file.absolutePath) // for testing; replace with 'file' in production
//        center = viewer
//    }
//
//    override fun save() {
//        // read-only
//    }
//
//    fun reload() = viewer.reload()
//
//    fun stopServer() {
//        viewer.stopServer()
//    }
//}
