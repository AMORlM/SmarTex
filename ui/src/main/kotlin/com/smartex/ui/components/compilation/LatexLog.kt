package com.smartex.ui.components.compilation

import com.smartex.latexcompiler.CompilerLog
import javafx.application.Platform
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

class LatexLog: TabPane(), CompilerLog {
    private val rawLog = RawLogView()
    private val richLog = RichLogView()

    private val rawLogTab = Tab("Raw", rawLog).apply {
        isClosable = false
    }
    private val richLogTab = Tab("Rich", richLog).apply {
        isClosable = false
    }

    init {
        tabs.add(rawLogTab)
    }

    fun setCallback(onOpenLocation: (file: String, line: Int?) -> Unit) {
        richLog.setCellFactory(onOpenLocation)
    }

    override fun onOut(text: String) {
        rawLog.write(text)
    }

    override fun onError(text: String) {
        rawLog.write(text)
    }

    fun clear() {
        rawLog.clear()
        richLog.clear()
        tabs.remove(richLogTab)
    }

    override fun flush() {
        richLog.logAll(LogProcesser.process(rawLog.text))
        Platform.runLater {
            tabs.add(richLogTab)
        }
    }
}