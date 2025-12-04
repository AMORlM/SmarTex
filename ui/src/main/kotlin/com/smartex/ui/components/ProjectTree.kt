package com.smartex.ui.components

import com.smartex.ui.components.projecttree.ProjectTreeCell
import com.smartex.ui.components.projecttree.ProjectTreeUtils
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.MouseButton
import javafx.util.Callback
import java.io.File

class ProjectTree: TreeView<File>() {
    fun setOnFileSelected(onFileSelected: (File) -> Unit) {
        // Handle file selection
        this.setOnMouseClicked { event ->
            if (event.button == MouseButton.PRIMARY) {
                val selectedItem = selectionModel.selectedItem
                if (selectedItem != null && selectedItem.value.isFile) {
                    onFileSelected(selectedItem.value)
                }
            }
        }

        // Set custom cell factory
        this.cellFactory = Callback {
            ProjectTreeCell(this)
        }
    }

    fun populateFromDirectory(rootDir: File) {
        val rootItem = ProjectTreeUtils.buildTreeItem(rootDir)
        this.root = rootItem
        this.isShowRoot = true
        rootItem.isExpanded = true
    }

    fun refreshBranch(item: TreeItem<File>) {
        ProjectTreeUtils.refreshBranch(item)
    }
}
