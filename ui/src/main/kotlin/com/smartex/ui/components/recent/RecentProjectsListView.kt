package com.smartex.ui.components.recent

import com.smartex.ui.project.RecentProjectsService
import javafx.scene.control.ListView
import javafx.scene.input.MouseButton
import javafx.util.Callback
import java.io.File

class RecentProjectsListView: ListView<File>() {

    fun setOnOpen(onProjectSelected: (File) -> Unit) {
        items.addAll(RecentProjectsService.getRecent())

        cellFactory = Callback {
            RecentProjectCell()
        }

        setOnMouseClicked { event ->
            if (event.button == MouseButton.PRIMARY) {
                val selected = selectionModel.selectedItem
                selected?.let { onProjectSelected(it) }
            }
        }
    }
}
