package com.smartex.ui.settings

import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.io.File
import kotlin.collections.get

class SettingsUIBuilder(
    private val projectRoot: File,
    private val schema: Map<String, Any>,
    private val initialValues: Map<String, Any?>
) {

    val writeBuffer = initialValues.toMutableMap()

    fun build(): GridPane {
        val grid = GridPane().apply {
            styleClass.add("grid")
        }

        var row = 0
        schema.forEach { (key, rawField) ->
            val field = rawField as? Map<*, *> ?: return@forEach
            val type = field["type"] as? String ?: return@forEach
            val labelText = field["label"] as? String ?: key
            val currentValue = initialValues[key]

            val label = Label(labelText)
            val editor = when (type) {
                "string" -> createStringField(key, currentValue)
                "dropdown" -> createDropdownField(key, field, currentValue)
                "fileSelector" -> createFileSelectorField(key, field, currentValue)
                "listBuilder" -> createListBuilderField(key, field, currentValue)
                else -> Label("Unsupported type: $type")
            }

            grid.add(label, 0, row)
            grid.add(editor, 1, row)
            row++
        }

        return grid
    }

    fun createStringField(key: String, currentValue: Any?) = TextField(currentValue?.toString() ?: "").apply {
        textProperty().addListener { _, _, newValue -> writeBuffer[key] = newValue }
    }

    fun createDropdownField(key: String, field: Map<*, *>, currentValue: Any?) = ComboBox<String>().apply {
        val values = (field["values"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        items.addAll(values)
        value = currentValue?.toString() ?: field["default"]?.toString()
        valueProperty().addListener { _, _, newValue -> writeBuffer[key] = newValue }
    }

    fun createFileSelectorField(key: String, field: Map<*, *>, currentValue: Any?): ComboBox<String> {
        val extensions = (field["extensions"] as? List<*>)?.map { it.toString() } ?: emptyList()
        val files = projectRoot.walkTopDown()
            .filter { it.isFile && extensions.any { ext -> it.name.endsWith(ext) } }
            .map { it.relativeTo(projectRoot).path }
            .toList()
        return ComboBox<String>().apply {
            items.addAll(files)
            value = currentValue?.toString() ?: files.firstOrNull()
            valueProperty().addListener { _, _, newValue -> writeBuffer[key] = newValue }
        }
    }

    fun createListBuilderField(key: String, field: Map<*, *>, currentValue: Any?): VBox {
        val allowed = (field["allowedValues"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        val initialList = (currentValue as? List<*>)?.map { it.toString() }?.toMutableList() ?: mutableListOf()

        val listView = ListView<String>().apply {
            items.addAll(initialList)
            maxHeight = 100.0
        }

        val addCombo = ComboBox<String>().apply {
            items.addAll(allowed)
            promptText = "Add step…"
        }

        val addButton = Button("+").apply {
            setOnAction {
                val selectedValue = addCombo.value ?: return@setOnAction

                val insertIndex = listView.selectionModel.selectedIndex + 1
                if (insertIndex < listView.items.size) {
                    listView.items.add(insertIndex, selectedValue)
                } else {
                    listView.items.add(selectedValue)
                }

                writeBuffer[key] = listView.items.toList()
            }
        }

        val removeButton = Button("-").apply {
            setOnAction {
                val selectedIndex = listView.selectionModel.selectedIndex
                if (selectedIndex >= 0) {
                    listView.items.removeAt(selectedIndex)
                    writeBuffer[key] = listView.items.toList()
                }
            }
        }
        writeBuffer[key] = listView.items.toList()

        return VBox(6.0, listView, HBox(6.0, addCombo, addButton, removeButton))
    }
}