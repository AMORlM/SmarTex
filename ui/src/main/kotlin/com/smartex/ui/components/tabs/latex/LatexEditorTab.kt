package com.smartex.ui.components.tabs.latex

import com.smartex.syntaxhighlighter.LatexHighlighter
import com.smartex.ui.components.tabs.FindBar
import com.smartex.ui.components.tabs.TextFileTab
import com.smartex.ui.settings.EditorAction
import com.smartex.ui.settings.ShortcutService
import com.smartex.ui.settings.ShortcutSettings
import javafx.application.Platform
import javafx.scene.layout.VBox
import java.io.File
import java.time.Duration

class LatexEditorTab(file: File) : TextFileTab(file) {
    private val highlighter = LatexHighlighter()
    private  val actions = LatexActions(codeArea)
    private val toolbar = LatexEditorToolbar(actions)

    private val topBar = VBox(
        toolbar,
        findBar
    )

    init {
        top = topBar
        setupShortcuts()
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

    private fun setupShortcuts() {
        sceneProperty().addListener { _, _, scene ->
            if (scene != null) {
                val shortcuts = ShortcutService(scene, ShortcutSettings)

                shortcuts.makeShortcut(EditorAction.BOLD)
                shortcuts.makeShortcut(EditorAction.ITALIC)
            }
        }
    }

    private fun ShortcutService.makeShortcut(action: EditorAction) =
        bind(action) {actions.execute(action)}
}