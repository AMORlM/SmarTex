package com.smartex.ui.components

import javafx.application.Platform
import javafx.concurrent.Task
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File

//                                              page, line, col -> do action - no return
class PdfViewer(file: File, val pdfClickHandler: (Int, Int, Int) -> Unit) : BorderPane() {
    data class PdfPageView(
        val pageIndex: Int,
        val imageView: ImageView,
        val pageWidthPts: Float,
        val pageHeightPts: Float,
        val dpi: Float
    )

    val scrollPane = ScrollPane()
    val contentBox = VBox(10.0)

    init {
        scrollPane.content = contentBox
        scrollPane.isFitToWidth = true

        try {
            val document = PDDocument.load(file)
            val renderer = PDFRenderer(document)

            val task = object : Task<Unit>() {
                override fun call() {
                    val dpi = 150f

                    for (pageIndex in 0 until document.numberOfPages) {

                        val page = document.getPage(pageIndex)
                        val mediaBox = page.mediaBox

                        val bufferedImage = renderer.renderImageWithDPI(pageIndex, dpi)
                        val fxImage = SwingFXUtils.toFXImage(bufferedImage, null)

                        val imageView = ImageView(fxImage).apply {
                            isPreserveRatio = true
                            fitWidthProperty().bind(scrollPane.widthProperty().subtract(20))
                        }

                        val pageView = PdfPageView(
                            pageIndex = pageIndex,
                            imageView = imageView,
                            pageWidthPts = mediaBox.width,
                            pageHeightPts = mediaBox.height,
                            dpi = dpi
                        )

                        imageView.setOnMouseClicked { event ->
                            handlePdfClick(pageView, event.x, event.y)
                        }

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

    private fun handlePdfClick(page: PdfPageView, imageX: Double, imageY: Double) {
        val imageView = page.imageView
        val bounds = imageView.boundsInLocal

        // Actual rendered image size
        val renderedWidthPx = bounds.width
        val renderedHeightPx = bounds.height

        // Scale from ImageView pixels -> PDF points
        val scaleX = page.pageWidthPts / renderedWidthPx
        val scaleY = page.pageHeightPts / renderedHeightPx

        val pdfX = (imageX * scaleX).toInt()

        // Flip Y axis (JavaFX top-left -> PDF bottom-left)
        val pdfY = ((renderedHeightPx - imageY) * scaleY).toInt()

        val pageNumber = page.pageIndex + 1 // SyncTeX is 1-based

        pdfClickHandler(pageNumber, pdfX, pdfY)
    }

    fun goToPage(pageNumber: Int) {
        val pageIndex = pageNumber - 1

        if (pageIndex !in contentBox.children.indices) return

        Platform.runLater {
            contentBox.applyCss()
            contentBox.layout()

            val node = contentBox.children[pageIndex]

            val maxScroll =
                contentBox.height - scrollPane.viewportBounds.height

            if (maxScroll > 0) {
                scrollPane.vvalue =
                    node.boundsInParent.minY / maxScroll
            }
        }
    }

}
//import com.smartex.pdfviewer.PDFViewer
//import javafx.application.Platform
//import javafx.scene.layout.BorderPane
//import java.io.File
//
//class PdfViewer(file: File) : BorderPane() {
//
//    private lateinit var viewer: PDFViewer
//
//    init {
//        Platform.runLater {
//            viewer = PDFViewer(file.parentFile)
//            viewer.loadPdf(file.name)
//            center = viewer
//        }
//    }
//
//    fun reload() = viewer.reload()
//    fun stop() = viewer.stopServer()
//}
