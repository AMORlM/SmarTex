package com.smartex.ui.components.tabs.latex

import com.smartex.ui.settings.EditorAction
import org.fxmisc.richtext.CodeArea

class LatexActions(
    private val codeArea: CodeArea
) {
    fun execute(action: EditorAction) {
        when (action) {
            EditorAction.BOLD -> wrapSelection("\\textbf{", "}")
            EditorAction.ITALIC -> wrapSelection("\\textit{", "}")
            EditorAction.ITEMIZE -> insertList("itemize")
            EditorAction.ENUMERATE -> insertList("enumerate")
            EditorAction.SECTION -> insertCommand("\\section{", "}")
            EditorAction.UNDERLINE -> insertCommand("\\underline{", "}")
            EditorAction.SUBSECTION -> insertCommand("\\subsection{", "}")
            EditorAction.PARAGRAPH -> insertCommand("\\paragraph{", "}")
            EditorAction.INLINE_MATH -> wrapSelection("$", "$")
            EditorAction.EQUATION -> insertEnvironment("equation")
            EditorAction.CITE -> insertCommand("\\cite{", "}")
            EditorAction.REFERENCE -> insertCommand("\\ref{", "}")
            EditorAction.HREF -> insertCommand("\\href{", "}{}")
            EditorAction.FIGURE -> insertFigure()
            else -> {}
        }
    }

    // --------------------------------------------------
    // Editing helpers
    // --------------------------------------------------

    private fun wrapSelection(prefix: String, suffix: String) {
        val selection = codeArea.selection

        if (selection.length > 0) {
            codeArea.replaceSelection(
                prefix +
                        codeArea.text.substring(selection.start, selection.end) +
                        suffix
            )
        } else {
            val pos = codeArea.caretPosition
            codeArea.insertText(pos, "$prefix$suffix")
            codeArea.moveTo(pos + prefix.length)
        }
        codeArea.requestFocus()
    }

    private fun insertCommand(prefix: String, suffix: String) {
        val pos = codeArea.caretPosition
        codeArea.insertText(pos, "$prefix$suffix")
        codeArea.moveTo(pos + prefix.length)
        codeArea.requestFocus()
    }

    private fun insertEnvironment(env: String) {
        val pos = codeArea.caretPosition

        val template = """
            \begin{$env}
            
            \end{$env}
        """.trimIndent()

        codeArea.insertText(pos, template)
        codeArea.moveTo(pos + "\\begin{$env}\n".length)
        codeArea.requestFocus()
    }

    private fun insertList(env: String) {
        val pos = codeArea.caretPosition

        val template = """
            \begin{$env}
                \item 
            \end{$env}
        """.trimIndent()

        codeArea.insertText(pos, template)
        codeArea.moveTo(pos + 19 + env.length)
        codeArea.requestFocus()
    }

    private fun insertFigure() {
        val pos = codeArea.caretPosition

        val template = """
            \begin{figure}
                \center
                \includegraphics{}
                \caption{}
                \label{fig:placeholder}
            \end{figure}
        """.trimIndent()

        codeArea.insertText(pos, template)
        codeArea.moveTo(pos + 48)
        codeArea.requestFocus()
    }
}
