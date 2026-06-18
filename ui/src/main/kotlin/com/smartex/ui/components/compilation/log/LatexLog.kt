package com.smartex.ui.components.compilation.log

import com.smartex.latexcompiler.CompilerLog
import com.smartex.logging.LogLevel
import javafx.application.Platform
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

class LatexLog: TabPane(), CompilerLog {
    private val rawLog = RawLogView()
    private val richLog = RichLogView()
    private val richErrorLog = RichLogView()
    private val richWarnLog = RichLogView()
    private val richInfoLog = RichLogView()

    private val rawLogTab = Tab("Raw", rawLog).apply {
        isClosable = false
    }
    private val richLogTab = Tab("Rich", richLog).apply {
        isClosable = false
    }
    private val richErrorLogTab = Tab("Error", richErrorLog).apply {
        isClosable = false
    }
    private val richWarnLogTab = Tab("Warn", richWarnLog).apply {
        isClosable = false
    }
    private val richInfoLogTab = Tab("Info", richInfoLog).apply {
        isClosable = false
    }

    init {
        tabs.add(rawLogTab)
    }

    fun setCallback(onOpenLocation: (file: String, line: Int?) -> Unit) {
        richLog.setCellFactory(onOpenLocation)
        richErrorLog.setCellFactory(onOpenLocation)
        richWarnLog.setCellFactory(onOpenLocation)
        richInfoLog.setCellFactory(onOpenLocation)
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
        richInfoLog.clear()
        richWarnLog.clear()
        richErrorLog.clear()
        tabs.removeAll(richLogTab, richErrorLogTab, richWarnLogTab, richInfoLogTab)
    }

    override fun flush() {
        val entries = LogProcesser.process(rawLog.text)

        val errors = entries.filter { it.level == LogLevel.ERROR }
        val warns  = entries.filter { it.level == LogLevel.WARN }
        val infos  = entries.filter { it.level == LogLevel.INFO }

        richLog.logAll(entries)
        richErrorLog.logAll(errors)
        richWarnLog.logAll(warns)
        richInfoLog.logAll(infos)

        Platform.runLater {
            richLogTab.setTitle("Rich", entries.size)
            richErrorLogTab.setTitle("Error", errors.size)
            richWarnLogTab.setTitle("Warn", warns.size)
            richInfoLogTab.setTitle("Info", infos.size)

            tabs.add(richLogTab)
            tabs.add(richErrorLogTab)
            tabs.add(richWarnLogTab)
            tabs.add(richInfoLogTab)
        }
    }


    private fun Tab.setTitle(base: String, count: Int) {
        text = "$base ($count)"
    }

}