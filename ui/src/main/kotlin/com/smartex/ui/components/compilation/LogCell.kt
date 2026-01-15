package com.smartex.ui.components.compilation

import com.smartex.logging.LogEntry
import com.smartex.logging.LogLevel
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class LogCell(
    private val onOpenLocation: (file: String, line: Int?) -> Unit
) : ListCell<LogEntry>() {
    private var expanded = false



    override fun updateItem(entry: LogEntry?, empty: Boolean) {
        super.updateItem(entry, empty)

        if (empty || entry == null) {
            graphic = null
            return
        }

        println("called")

        /* ================= HEADER ================= */

        val icon = Label(
            when (entry.level) {
                LogLevel.ERROR -> "⛔"
                LogLevel.WARN  -> "⚠"
                LogLevel.INFO  -> "ℹ"
            }
        ).apply {
            styleClass += "log-header-icon"
        }

        val title = Label(entry.title).apply {
            styleClass += "log-header-title"
        }

        val header = HBox(10.0, icon, title).apply {
            styleClass += listOf(
                "log-header",
                when (entry.level) {
                    LogLevel.ERROR -> "log-header-error"
                    LogLevel.WARN  -> "log-header-warn"
                    LogLevel.INFO  -> "log-header-info"
                }
            )
        }

        /* ================= BODY ================= */

        val message = Label(entry.message).apply {
            styleClass += "log-body-text"
        }

        val details = entry.details?.let {
            Label(it).apply {
                styleClass += "log-details"
                isVisible = expanded
                isManaged = expanded
            }
        }

        val body = VBox(6.0, message).apply {
            styleClass += "log-body"
            if (details != null) children += details
        }

        /* ================= FOOTER ================= */

        val footerText = buildString {
            entry.source?.let { append(it) }
            if (entry.lineStart != null) {
                append(" · line ${entry.lineStart}")
                entry.lineEnd?.let { append("–$it") }
            }
        }

        val footer = Label(footerText).apply {
            styleClass += "log-footer"
            maxWidth = Double.MAX_VALUE
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        /* ================= CARD ================= */

        val card = VBox(header, body, footer).apply {
            styleClass += "log-card"

            setOnMouseClicked {
                println("${entry.source}, ${entry.lineStart}")
                if (entry.source != null) {
                    onOpenLocation(entry.source, entry.lineStart)
                }
            }
        }

        /* ================= EXPAND TOGGLE ================= */

        header.setOnMouseClicked {
            expanded = !expanded
            details?.apply {
                isVisible = expanded
                isManaged = expanded
            }
        }

        graphic = card
    }
}
