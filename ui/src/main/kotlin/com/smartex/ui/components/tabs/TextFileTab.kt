package com.smartex.ui.components.tabs

import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import java.io.File
import java.nio.charset.StandardCharsets

open class TextFileTab(file: File) : FileTab(file) {
    private var savedFileContent = file.readText()
    protected val codeArea = CodeArea(savedFileContent).apply {
        isWrapText = true
    }

    private var dirty = false

    protected val findBar = FindBar(codeArea).apply {
        isVisible = false
        isManaged = false
    }

    init {
        codeArea.textProperty().addListener { _, _, new ->
            // Detect modification
            val isDirty = new != savedFileContent
            if (isDirty != dirty) {
                dirty = isDirty
                onDirtyChanged?.invoke(dirty)
            }
        }

        top = findBar

        // Add line numbers
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        // Wrap in a scrollable pane
        val scrollPane = VirtualizedScrollPane(codeArea)
        center = scrollPane
    }

    protected fun getText(): String = codeArea.text

    fun moveCursor(line: Int, col: Int = 0) {
        val paragraphIndex = (line - 1).coerceIn(0, codeArea.paragraphs.size - 1)
        val colIndex = col.coerceIn(0, codeArea.paragraphs[paragraphIndex].text.length)

        codeArea.moveTo(paragraphIndex, colIndex)
        codeArea.showParagraphAtTop(paragraphIndex)
        codeArea.requestFocus()
    }

    override fun find() {
        findBar.open()
    }

    override fun replace() {
        findBar.openReplace()
    }


    override fun save() {
        file.writer(StandardCharsets.UTF_8).use {
            val newText = getText()
            it.write(newText)
            savedFileContent = newText
        }
        dirty = false
        onDirtyChanged?.invoke(false)
    }

    override fun undo() {
        codeArea.undo()
    }

    override fun redo() {
        codeArea.redo()
    }

    override fun copy() {
        codeArea.copy()
    }

    override fun paste() {
        codeArea.paste()
    }

    override fun cut() {
        codeArea.cut()
    }
}
