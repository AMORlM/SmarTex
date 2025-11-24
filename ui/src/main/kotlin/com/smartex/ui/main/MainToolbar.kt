package com.smartex.ui.main

import com.smartex.settings.SettingsManager
import com.smartex.ui.main.windows.SettingsWindow
import com.smartex.ui.components.CompilationPane
import com.smartex.ui.components.FileTabPane
import com.smartex.ui.components.ProjectTree
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.TextField
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.stage.DirectoryChooser
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File

class MainToolbar(
    private val stage: Stage,
    private val projectTree: ProjectTree,
    private val fileTabPane: FileTabPane,
    private val splitPane: SplitPane
) : ToolBar() {

    val settingsButton = Button("Settings")

    init {
        val newProjButton = Button("New Project").apply {
            setOnAction {
                showNewProjectWindow()
            }
        }

        val openProjButton = Button("Open Project").apply {
            setOnAction {
                val selectedDirectory = DirectoryChooser().apply {
                    title = "Open Project"
                    initialDirectory = File(System.getProperty("user.home"))
                }.showDialog(stage)

                selectedDirectory?.let { file ->
                    openProject(file)
                }
            }
        }

        val saveButton = Button("Save File").apply {
            setOnAction { fileTabPane.saveCurrentFile() }
        }


        items.addAll(newProjButton, openProjButton, saveButton, settingsButton)
    }


    fun openProject(file: File) {
        projectTree.populateFromDirectory(file)
        splitPane.items[2] = CompilationPane(file)
        SettingsManager.init(file, file)
        SettingsManager.loadAll()
        settingsButton.apply {
            setOnAction { SettingsWindow.show(file) }
        }
    }


    fun showNewProjectWindow() {
        val stage = Stage(StageStyle.DECORATED)
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.title = "New Project"

        val nameLabel = Label("Name:")
        val name = TextField("Project name")

        var locationFile = File(System.getProperty("user.home"))

        val locationLabel = Label("Location:")
        val locationButton = Button("Project location").apply {
            setOnAction {
                val location = DirectoryChooser().apply {
                    title = "Select project directory"
                    initialDirectory = locationFile
                }.showDialog(stage)

                location?.let {
                    locationFile = location
                }
            }
        }

        val grid = GridPane().apply {
            hgap = 12.0
            vgap = 12.0
            padding = Insets(15.0)
        }

        grid.add(nameLabel, 0, 0)
        grid.add(name, 1, 0)
        grid.add(locationLabel, 0, 1)
        grid.add(locationButton, 1, 1)

        val create = Button("Create").apply {
            setOnAction {
                val projectFolder = File(locationFile, name.text)
                projectFolder.mkdir()
                val main = File(projectFolder, "main.tex")
                main.createNewFile()

                openProject(projectFolder)
                stage.close()
            }
        }

        val cancel = Button("cancel").apply {
            setOnAction {
                stage.close()
            }
        }

        val buttons = ToolBar().apply {
            items.addAll(create, cancel)
        }

        val root = BorderPane().apply {
            center = grid
            bottom = buttons
        }
        stage.scene = Scene(root, 600.0, 500.0)
        stage.showAndWait()
    }
}
