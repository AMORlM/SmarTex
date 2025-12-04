package com.smartex.ui.components.compilation

import javafx.application.Platform
import javafx.scene.control.TextArea
import java.io.OutputStream
import java.io.PrintStream

class LogView : TextArea() {
    val outputStream = PrintStream(TextAreaOutputStream(this))

    init {
        isEditable = false
    }

    fun append(text: String) {
        Platform.runLater { appendText(text) }
    }

    private class TextAreaOutputStream(private val textArea: TextArea) : OutputStream() {
        private val buffer = StringBuilder()

        override fun write(b: Int) {
            buffer.append(b.toChar())
            if (b == '\n'.code) {
                flush()
            }
        }

        override fun flush() {
            if (buffer.isNotEmpty()) {
                val text = buffer.toString()
                buffer.clear()
                Platform.runLater { textArea.appendText(text) }
            }
        }
    }
}
