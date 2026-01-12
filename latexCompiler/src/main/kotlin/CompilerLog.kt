package com.smartex.latexcompiler

interface CompilerLog {
    fun onOut(text: String)
    fun onError(text: String)

    fun flush()
}