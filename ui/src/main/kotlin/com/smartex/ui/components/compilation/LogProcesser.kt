package com.smartex.ui.components.compilation

import com.smartex.logging.LogEntry
import com.smartex.logging.LogLevel

object LogProcesser {
    fun process(text: String): List<LogEntry> {
        return listOf(
            LogEntry(LogLevel.INFO, "This is a test1", "Info simulation", "Recipes.tex",1,2, null, null),
            LogEntry(LogLevel.WARN, "This is a test2", "Warning simulation", "Recipes.tex",3,4, null, null),
            LogEntry(LogLevel.ERROR, "This is a test3", "Error simulation", "Recipes.tex",5,6, null, null)
        )
    }
}