package com.smartex.ui.components.filetabs

import javafx.scene.layout.BorderPane
import java.io.File

abstract class FileTab(internal val file: File) : BorderPane(){
    fun getFileName(): String = file.name
    abstract fun save()

    var onDirtyChanged: ((Boolean) -> Unit)? = null
}