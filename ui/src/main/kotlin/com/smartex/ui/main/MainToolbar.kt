package com.smartex.ui.main

import com.smartex.ui.components.FileTabPane
import com.smartex.ui.components.ProjectTree
import com.smartex.ui.main.windows.NewProjectWindow
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.control.ToolBar
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
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
                val new = NewProjectWindow.show()
                if (new != null) {
                    ProjectService.openProject(new, projectTree, splitPane, settingsButton)
                }
            }
        }

        val openProjButton = Button("Open Project").apply {
            setOnAction {
                val selectedDirectory = DirectoryChooser().apply {
                    title = "Open Project"
                    initialDirectory = File(System.getProperty("user.home"))
                }.showDialog(stage)

                selectedDirectory?.let { openProject(it) }
            }
        }

        val saveButton = Button("Save File").apply {
            setOnAction { fileTabPane.saveCurrentFile() }
        }

        items.addAll(newProjButton, openProjButton, saveButton, settingsButton)
    }


    fun openProject(file: File) {
        ProjectService.openProject(file, projectTree, splitPane, settingsButton)
    }
}
