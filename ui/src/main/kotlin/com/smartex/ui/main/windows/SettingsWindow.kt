package com.smartex.ui.main.windows

import com.smartex.settings.SettingsManager
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File
import kotlin.collections.get

/**
 * Universal settings window that dynamically builds UI from schema.
 */
object SettingsWindow {

    // Holds UI field values before saving
    private val uiData = mutableMapOf<String, MutableMap<String, Any?>>()

    fun show(projectRoot: File) {
        val stage = Stage(StageStyle.DECORATED)
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.title = "SmarTex Settings"

        val tabPane = TabPane()

        SettingsManager.getModules().forEach { module ->

            val schema = SettingsManager.loadSchema(module)
            val projectSettings = SettingsManager.getProjectSettings(module)
            val moduleUiValues = projectSettings.toMutableMap()

            uiData[module.moduleName] = moduleUiValues

            val grid = buildModuleSettingsUI(
                schema = schema,
                projectRoot = projectRoot,
                projectSettings = projectSettings,
                writeBuffer = moduleUiValues
            )

            val tab = Tab(module.moduleName).apply {
                isClosable = false
                content = ScrollPane(grid)
            }

            tabPane.tabs.add(tab)
        }

        val saveButton = Button("Save").apply {
            setOnAction {
                SettingsManager.saveAll(uiData)
                SettingsManager.loadAll()
                stage.close()
            }
        }

        val root = BorderPane().apply {
            center = tabPane
            bottom = HBox(saveButton).apply {
                padding = Insets(10.0)
                alignment = Pos.CENTER_RIGHT
            }
        }

        stage.scene = Scene(root, 600.0, 500.0)
        stage.showAndWait()
    }


    // ----------------------------------------------------
    // UI Builder
    // ----------------------------------------------------

    private fun buildModuleSettingsUI(
        schema: Map<String, Any>,
        projectRoot: File,
        projectSettings: Map<String, Any?>,
        writeBuffer: MutableMap<String, Any?>
    ): GridPane {

        val grid = GridPane().apply {
            hgap = 12.0
            vgap = 12.0
            padding = Insets(15.0)
        }

        var row = 0

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

        // Build UI for each field
        schema.forEach { (key, rawField) ->
            val field = rawField as? Map<*, *> ?: return@forEach
            val type = field["type"] as? String ?: return@forEach
            val labelText = field["label"] as? String ?: key
            val currentValue = projectSettings[key]

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
}