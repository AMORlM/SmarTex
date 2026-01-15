package com.smartex.logging

data class LogEntry(
    val level: LogLevel,
    val title: String,
    val message: String,
    val source: String? = null,
    val lineStart: Int? = null,
    val lineEnd: Int? = null,
    val details: String? = null,
    val code: String? = null
)
