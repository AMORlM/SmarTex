package com.smartex.ui.components.tabs.latex

import com.smartex.ui.settings.EditorAction
import com.smartex.ui.settings.ShortcutSettings
import javafx.scene.control.Button
import javafx.scene.control.Separator
import javafx.scene.control.ToolBar
import javafx.scene.control.Tooltip
import javafx.util.Duration

class LatexEditorToolbar(
    private val actions: LatexActions
) : ToolBar() {

    init {
        styleClass += "latex-toolbar"

        // ─── Text styling ───────────────────────────────
        items += iconButton("𝐁", "Bold (${ShortcutSettings.getShortcut(EditorAction.BOLD)})") {
            actions.execute(EditorAction.BOLD)
        }

        items += iconButton("𝑰", "Italic (${ShortcutSettings.getShortcut(EditorAction.ITALIC)})") {
            actions.execute(EditorAction.ITALIC)
        }

        items += iconButton("𝑈", "Underline") {
            actions.execute(EditorAction.UNDERLINE)
        }

        items += Separator()

        // ─── Lists ──────────────────────────────────────
        items += iconButton("•", "Bullet list") {
            actions.execute(EditorAction.ITEMIZE)
        }

        items += iconButton("1.", "Numbered list") {
            actions.execute(EditorAction.ENUMERATE)
        }

        items += Separator()

        // ─── Structure ──────────────────────────────────
        items += iconButton("§", "Section") {
            actions.execute(EditorAction.SECTION)
        }

        items += iconButton("§§", "Subsection") {
            actions.execute(EditorAction.SUBSECTION)
        }

        items += iconButton("¶", "Paragraph") {
            actions.execute(EditorAction.PARAGRAPH)
        }

        items += Separator()

        // ─── Math ───────────────────────────────────────
        items += iconButton("∑", "Inline math ($...$)") {
            actions.execute(EditorAction.INLINE_MATH)
        }

        items += iconButton("∫", "Display math") {
            actions.execute(EditorAction.EQUATION)
        }

        items += Separator()

        // ─── Citations ───────────────────────────────────
        items += iconButton("C", "Insert citation") {
            actions.execute(EditorAction.CITE)
        }

        items += iconButton("R", "Insert cross reference") {
            actions.execute(EditorAction.REFERENCE)
        }

        items += iconButton("L", "Insert link") {
            actions.execute(EditorAction.HREF)
        }

        items += iconButton("F", "Insert figure") {
            actions.execute(EditorAction.FIGURE)
        }
    }

    // --------------------------------------------------
    // Button helpers
    // --------------------------------------------------

    private fun iconButton(
        icon: String,
        tooltipText: String,
        action: () -> Unit
    ): Button =
        Button(icon).apply {
            isFocusTraversable = false
            styleClass += "latex-toolbar-button"

            tooltip = Tooltip(tooltipText).apply {
                showDelay = Duration.millis(300.0)
            }

            setOnAction { action() }
        }
}
