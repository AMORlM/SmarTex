package com.smartex.ui.components.compilation

import javafx.scene.control.Button
import javafx.scene.control.ToolBar

class CompileToolbar(
    private val onCompile: () -> Unit,
    private val onToggleView: () -> Unit,
    private val onSavePdf: () -> Unit
) : ToolBar() {

    init {
        val compileButton = Button("Compile").apply {
            setOnAction { onCompile() }
        }

        val toggleButton = Button("Toggle view").apply {
            setOnAction { onToggleView() }
        }

        val savePDFButton = Button("Save File").apply {
            setOnAction { onSavePdf() }
        }

        items.addAll(compileButton, toggleButton, savePDFButton)
    }
}
