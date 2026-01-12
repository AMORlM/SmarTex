package com.smartex.ui.components.compilation

import com.smartex.latexcompiler.CompilerLog

class LatexLog: CompilerLog {
    val rawLog = RawLogView()
    val richLog = RichLogView()

    override fun onOut(text: String) {
        rawLog.write(text)
    }

    override fun onError(text: String) {
        rawLog.write(text)
    }

    fun clear() {
        rawLog.clear()
        richLog.clear()
    }

    override fun flush() {
        LogProcesser(richLog).process(rawLog.text)
    }
}