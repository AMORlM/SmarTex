package com.smartex.ui.components.compilation

import com.smartex.logging.LogEntry
import com.smartex.logging.LogLevel
import com.smartex.logging.RichLogSink
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.util.Callback

class RichLogView : ListView<LogEntry>(), RichLogSink {

    init {
        cellFactory = Callback { LogCell() }
        styleClass.add("rich-log")
    }

    override fun log(entry: LogEntry) {
        Platform.runLater {
            items.add(entry)
            scrollTo(items.size - 1)
        }
    }

    override fun clear() {

    }

    private class LogCell : ListCell<LogEntry>() {

        override fun updateItem(entry: LogEntry?, empty: Boolean) {
            super.updateItem(entry, empty)

            if (empty || entry == null) {
                graphic = null
                text = null
                return
            }

            val icon = Label(iconFor(entry.level))
            icon.styleClass += "log-icon"

            val message = Label(entry.message)
            message.font = Font.font("Monospaced")
            message.wrapTextProperty().set(true)

            val header = HBox().apply {
                children.addAll(icon, message)
                padding = Insets(8.0)
            }

            val box = VBox().apply {
                children += header
                padding = Insets(4.0, 8.0, 4.0, 8.0)
            }

            // Optional details (expandable)
            entry.details?.let {
                val detailsArea = TextArea(it)
                detailsArea.isEditable = false
                detailsArea.isWrapText = true
                detailsArea.prefRowCount = 3
                detailsArea.font = Font.font("Monospaced")
                detailsArea.styleClass += "log-details"

                val toggle = Hyperlink("Show details")
                toggle.setOnAction {
                    if (box.children.contains(detailsArea)) {
                        box.children.remove(detailsArea)
                        toggle.text = "Show details"
                    } else {
                        box.children.add(detailsArea)
                        toggle.text = "Hide details"
                    }
                }

                box.children.add(toggle)
            }

            graphic = box
        }

        private fun iconFor(level: LogLevel): String =
            when (level) {
                LogLevel.ERROR -> "❌"
                LogLevel.WARN -> "⚠"
                LogLevel.INFO -> "ℹ"
            }
    }
}
