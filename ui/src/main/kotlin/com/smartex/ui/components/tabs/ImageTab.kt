package com.smartex.ui.components.tabs

import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import java.io.File

class ImageTab(file: File) : FileTab(file) {

    private val imageView = ImageView()

    init {
        try {
            val image = Image(file.toURI().toString(), true)

            imageView.image = image
            imageView.isPreserveRatio = true
            imageView.isSmooth = true

            imageView.fitWidthProperty().bind(widthProperty())
            imageView.fitHeightProperty().bind(heightProperty())

            center = imageView

        } catch (_: Exception) {
            center = Label("Could not load image: ${file.name}")
        }
    }

    override fun save() {
        // Images are not edited/saved by SmarTex
    }

    override fun copy() {
        copyToClipboard(file.path)
    }

    private fun copyToClipboard(text: String) {
        val content = ClipboardContent().apply {
            putString(text)
        }

        Clipboard.getSystemClipboard().setContent(content)
    }
}