package com.smartex.ui.components.tabs

import javafx.scene.layout.BorderPane
import java.io.File

abstract class FileTab(internal val file: File) : BorderPane(){
    fun getFileName(): String = file.name
    abstract fun save()

    var onDirtyChanged: ((Boolean) -> Unit)? = null

    open fun undo(){}
    open fun redo(){}

    open fun copy(){}
    open fun paste(){}
    open fun cut(){}
}