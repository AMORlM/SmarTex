package com.smartex.ui.components.filetabs

import javafx.application.Platform
import javafx.concurrent.Task
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File

class PdfViewerTab(file: File) : FileTab(file) {

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

    init {
        val scrollPane = ScrollPane()
        val contentBox = VBox(10.0)
        scrollPane.content = contentBox
        scrollPane.isFitToWidth = true

        try {
            val document = PDDocument.load(file)
            val renderer = PDFRenderer(document)

            val task = object : Task<Unit>() {
                override fun call() {
                    for (pageIndex in 0 until document.numberOfPages) {
                        val bufferedImage = renderer.renderImageWithDPI(pageIndex, 150f)
                        val fxImage = SwingFXUtils.toFXImage(bufferedImage, null)
                        val imageView = ImageView(fxImage)
                        imageView.isPreserveRatio = true
                        imageView.fitWidthProperty().bind(scrollPane.widthProperty().subtract(20)) // Adjust for padding
                        Platform.runLater {
                            contentBox.children.add(imageView)
                        }
                    }
                }

                override fun succeeded() {
                    super.succeeded()
                    document.close()
                }

                override fun failed() {
                    super.failed()
                    document.close()
                }
            }

            Thread(task).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        center = scrollPane
    }

    override fun save() {}
}
