package com.smartex.ui.startup

import com.smartex.ui.components.recent.RecentProjectsListView
import com.smartex.ui.project.ProjectService.openProject
import com.smartex.ui.newproject.NewProjectWindow
import javafx.fxml.FXML
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

class StartupWindowController {
    lateinit var recentListView: RecentProjectsListView
    private lateinit var stage: Stage

    fun setStage(stage: Stage) {
        this.stage = stage
    }

    @FXML
    fun initialize() {
        recentListView.setOnOpen { file ->
            openProjectInNewWindow(file)
        }
    }


    @FXML
    fun onNewProject() {
        val new = NewProjectWindow.show()
        new?.let { openProjectInNewWindow(it) }
    }

    @FXML
    fun onOpenProject() {
        val selectedDirectory = DirectoryChooser().apply {
            title = "Open Project"
            initialDirectory = File(System.getProperty("user.home"))
        }.showDialog(stage)

        selectedDirectory?.let { openProjectInNewWindow(it) }
    }

    private fun openProjectInNewWindow(selectedItem: File) {
        openProject(selectedItem)
        stage.close()
    }
}
