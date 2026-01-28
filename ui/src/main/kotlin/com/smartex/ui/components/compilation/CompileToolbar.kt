package com.smartex.ui.components.compilation

import javafx.scene.control.Button
import javafx.scene.control.ToolBar

class CompileToolbar(
    private val onCompile: () -> Unit,
    private val onToggleView: () -> Unit
) : ToolBar() {

    init {
        val compileButton = Button("Compile").apply {
            setOnAction { onCompile() }
        }

        val toggleButton = Button("Toggle view").apply {
            setOnAction { onToggleView() }
        }

        items.addAll(compileButton, toggleButton)
    }
}
