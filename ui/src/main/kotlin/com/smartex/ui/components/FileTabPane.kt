package com.smartex.ui.components

import com.smartex.ui.components.tabs.FileTab
import com.smartex.ui.components.tabs.FileTabFactory
import com.smartex.ui.components.tabs.TextFileTab
import javafx.application.Platform
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import java.io.File

class FileTabPane : TabPane() {

    private val openFiles = mutableMapOf<File, Tab>()

    fun openFile(file: File): Tab {
        openFiles[file]?.let {
            selectionModel.select(it)
            return it
        }

        val editorTab = FileTabFactory.createFileTab(file)
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

    fun saveCurrentFile() = getCurrentEditor()?.save()

    private fun getCurrentEditor(): FileTab? {
        val selectedTab = selectionModel.selectedItem ?: return null
        return selectedTab.content as? FileTab
    }

    override fun resize(p0: Double, p1: Double) {
        super.resize(p0, p1)

        // Notify the current editor about the resize
        getCurrentEditor()?.resize(p0, p1)
    }
}