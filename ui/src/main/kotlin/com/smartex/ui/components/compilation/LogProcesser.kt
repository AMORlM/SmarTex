package com.smartex.ui.components.compilation

import com.smartex.logging.LogEntry
import com.smartex.logging.LogLevel
import com.smartex.logging.RichLogSink

class LogProcesser(private val richLog: RichLogSink) {
    fun process(text: String) {
        richLog.log(LogEntry(LogLevel.WARN, "This is a test", "Recipe Book.tex",1,2, null, null))
    }
}