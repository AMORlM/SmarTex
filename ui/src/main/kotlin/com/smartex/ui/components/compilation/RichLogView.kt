package com.smartex.ui.components.compilation

import com.smartex.logging.LogEntry
import com.smartex.logging.RichLogSink
import javafx.application.Platform
import javafx.scene.control.ListView
import javafx.util.Callback

class RichLogView : ListView<LogEntry>(), RichLogSink {

    init {
        styleClass += "rich-log"
    }

    fun setCellFactory(onOpenLocation: (file: String, line: Int?) -> Unit) {
        cellFactory = Callback { LogCell(onOpenLocation) }
    }

    override fun log(entry: LogEntry) {
        Platform.runLater {
            items += entry
        }
    }

    override fun logAll(entries: List<LogEntry>) {
        Platform.runLater {
            items.addAll(entries)
        }
    }

    override fun clear() {
        items.clear()
    }
}
