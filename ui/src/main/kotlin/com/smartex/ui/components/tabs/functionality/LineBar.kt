package com.smartex.ui.components.tabs.functionality

import com.smartex.ui.components.utils.InlineTextPopup
import org.fxmisc.richtext.CodeArea

class LineBar(
    val codeArea: CodeArea,
    val moveCursor: (line: Int, col: Int) -> Unit
) {
    fun openToLine() {
        InlineTextPopup.show(codeArea.scene.window, "Go to line:", "Line[:col]") {
            text ->
                val parts = text.trim().split(":")
                val line = parts.getOrNull(0)?.toIntOrNull() ?: return@show

                val col = parts.getOrNull(1)?.toIntOrNull() ?: 0

                moveCursor(line, col)
        }
    }
}