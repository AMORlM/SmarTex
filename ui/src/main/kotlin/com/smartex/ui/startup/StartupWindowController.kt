package com.smartex.ui.startup

import com.smartex.ui.services.ProjectService.openProject
import com.smartex.ui.windows.NewProjectWindow
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

class StartupWindowController {
    private lateinit var stage: Stage

    fun setStage(stage: Stage) {
        this.stage = stage
    }


    fun setNewProject() {
        val new = NewProjectWindow.show()
        new?.let { openProjectInNewWindow(it) }

//        recentListView.setOnOpen { file ->
//            openProjectInNewWindow(file)
//        }
    }

    fun setOpenProject() {
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
