package com.smartex.settings

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File

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
            val moduleUiValues = mutableMapOf<String, Any?>()

            uiData[module.moduleName] = moduleUiValues

            val grid = buildModuleSettingsUI(
                moduleName = module.moduleName,
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
        moduleName: String,
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

        schema.forEach { (key, rawField) ->
            val field = rawField as Map<*, *>
            val type = field["type"] as String
            val labelText = field["label"] as? String ?: key
            val currentValue = projectSettings[key]

            val label = Label(labelText)

            val editor = when (type) {

                // ----------------------
                // STRING FIELD
                // ----------------------
                "string" -> {
                    val tf = TextField(currentValue?.toString() ?: "")
                    tf.textProperty().addListener { _, _, newValue ->
                        writeBuffer[key] = newValue
                    }
                    tf
                }

                // ----------------------
                // DROPDOWN
                // ----------------------
                "dropdown" -> {
                    val values = field["values"] as List<*>
                    val combo = ComboBox<String>().apply {
                        items.addAll(values.filterIsInstance<String>())
                        value = currentValue?.toString() ?: field["default"]?.toString()
                    }
                    combo.valueProperty().addListener { _, _, newValue ->
                        writeBuffer[key] = newValue
                    }
                    combo
                }

                // ----------------------
                // FILE SELECTOR
                // ----------------------
                "fileSelector" -> {
                    val extensions = field["extensions"] as List<*>
                    val texFiles = projectRoot.walkTopDown()
                        .filter { f -> f.isFile && extensions.any { ext -> f.name.endsWith(ext.toString()) } }
                        .map { it.relativeTo(projectRoot).path }
                        .toList()

                    val combo = ComboBox<String>().apply {
                        items.addAll(texFiles)
                        value = currentValue?.toString() ?: texFiles.firstOrNull()
                    }

                    combo.valueProperty().addListener { _, _, newValue ->
                        writeBuffer[key] = newValue
                    }

                    combo
                }

                // ----------------------
                // LIST BUILDER
                // ----------------------
                "listBuilder" -> {
                    val allowed = field["allowedValues"] as List<*>
                    val initial = (currentValue as? List<*>)?.map { it.toString() }?.toMutableList()
                        ?: mutableListOf()

                    val listView = ListView<String>().apply {
                        items.addAll(initial)
                        maxHeight = 100.0
                    }

                    val addCombo = ComboBox<String>().apply {
                        items.addAll(allowed.filterIsInstance<String>())
                        promptText = "Add step…"
                    }

                    val addButton = Button("+").apply {
                        setOnAction {
                            val selected = addCombo.value ?: return@setOnAction
                            listView.items.add(selected)
                            addCombo.value = null
                            writeBuffer[key] = listView.items.toList()
                        }
                    }

                    val removeButton = Button("-").apply {
                        setOnAction {
                            val selected = listView.selectionModel.selectedItem ?: return@setOnAction
                            listView.items.remove(selected)
                            writeBuffer[key] = listView.items.toList()
                        }
                    }

                    val box = VBox(6.0,
                        listView,
                        HBox(6.0, addCombo, addButton, removeButton)
                    )

                    // store initial value
                    writeBuffer[key] = listView.items.toList()

                    box
                }

                else -> Label("Unsupported type: $type")
            }

            grid.add(label, 0, row)
            grid.add(editor, 1, row)
            row++
        }

        return grid
    }
}
