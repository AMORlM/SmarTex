package com.smartex.ui.startup

import com.smartex.ui.main.ProjectService
import com.smartex.ui.main.ProjectService.openProject
import com.smartex.ui.windows.NewProjectWindow
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

class StartupWindow(private val stage: Stage) {

    fun show() {
        val newProjectButton = Button("New Project").apply {
            setOnAction {
                val new = NewProjectWindow.show()
                new?.let { openProject(stage, it) }
            }
        }

        val openProjectButton = Button("Open Project").apply {
            setOnAction {
                val selectedDirectory = DirectoryChooser().apply {
                    this.title = "Open Project"
                    initialDirectory = File(System.getProperty("user.home"))
                }.showDialog(stage)

                selectedDirectory?.let { openProjectInNewWindow(it) }
            }
        }

        // Recent Projects
        val recentList = ListView<File>().apply {
            items.addAll(ProjectService.getRecentProjects())
            setOnMouseClicked { event ->
                if (event.button == MouseButton.PRIMARY) {
                    val selectedItem = selectionModel.selectedItem
                    selectedItem?.let { openProjectInNewWindow(it) }
                }
            }
        }

        val actions = HBox(newProjectButton, openProjectButton).apply {
            spacing = 10.0
            alignment = Pos.TOP_RIGHT
            padding = Insets(0.0, 0.0,20.0,0.0)
        }

        val root = BorderPane().apply {
            top = actions
            center = recentList
            padding = Insets(20.0)
        }

        val scene = Scene(root, 700.0, 450.0)
        stage.apply {
            this.scene = scene
            title = "Welcome to SmarTex IDE"
            icons.add(Image(javaClass.classLoader.getResourceAsStream("icons/sadje.jpg")))
            show()
        }
    }

    private fun openProjectInNewWindow(selectedItem: File) {
        openProject(selectedItem)
        stage.close()
    }

}
