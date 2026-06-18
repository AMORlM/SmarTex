package com.smartex.ui.components.compilation.log

import com.smartex.logging.RawLogSink
import javafx.application.Platform
import javafx.scene.control.TextArea

class RawLogView : TextArea(), RawLogSink {
    init {
        isEditable = false
        isWrapText = true
    }

    override fun write(text: String) {
        Platform.runLater { appendText(text) }
    }
}
