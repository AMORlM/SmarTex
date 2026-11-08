package com.smartex.ui.components.tabs

import com.smartex.ui.components.tabs.functionality.*
import com.smartex.ui.components.tabs.functionality.LineBreak.*
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import java.io.File
import java.io.FileReader
import java.nio.charset.Charset

open class TextFileTab(file: File) : FileTab(file),
    Undoable,
    ClipboardEditable,
    SearchableSupport,
    TextEditorStatusSupport {
    private var savedFileContent = replaceWithLineFeed(file.readText())     // IDE works with LF as line break
    protected val codeArea = CodeArea(savedFileContent).apply {
        isWrapText = true
    }

    private var dirty = false

    protected val findBar = FindBar(codeArea).apply {
        isVisible = false
        isManaged = false
    }

    protected val goTo = LineBar(codeArea, ::moveCursor)

    private val obsEncoding = SimpleObjectProperty(getFileEncoder())
    private val obsLineBreak = SimpleObjectProperty(getFileLineBreak())
    private val obsLineCol = SimpleObjectProperty(
        Pos(codeArea.currentParagraph + 1, codeArea.caretColumn + 1)
    )

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

        // Add caret position listener
        codeArea.caretPositionProperty().addListener {
            obsLineCol.value = Pos(codeArea.currentParagraph + 1, codeArea.caretColumn + 1)
        }

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

    override fun save() {
        file.writer(obsEncoding.value).use {                 // encode support
            val newText = getText()
            it.write(replaceWithLineBreak(newText))     // save with file line break
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

    override fun openFind() {
        findBar.open()
    }

    override fun openReplace() {
        findBar.openReplace()
    }

    override fun openToLine() {
        goTo.openToLine()
    }

    override fun getObsEncoder(): SimpleObjectProperty<Charset> {
        return obsEncoding
    }

    override fun getObsLineBreak(): SimpleObjectProperty<LineBreak> {
        return obsLineBreak
    }

    override fun getObsLineCol(): ObservableValue<Pos> {
        return obsLineCol
    }

    private fun getFileEncoder(): Charset {
        return Charset.forName(FileReader(file).encoding)
    }

    private fun getFileLineBreak(): LineBreak {
        val text = file.readText(obsEncoding.value)

        var lf = 0
        var cr = 0
        var crlf = 0
        var i = 0

        while (i < text.length) {
            when (text[i]) {
                '\r' -> {
                    if (i + 1 < text.length && text[i + 1] == '\n') {
                        crlf++
                        i++
                    } else {
                        cr++
                    }
                }
                '\n' -> lf++
            }
            i++
        }

        return when {
            crlf > cr && crlf > lf -> CRLF
            cr > lf -> CR
            else -> LF
        }
    }

    private fun replaceWithLineBreak(text: String): String {
        if (obsLineBreak.value != LF)
            text.replace(oldValue = LF.value, newValue = obsLineBreak.value.value)
        return text
    }

    private fun replaceWithLineFeed(text: String): String {
        text.replace(oldValue = CRLF.value, newValue = LF.value)
        text.replace(oldValue = CR.value, newValue = LF.value)
        return text
    }
}