package com.smartex.ui.components

import com.smartex.ui.components.tabs.FileTab
import com.smartex.ui.components.tabs.FileTabFactory
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import java.io.File

class FileTabPane : TabPane() {

    private val openFiles = mutableMapOf<File, Tab>()

    fun openFile(file: File) {
        if (openFiles.containsKey(file)) {
            selectionModel.select(openFiles[file])
            return
        }

        val editorTab = FileTabFactory.createFileTab(file)
        val tab = Tab(editorTab?.getFileName(), editorTab).apply {
            isClosable = true
            setOnClosed {
                openFiles.remove(file)
            }
        }
        editorTab?.onDirtyChanged = { dirty ->
            tab.text = if (dirty) editorTab.getFileName() + "*" else editorTab.getFileName()
        }

        openFiles[file] = tab
        tabs.add(tab)
        selectionModel.select(tab)
    }

    fun closeFile(file: File) {
        tabs.remove(openFiles[file])
        openFiles.remove(file)
    }


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