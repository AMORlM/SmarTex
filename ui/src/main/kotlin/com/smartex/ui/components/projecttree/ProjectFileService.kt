package com.smartex.ui.components.projecttree

import javafx.scene.control.Alert
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import java.io.File

object ProjectFileService {

    fun rename(file: File, newName: String, treeItem: TreeItem<File>, treeView: TreeView<File>) {
        if (newName.isBlank()) return
        val newFile = File(file.parentFile, newName)
        if (file.renameTo(newFile)) {
            treeItem.value = newFile
        } else {
            showError("Failed to rename ${file.name}", treeView)
        }
    }

    fun delete(file: File, treeItem: TreeItem<File>, treeView: TreeView<File>) {
        val confirm = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = "Delete File"
            headerText = "Are you sure you want to delete ${file.name}?"
            contentText = "This action cannot be undone."
        }

        confirm.showAndWait().ifPresent { response ->
            if (response == javafx.scene.control.ButtonType.OK) {
                if (file.deleteRecursively()) {
                    treeItem.parent?.children?.remove(treeItem)
                } else {
                    showError("Failed to delete ${file.name}", treeView)
                }
            }
        }
    }

    fun newFile(file: File, name: String, treeItem: TreeItem<File>, treeView: TreeView<File>) {
        val parent = if (file.isDirectory) file else file.parentFile
        File(parent, name).apply {
            createNewFile()
        }

        ProjectTreeUtils.refreshBranch(treeItem)
    }

    fun newFolder(file: File, name: String, treeItem: TreeItem<File>, treeView: TreeView<File>) {
        val parent = if (file.isDirectory) file else file.parentFile
        File(parent, name).apply {
            mkdirs()
        }

        ProjectTreeUtils.refreshBranch(treeItem)
    }

    private fun showError(message: String, treeView: TreeView<File>) {
        Alert(Alert.AlertType.ERROR).apply {
            title = "Error"
            headerText = null
            contentText = message
        }.showAndWait()
    }
}
