package com.smartex.ui.main

import com.smartex.settings.SettingsManager
import com.smartex.ui.components.CompilationPane
import com.smartex.ui.components.FileTabPane
import com.smartex.ui.components.ProjectTree
import com.smartex.ui.windows.NewProjectWindow
import com.smartex.ui.windows.SettingsWindow
import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

class MainWindowController {
    @FXML lateinit var projectTreeContainer: AnchorPane
    @FXML lateinit var pdfPreviewContainer: AnchorPane

    @FXML lateinit var fileTabPane: FileTabPane
    private val projectTree = ProjectTree { file -> fileTabPane.openFile(file) }

    private lateinit var rootProject: File
    private lateinit var stage: Stage

    @FXML
    fun setter(stage: Stage, rootProject: File) {
        this.stage = stage
        this.rootProject = rootProject

        // Setup PDF preview
        pdfPreviewContainer.children.add(CompilationPane(rootProject))

        // Populate project tree
        projectTree.populateFromDirectory(rootProject)
        projectTreeContainer.children.add(projectTree)

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
        projectTree.populateFromDirectory(root)
        pdfPreviewContainer.children.setAll(CompilationPane(root))
        SettingsManager.init(root, root)
        SettingsManager.loadAll()
    }
}

