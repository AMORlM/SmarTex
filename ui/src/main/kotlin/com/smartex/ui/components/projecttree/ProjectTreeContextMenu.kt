package com.smartex.ui.components.projecttree

import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import java.io.File

class ProjectTreeContextMenu(
    private val renameCallback:(File, File) -> Unit,
    private val newFileCallback: (File) -> Unit,
    private val deleteCallback: (File) -> Unit
) {

    fun create(treeView: TreeView<File>, file: File, treeItem: TreeItem<File>): ContextMenu {
        val renameItem = MenuItem("Rename").apply {
            setOnAction {
                InlineTextPopup.show(treeView.scene.window, "Rename", file.name) { newName ->
                    val newFile = ProjectFileService.rename(file, newName, treeItem)
                    renameCallback(file, newFile)
                }
            }
        }

        val deleteItem = MenuItem("Delete").apply {
            setOnAction {
                val delFile = ProjectFileService.delete(file, treeItem)
                deleteCallback(delFile)
            }
        }

        val newFileItem = MenuItem("New file").apply {
            setOnAction {
                InlineTextPopup.show(treeView.scene.window, "New File", "untitled") { name ->
                    val newFile = ProjectFileService.newFile(file, name, treeItem)
                    treeItem.isExpanded = true
                    newFileCallback(newFile)
                }
            }
        }

        val newFolderItem = MenuItem("New folder").apply {
            setOnAction {
                InlineTextPopup.show(treeView.scene.window, "New Folder", "NewFolder") { name ->
                    ProjectFileService.newFolder(file, name, treeItem)
                    treeItem.isExpanded = true
                }
            }
        }

        return ContextMenu(renameItem, deleteItem, newFileItem, newFolderItem)
    }
}
