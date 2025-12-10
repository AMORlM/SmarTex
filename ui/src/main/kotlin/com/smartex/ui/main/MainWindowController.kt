package com.smartex.ui.main

import com.smartex.settings.SettingsManager
import com.smartex.ui.components.CompilationPane
import com.smartex.ui.components.FileTabPane
import com.smartex.ui.components.ProjectTree
import com.smartex.ui.newproject.NewProjectWindow
import com.smartex.ui.settings.SettingsWindow
import javafx.fxml.FXML
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

class MainWindowController {
    @FXML lateinit var compilationPane: CompilationPane
    @FXML lateinit var fileTabPane: FileTabPane
    @FXML lateinit var projectTree: ProjectTree

    private lateinit var rootProject: File
    private lateinit var stage: Stage

    @FXML
    fun setter(stage: Stage, rootProject: File) {
        this.rootProject = rootProject
        this.stage = stage

        // Setup PDF preview
        compilationPane.setProjectRoot(rootProject)

        // Populate project tree
        projectTree.setCallbacks({
            old, new ->
                if (fileTabPane.fileIsOpen(old)) {
                    fileTabPane.closeFile(old)
                    fileTabPane.openFile(new)
                }
            },
            { file -> fileTabPane.openFile(file) },
            { file -> fileTabPane.closeFile(file) },
            { file -> fileTabPane.openFile(file) }
        )
        projectTree.populateFromDirectory(rootProject)

        // Load settings
        SettingsManager.init(rootProject, rootProject)
        SettingsManager.loadAll()
    }

    @FXML
    fun onNewProject() {
        val newRoot = NewProjectWindow.show()
        newRoot?.let { openProject(it) }
    }

    @FXML
    fun onOpenProject() {
        val selectedDirectory = DirectoryChooser().apply {
            title = "Open Project"
            initialDirectory = File(System.getProperty("user.home"))
        }.showDialog(stage)
        selectedDirectory?.let { openProject(it) }
    }

    @FXML
    fun onSave() {
        fileTabPane.saveCurrentFile()
    }

    @FXML
    fun onSettings() {
        SettingsWindow.show(rootProject)
    }

    private fun openProject(root: File) {
        rootProject = root
        projectTree.populateFromDirectory(root)
        compilationPane.setProjectRoot(root)
        SettingsManager.init(root, root)
        SettingsManager.loadAll()
    }
}

