package com.smartex.ui.components.projecttree

import javafx.scene.control.Alert
import javafx.scene.control.TreeItem
import java.io.File

object ProjectFileService {

    /**
     * Renames the file if newName provided.
     * Returns the final file.
     */
    fun rename(file: File, newName: String, treeItem: TreeItem<File>): File {
        if (newName.isBlank()) return file
        val newFile = File(file.parentFile, newName)
        if (file.renameTo(newFile)) {
            treeItem.value = newFile
        } else {
            showError("Failed to rename ${file.name}")
        }

        return newFile
    }

    /**
     * Opens a prompt to onfirm the action. Deletes the file.
     * Returns the deleted file.
     */
    fun delete(file: File, treeItem: TreeItem<File>): File {
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
                    showError("Failed to delete ${file.name}")
                }
            }
        }

        return file
    }

    /**
     * Creates the new file in the same dir as the file provided.
     * Returns the new file.
     */
    fun newFile(file: File, name: String, treeItem: TreeItem<File>): File {
        val parent = if (file.isDirectory) file else file.parentFile
        val newFile = File(parent, name).apply {
            createNewFile()
        }

        ProjectTreeUtils.refreshBranch(treeItem)

        return newFile
    }

    /**
     * Creates the new file in the same dir as the file provided.
     * Returns the new file.
     */
    fun newFolder(file: File, name: String, treeItem: TreeItem<File>): File {
        val parent = if (file.isDirectory) file else file.parentFile
        val newFolder = File(parent, name).apply {
            mkdirs()
        }

        ProjectTreeUtils.refreshBranch(treeItem)

        return newFolder
    }

    private fun showError(message: String) {
        Alert(Alert.AlertType.ERROR).apply {
            title = "Error"
            headerText = null
            contentText = message
        }.showAndWait()
    }
}
