package com.smartex.ui.components.filetabs

import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import java.io.File
import java.nio.charset.StandardCharsets

open class TextFileTab(file: File) : FileTab(file) {
    private var savedFileContent = file.readText()
    protected val codeArea = CodeArea(savedFileContent)

    private var dirty = false

    init {
        codeArea.textProperty().addListener { _, _, new ->
            // Detect modification
            val isDirty = new != savedFileContent
            if (isDirty != dirty) {
                dirty = isDirty
                onDirtyChanged?.invoke(dirty)
            }
        }
    }

    init {
        // Add line numbers
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        // Wrap in a scrollable pane
        val scrollPane = VirtualizedScrollPane(codeArea)
        center = scrollPane
    }

    protected fun getText(): String = codeArea.text

    override fun save() {
        file.writer(StandardCharsets.UTF_8).use {
            val newText = getText()
            it.write(newText)
            savedFileContent = newText
        }
        dirty = false
        onDirtyChanged?.invoke(false)
    }
}
