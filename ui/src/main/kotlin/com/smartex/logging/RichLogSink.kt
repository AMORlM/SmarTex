package com.smartex.logging

interface RichLogSink {
    fun log(entry: LogEntry)
    fun logAll(entries: List<LogEntry>)
    fun clear()
}
