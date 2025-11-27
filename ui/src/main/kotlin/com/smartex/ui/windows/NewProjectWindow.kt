package com.smartex.ui.windows

import com.smartex.ui.main.ProjectService
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.stage.DirectoryChooser
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File

object NewProjectWindow {

    /**
     * Shows the dialog and returns the created project directory,
     * or null if cancelled.
     */
    fun show(): File? {
        val stage = Stage(StageStyle.DECORATED).apply {
            initModality(Modality.APPLICATION_MODAL)
            title = "New Project"
        }

        val nameField = TextField("Project name")

        val locationField = TextField(System.getProperty("user.home")).apply {
            isEditable = false
        }

        val locationButton = Button("Browse…").apply {
            setOnAction {
                val chosen = DirectoryChooser().apply {
                    title = "Choose Project Location"
                    initialDirectory = File(locationField.text)
                }.showDialog(stage)

                if (chosen != null) {
                    locationField.text = chosen.absolutePath
                }
            }
        }

        val grid = GridPane().apply {
            hgap = 12.0
            vgap = 12.0
            padding = Insets(15.0)

            add(Label("Name:"), 0, 0)
            add(nameField, 1, 0)
            add(Label("Location:"), 0, 1)
            add(locationField, 1, 1)
            add(locationButton, 2, 1)
        }

        var result: File? = null

        val createButton = Button("Create").apply {
            setOnAction {
                result = ProjectService.createProject(locationField.text, nameField.text)
                stage.close()
            }
        }

        val cancelButton = Button("Cancel").apply {
            setOnAction { stage.close() }
        }

        val buttonBar = ToolBar(createButton, cancelButton)

        stage.scene = Scene(BorderPane().apply {
            center = grid
            bottom = buttonBar
        }, 500.0, 300.0)

        stage.showAndWait()
        return result
    }
}