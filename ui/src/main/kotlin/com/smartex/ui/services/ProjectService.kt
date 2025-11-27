package com.smartex.ui.services

import com.smartex.ui.main.MainWindow
import javafx.stage.Stage
import java.io.File

object ProjectService {
    fun openProject(folder: File) {
        openProject(Stage(), folder)
    }


    fun openProject(stage: Stage, folder: File) {
        RecentProjectsService.add(folder)
        val mainWindow = MainWindow(stage, folder)
        mainWindow.show()
    }

    fun createProject(location: String, name: String): File? {
        val projectFolder = File(location, name)
        if (!projectFolder.mkdir()) {
            return null
        }

        File(projectFolder, "main.tex").createNewFile()

        return projectFolder
    }
}
