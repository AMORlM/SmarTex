package com.smartex.ui.main

import com.smartex.settings.SettingsManager
import com.smartex.ui.main.windows.SettingsWindow
import com.smartex.ui.components.CompilationPane
import com.smartex.ui.components.FileTabPane
import com.smartex.ui.components.ProjectTree
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import java.io.File

object ProjectService {

    fun openProject(
        root: File,
        projectTree: ProjectTree,
        fileTabPane: FileTabPane,
        splitPane: SplitPane,
        settingsButton: Button
    ) {
        projectTree.populateFromDirectory(root)
        splitPane.items[2] = CompilationPane(root)

        SettingsManager.init(root, root)
        SettingsManager.loadAll()

        settingsButton.setOnAction {
            SettingsWindow.show(root)
        }
    }

    fun createProject(name: String, location: File): File {
        val projectFolder = File(location, name)
        projectFolder.mkdir()

        File(projectFolder, "main.tex").createNewFile()

        return projectFolder
    }
}
