package com.smartex.logging

interface RichLogSink {
    fun log(entry: LogEntry)
    fun clear()
}
