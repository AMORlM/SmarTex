package com.smartex.ui.components.projecttree

import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import javafx.stage.Popup
import javafx.stage.Window

object InlineTextPopup {

    fun show(owner: Window, message: String, initialValue: String, onAction: (String) -> Unit) {
        // Message label
        val label = Label(message).apply {
            style = """
                -fx-text-fill: white; 
            """.trimIndent()
        }

        // Input field
        val textField = TextField(initialValue).apply {
            prefWidth = 240.0
            padding = Insets(6.0)
            style = """
                    -fx-background-color: #3c3f41;
                    -fx-text-inner-color: white;
                    -fx-border-color: #555555;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                    -fx-font-size: 13px;
            """.trimIndent()
        }

        // Root container
        val root = VBox(label, textField).apply {
            padding = Insets(4.0)
            style = """
                    -fx-background-color: #3c3f41;
                    -fx-text-inner-color: white;
                    -fx-border-color: #555555;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                    -fx-font-size: 13px;
            """.trimIndent()
        }


        val popup = Popup().apply {
            isAutoHide = true
            isAutoFix = true
            content.add(root)
        }

        textField.setOnKeyPressed {
            when (it.code) {
                KeyCode.ENTER -> { onAction(textField.text); popup.hide() }
                KeyCode.ESCAPE -> popup.hide()
                else -> {}
            }
        }

        // Position the popup over the owner window (centered)
        val ownerBounds = owner.scene?.window?.let { window ->
            Point2D(window.x + window.width / 2 - 120, window.y + window.height / 2 - 30)
        } ?: Point2D(100.0, 100.0)
        popup.show(owner, ownerBounds.x, ownerBounds.y)
        textField.requestFocus()
        textField.selectAll()
    }
}
