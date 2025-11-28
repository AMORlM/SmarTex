package com.smartex.ui.windows

import com.smartex.ui.services.ProjectService
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
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

        val locationButton = Button().apply {
            graphic = ImageView(Image(javaClass.classLoader
                .getResourceAsStream("icons/folder-closed.png"))).apply {
                fitWidth = 16.0
                fitHeight = 16.0
                isPreserveRatio = true
            }
            styleClass += "text-field-icon-button"

            StackPane.setAlignment(this, Pos.CENTER_RIGHT)
            StackPane.setMargin(this, Insets(6.0))

            setOnAction {
                DirectoryChooser().apply {
                    title = "Choose Project Location"
                    initialDirectory = File(locationField.text)
                }.showDialog(stage)?.let {
                    locationField.text = it.absolutePath
                }
            }
        }

        val locationStack = StackPane().apply {
            children.addAll(locationField, locationButton)
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        val grid = GridPane().apply {
            padding = Insets(15.0)
            hgap = 12.0
            vgap = 12.0

            val labelCol = ColumnConstraints().apply {
                halignment = HPos.LEFT
                hgrow = Priority.NEVER
            }

            val inputCol = ColumnConstraints().apply {
                hgrow = Priority.ALWAYS
            }

            columnConstraints.addAll(labelCol, inputCol)

            add(Label("Name:"), 0, 0)
            add(nameField, 1, 0)
            add(Label("Location:"), 0, 1)
            add(locationStack, 1, 1)
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

        val spacer = Pane().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        val buttonBar = HBox(10.0, spacer, cancelButton, createButton).apply {
            styleClass += "dialog-buttonbar"
            alignment = Pos.BASELINE_LEFT
            padding = Insets(10.0)
        }

        stage.scene = Scene(BorderPane().apply {
            stylesheets.add(javaClass.classLoader.getResource("styles/dracula.css")!!.toExternalForm())
            bottom = buttonBar
            center = grid
        }, 500.0, 300.0)

        stage.apply {
            isResizable = false
            isMaximized = false
            title = "Create New Project"
            icons.add(Image(javaClass.classLoader.getResourceAsStream("icons/sadje.jpg")))
            showAndWait()
        }

        return result
    }
}