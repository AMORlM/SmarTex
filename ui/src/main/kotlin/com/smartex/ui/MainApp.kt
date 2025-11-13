package com.smartex.ui

import com.smartex.ui.main.MainWindow
import javafx.application.Application
import javafx.stage.Stage

class MainApp : Application() {
    override fun start(stage: Stage) {
        val mainWindow = MainWindow(stage)
        mainWindow.show()
    }
}
