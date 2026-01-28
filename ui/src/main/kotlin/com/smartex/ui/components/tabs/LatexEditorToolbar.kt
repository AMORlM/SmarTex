package com.smartex.ui.components.tabs

import javafx.scene.control.Button
import javafx.scene.control.ToolBar
import org.fxmisc.richtext.CodeArea

class LatexEditorToolbar(
    private val codeArea: CodeArea
) : ToolBar() {

    init {
        items += createButton("B") {
            wrapSelection("\\textbf{", "}")
        }

        items += createButton("I") {
            wrapSelection("\\textit{", "}")
        }

        items += createButton("•") {
            insertEnvironment("itemize")
        }

        items += createButton("1.") {
            insertEnvironment("enumerate")
        }

        items += createButton("§") {
            insertCommand("\\section{", "}")
        }
    }

    private fun createButton(label: String, action: () -> Unit): Button =
        Button(label).apply {
            isFocusTraversable = false
            setOnAction { action() }
        }

    /** Wraps selected text or inserts empty command */
    private fun wrapSelection(prefix: String, suffix: String) {
        val selection = codeArea.selection

        if (selection.length > 0) {
            codeArea.replaceSelection("$prefix${codeArea.text.substring(selection.start, selection.end)}$suffix")
        } else {
            val pos = codeArea.caretPosition
            codeArea.insertText(pos, "$prefix$suffix")
            codeArea.moveTo(pos + prefix.length)
        }

        codeArea.requestFocus()
    }

    /** Inserts commands like \section{}, \subsection{}, etc */
    private fun insertCommand(prefix: String, suffix: String) {
        val pos = codeArea.caretPosition
        codeArea.insertText(pos, "$prefix$suffix")
        codeArea.moveTo(pos + prefix.length)
        codeArea.requestFocus()
    }

    /** Inserts LaTeX environments */
    private fun insertEnvironment(env: String) {
        val pos = codeArea.caretPosition

        val template = """
            \begin{$env}
                
            \end{$env}
        """.trimIndent()

        codeArea.insertText(pos, template)

        // Move cursor inside environment body
        codeArea.moveTo(pos + "\\begin{$env}\n".length)

        codeArea.requestFocus()
    }
}
