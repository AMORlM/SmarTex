package com.smartex.ui.main

import com.smartex.settings.SettingsManager
import com.smartex.ui.components.CompilationPane
import com.smartex.ui.components.FileTabPane
import com.smartex.ui.components.ProjectTree
import com.smartex.ui.windows.NewProjectWindow
import com.smartex.ui.windows.SettingsWindow
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
    private val splitPane: SplitPane,
    root: File
) : ToolBar() {

    val settingsButton = Button("Settings")

    init {
        val newProjectButton = Button("New Project").apply {
            setOnAction {
                val new = NewProjectWindow.show()
                new?.let { openProject(it) }
            }
        }

        val openProjectButton = Button("Open Project").apply {
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

        items.addAll(newProjectButton, openProjectButton, saveButton, settingsButton)

        openProject(root)
    }


    private fun openProject(root: File) {
        projectTree.populateFromDirectory(root)
        splitPane.items[2] = CompilationPane(root)

        SettingsManager.init(root, root)
        SettingsManager.loadAll()

        settingsButton.setOnAction {
            SettingsWindow.show(root)
        }
    }
}
