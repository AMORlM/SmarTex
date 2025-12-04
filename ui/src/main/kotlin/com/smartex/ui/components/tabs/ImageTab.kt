package com.smartex.ui.components.tabs


import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.io.File

class ImageTab(file: File) : FileTab(file) {

    private val imageView = ImageView()

    init {
        try {
            val image = Image(file.toURI().toString(), true)

            imageView.image = image
            imageView.isPreserveRatio = true
            imageView.isSmooth = true

            // Bind size of image to the parent Pane
            imageView.fitWidthProperty().bind(widthProperty())
            imageView.fitHeightProperty().bind(heightProperty())

            center = imageView

        } catch (_: Exception) {
            center = Label("Could not load image: ${file.name}")
        }
    }

    override fun save() {
        // No saving functionality for images
    }
}
