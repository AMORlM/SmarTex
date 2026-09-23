package com.smartex.ui.components

import com.smartex.service.ProjectPathResolver
import com.smartex.ui.components.tabs.FileTab
import com.smartex.ui.components.tabs.FileTabFactory
import com.smartex.ui.components.tabs.TextFileTab
import com.smartex.ui.components.tabs.functionality.ClipboardEditable
import com.smartex.ui.components.tabs.functionality.SearchableSupport
import com.smartex.ui.components.tabs.functionality.Undoable
import javafx.application.Platform
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import java.io.File

class FileTabPane : TabPane() {

    val currentTab
        get() = selectionModel.selectedItem?.content as? FileTab

    private val openFiles = mutableMapOf<File, Tab>()

    private lateinit var pathResolver: ProjectPathResolver

    fun setPathResolver(pathResolver: ProjectPathResolver) {
        this.pathResolver = pathResolver
    }
    
    fun openFile(file: File): Tab {
        openFiles[file]?.let {
            selectionModel.select(it)
            return it
        }

        val editorTab = FileTabFactory.createFileTab(file, pathResolver)
        val tab = Tab(editorTab?.getFileName(), editorTab).apply {
            isClosable = true
            setOnClosed { openFiles.remove(file) }
        }

        editorTab?.onDirtyChanged = { dirty ->
            tab.text = if (dirty) "${editorTab.getFileName()}*" else editorTab.getFileName()
        }

        openFiles[file] = tab
        tabs.add(tab)
        selectionModel.select(tab)
        return tab
    }

    fun openFile(file: File, line: Int?, col: Int = 0) {
        val tab = openFile(file)

        if (line == null) return

        Platform.runLater {
            (tab.content as? TextFileTab)?.moveCursor(line, col)
        }
    }

    fun closeFile(file: File) {
        tabs.remove(openFiles[file])
        openFiles.remove(file)
    }

    fun fileIsOpen(file: File) = openFiles.containsKey(file)
    
    fun saveCurrentFile() = currentTab?.save()

    fun undo() = (currentTab as? Undoable)?.undo()
    fun redo() = (currentTab as? Undoable)?.redo()

    fun cut() = (currentTab as? ClipboardEditable)?.cut()
    fun copy() = (currentTab as? ClipboardEditable)?.copy()
    fun paste() = (currentTab as? ClipboardEditable)?.paste()

    fun openFind() = (currentTab as? SearchableSupport)?.openFind()
    fun openReplace() = (currentTab as? SearchableSupport)?.openReplace()
    fun openToLine() = (currentTab as? SearchableSupport)?.openToLine()
}