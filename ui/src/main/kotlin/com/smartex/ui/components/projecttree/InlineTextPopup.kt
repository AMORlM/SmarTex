package com.smartex.ui.components.projecttree

import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import javafx.stage.Popup
import javafx.stage.Window

object InlineTextPopup {

    fun show(owner: Window, message: String, initialValue: String, onAction: (String) -> Unit) {
        val label = Label(message)

        val textField = TextField(initialValue).apply {
            prefWidth = 240.0
            padding = Insets(6.0)
        }

        val root = VBox(label, textField).apply { padding = Insets(4.0) }

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

        popup.show(owner)
        textField.requestFocus()
        textField.selectAll()
    }
}
