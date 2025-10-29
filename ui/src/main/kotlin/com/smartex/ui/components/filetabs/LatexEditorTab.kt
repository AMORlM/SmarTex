package com.smartex.ui.components.filetabs

import com.smartex.syntaxhighlighter.LatexHighlighter
import javafx.application.Platform
import java.io.File
import java.time.Duration

class LatexEditorTab(file: File) : TextFileTab(file) {

    private val highlighter = LatexHighlighter()

    init {
        // Load highlighting CSS
        stylesheets.add(
            javaClass.getResource("/styles/latex-highlighting.css")?.toExternalForm()
        )

        // RichTextFX: listen for changes, re‑highlight with a small delay
        codeArea.richChanges()
            .filter { change -> change.inserted.text.isNotEmpty() || change.removed.text.isNotEmpty() }
            .successionEnds(Duration.ofMillis(300))
            .subscribe { _ ->
                highlightLaTeX()
            }

        highlightLaTeX()
    }

    private fun highlightLaTeX() {
        val text = getText()
        val spans = highlighter.highlight(text)

        Platform.runLater {
            codeArea.setStyleSpans(0, spans)
        }
    }

}