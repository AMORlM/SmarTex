package com.smartex.ui.components.tabs

import com.smartex.syntaxhighlighter.LatexHighlighter
import javafx.application.Platform
import java.io.File
import java.time.Duration

class LatexEditorTab(file: File) : TextFileTab(file) {
    private val highlighter = LatexHighlighter()
    private val toolbar = LatexEditorToolbar(codeArea)

    init {
        top = toolbar
        setupHighlighting()
    }

    private fun setupHighlighting() {
        // Load highlighting CSS
        stylesheets.add(
            javaClass.getResource("/styles/latex-highlighting.css")?.toExternalForm()
        )

        // RichTextFX: listen for changes, re‑highlight with a small delay
        codeArea.richChanges()
            .filter { change -> change.inserted.text.isNotEmpty() || change.removed.text.isNotEmpty() }
            .successionEnds(Duration.ofMillis(300))
            .subscribe { highlightLaTeX() }

        highlightLaTeX()
    }

    private fun highlightLaTeX() {
        val spans = highlighter.highlight(getText())

        Platform.runLater {
            codeArea.setStyleSpans(0, spans)
        }
    }
}