package com.smartex.ui

import com.smartex.ui.startup.StartupWindow
import javafx.application.Application
import javafx.stage.Stage

class MainApp : Application() {
    override fun start(stage: Stage) {
        val startup = StartupWindow(stage)
        startup.show()
    }
}
