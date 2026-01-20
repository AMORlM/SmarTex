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
    private val icon = Label()
    private val title = Label()
    private val message = Label()
    private val footer = Label()

    private val header = HBox(10.0, icon, title)
    private val body = VBox(6.0, message)
    private val card = VBox(header, body, footer)

    init {
        header.styleClass += "log-header"
        body.styleClass += "log-body"
        footer.styleClass += "log-footer"
        card.styleClass += "log-card"

        listOf(title, message, footer).forEach {
            it.apply {
                isWrapText = true
                maxWidth = Double.MAX_VALUE
            }
        }

        // force width constraint
        widthProperty().addListener { _, _, newWidth ->
            card.prefWidth = newWidth.toDouble() - 20
        }
    }

    override fun updateItem(entry: LogEntry?, empty: Boolean) {
        super.updateItem(entry, empty)

        if (empty || entry == null) {
            graphic = null
            return
        }

        /* ================= HEADER ================= */

        icon.text = when (entry.level) {
            LogLevel.ERROR -> "⛔"
            LogLevel.WARN  -> "⚠"
            LogLevel.INFO  -> "ℹ"
        }

        title.text = entry.title


        header.styleClass.removeAll("log-header-error", "log-header-warn", "log-header-info")

        header.styleClass += when (entry.level) {
            LogLevel.ERROR -> "log-header-error"
            LogLevel.WARN  -> "log-header-warn"
            LogLevel.INFO  -> "log-header-info"
        }

        /* ================= BODY ================= */

        message.text = entry.message

//        val details = entry.details?.let {
//            Label(it).apply {
//                styleClass += "log-details"
//                isWrapText = true
//            }
//        }
//
//        details?.let {
//            body.children.clear()
//            body.children.addAll(message, details)
//        }

        /* ================= FOOTER ================= */

        val footerText = buildString {
            entry.source?.let { append(it) }
            if (entry.lineStart != null) {
                append(" · line ${entry.lineStart}")
                entry.lineEnd?.let { append("–$it") }
            }
        }

        footer.apply {
            text = footerText
            maxWidth = Double.MAX_VALUE
            HBox.setHgrow(this, Priority.ALWAYS)

            setOnMouseClicked {
                entry.source?.let {
                    onOpenLocation(entry.source, entry.lineStart)
                }
            }
        }

        /* ================= CARD ================= */

        graphic = card
    }
}
