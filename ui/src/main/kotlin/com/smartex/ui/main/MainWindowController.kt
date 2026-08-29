package com.smartex.ui.main

import com.smartex.settings.SettingsManager
import com.smartex.ui.components.CompilationPane
import com.smartex.ui.components.FileTabPane
import com.smartex.ui.components.ProjectTree
import com.smartex.ui.components.compilation.NavigationController
import com.smartex.ui.newproject.NewProjectWindow
import com.smartex.ui.project.RecentProjectsService.getRecent
import com.smartex.ui.settings.EditorAction
import com.smartex.ui.settings.SettingsWindow
import com.smartex.ui.settings.ShortcutService
import com.smartex.ui.settings.ShortcutSettings
import javafx.fxml.FXML
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

class MainWindowController {
    @FXML lateinit var compilationPane: CompilationPane
    @FXML lateinit var fileTabPane: FileTabPane
    @FXML lateinit var projectTree: ProjectTree
    @FXML lateinit var recentProjectsMenu: Menu

    private lateinit var navigationController: NavigationController
    private lateinit var shortcutService: ShortcutService
    private lateinit var rootProject: File
    private lateinit var stage: Stage

    @FXML
    fun initialize() {
        refreshRecentProjects()
    }

    @FXML
    fun setter(stage: Stage, rootProject: File) {
        // Set stage
        this.stage = stage

        // Set shortcut scene
        shortcutService = ShortcutService(stage.scene, ShortcutSettings)

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

    private fun refreshRecentProjects() {
        recentProjectsMenu.items.clear()

        val recentProjects = getRecent()

        if (recentProjects.isEmpty()) {
            val emptyItem = MenuItem("No Recent Projects").apply {
                isDisable = true
            }

            recentProjectsMenu.items.add(emptyItem)
            return
        }

        recentProjects.forEach { project ->
            val item = MenuItem(project.name)

            item.setOnAction {
                openProject(project)
            }

            recentProjectsMenu.items.add(item)
        }
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

    @FXML
    fun onUndo() {
        fileTabPane.undo()
    }

    @FXML
    fun onRedo() {
        fileTabPane.redo()
    }

    @FXML
    fun onCopy() {
        fileTabPane.copy()
    }

    @FXML
    fun onPaste() {
        fileTabPane.paste()
    }

    @FXML
    fun onCut() {
        fileTabPane.cut()
    }

    @FXML
    fun onFind() {
        fileTabPane.find()
    }

    @FXML
    fun onReplace() {
        fileTabPane.replace()
    }

    private fun openProject(root: File) {
        // Set project root
        rootProject = root

        // Set project tree
        projectTree.populateFromDirectory(root)

        setCompilationAndNavigation(root)

        // Load settings
        SettingsManager.init(root, root)
        SettingsManager.loadAll()

        // Bind actions to shortcuts
        setShortcutActions()
    }

    private fun setCompilationAndNavigation(root: File) {
        // Set PDF preview
        compilationPane.setProjectRoot(root)

        // Set navigation
        navigationController = NavigationController(compilationPane.compiler) { file, line, col ->
            fileTabPane.openFile(File(root, file), line, col)
        }

        compilationPane.setNavigationController(navigationController)
    }

    private fun setShortcutActions() {
        shortcutService.clear()
        shortcutService.bind(EditorAction.SAVE) { onSave() }
    }
}

