package com.smartex.ui.components.recent

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import java.io.File

class RecentProjectCell(private val onRemove: (File) -> Unit) : ListCell<File>() {
    private val nameLabel = Label().apply {
        style = "-fx-font-size: 15px; -fx-font-weight: bold;"
    }
    private val pathLabel = Label().apply {
        style = "-fx-font-size: 11px; -fx-text-fill: #aaaaaa;"
    }

    private val labelsBox = VBox(nameLabel, pathLabel).apply {
        spacing = 2.0
    }

    private val removeButton = Button("✕").apply {
        style += "-fx-text-fill: red;"
        styleClass.add("close")
        setOnAction {
            item?.let { onRemove(it) }
        }
    }

    private val container = HBox(labelsBox, removeButton).apply {
        spacing = 10.0
        HBox.setHgrow(labelsBox, Priority.ALWAYS)
        alignment = javafx.geometry.Pos.CENTER_LEFT
    }

    private val home = System.getProperty("user.home")

    override fun updateItem(item: File?, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty || item == null) {
            text = null
            graphic = null
        } else {
            nameLabel.text = item.name
            pathLabel.text = getShortPath(item.path)
            graphic = container
        }
    }

    private fun getShortPath(path: String) =
        if (path.startsWith(home)) path.replace(home, "~") else path
}