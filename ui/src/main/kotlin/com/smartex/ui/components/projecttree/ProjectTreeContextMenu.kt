package com.smartex.ui.components.projecttree

import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import java.io.File

object ProjectTreeContextMenu {

    fun create(treeView: TreeView<File>, file: File, treeItem: TreeItem<File>): ContextMenu {
        val renameItem = MenuItem("Rename").apply {
            setOnAction {
                InlineTextPopup.show(treeView.scene.window, "Rename", file.name) { newName ->
                    ProjectFileService.rename(file, newName, treeItem, treeView)
                }
            }
        }

        val deleteItem = MenuItem("Delete").apply {
            setOnAction { ProjectFileService.delete(file, treeItem, treeView) }
        }

        val newFileItem = MenuItem("New file").apply {
            setOnAction {
                InlineTextPopup.show(treeView.scene.window, "New File", "untitled") { name ->
                    ProjectFileService.newFile(file, name, treeItem, treeView)
                    treeItem.isExpanded = true
                }
            }
        }

        val newFolderItem = MenuItem("New folder").apply {
            setOnAction {
                InlineTextPopup.show(treeView.scene.window, "New Folder", "NewFolder") { name ->
                    ProjectFileService.newFolder(file, name, treeItem, treeView)
                    treeItem.isExpanded = true
                }
            }
        }

        return ContextMenu(renameItem, deleteItem, newFileItem, newFolderItem)
    }
}
