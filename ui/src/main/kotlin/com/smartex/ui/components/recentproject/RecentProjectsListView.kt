package com.smartex.ui.components.recentproject

import com.smartex.ui.services.RecentProjectsService
import javafx.scene.control.ListView
import javafx.scene.input.MouseButton
import javafx.util.Callback
import java.io.File

class RecentProjectsListView(
    private val onProjectSelected: (File) -> Unit
) : ListView<File>() {

    init {
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
