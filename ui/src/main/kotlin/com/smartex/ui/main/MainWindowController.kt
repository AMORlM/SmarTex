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

    private lateinit var shortcutService: ShortcutService
    private lateinit var rootProject: File
    private lateinit var stage: Stage

    @FXML
    fun setter(stage: Stage, rootProject: File) {
        // Set stage
        this.stage = stage

        // Set shortcut scene
        //shortcutService = ShortcutService(stage.scene)

        // Set project tree callbacks
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

        openProject(rootProject)
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
        setShortcutActions()
    }

    private fun openProject(root: File) {
        // Set project root
        rootProject = root

        // Set project tree
        projectTree.populateFromDirectory(root)

        // Set PDF preview
        compilationPane.setProjectRoot(root)

        // Load settings
        SettingsManager.init(root, root)
        SettingsManager.loadAll()

        // Bind actions to shortcuts
        //setShortcutActions()
    }

    private fun setShortcutActions() {
        shortcutService.removeShortcuts()
        shortcutService.setSave { onSave() }
    }
}

