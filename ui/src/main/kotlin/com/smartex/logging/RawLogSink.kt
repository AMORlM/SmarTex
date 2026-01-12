package com.smartex.logging

interface RawLogSink {
    fun write(text: String)
    fun clear()
}