package com.smartex.ui.main

import com.smartex.settings.SettingsManager
import com.smartex.settings.SettingsWindow
import com.smartex.ui.components.CompilationPane
import com.smartex.ui.components.FileTabPane
import com.smartex.ui.components.ProjectTree
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
        val openDirButton = Button("Open Directory").apply {
            setOnAction {
                val selectedDirectory = DirectoryChooser().apply {
                    title = "Open Directory"
                    initialDirectory = File(System.getProperty("user.home"))
                }.showDialog(stage)

                selectedDirectory?.let { file ->
                    projectTree.populateFromDirectory(file)
                    splitPane.items[2] = CompilationPane(file)
                    SettingsManager.init(file, file)
                    SettingsManager.loadAll()
                    settingsButton.apply {
                        setOnAction { SettingsWindow.show(file) }
                    }
                }
            }
        }

        val saveButton = Button("Save File").apply {
            setOnAction { fileTabPane.saveCurrentFile() }
        }


        items.addAll(openDirButton, saveButton, settingsButton)
    }
}
