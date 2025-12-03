package com.smartex.ui.startup

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

class StartupWindow(private val stage: Stage) {
    fun show() {
        val loader = FXMLLoader(javaClass.getResource("/fxml/StartupWindow.fxml"))
        val root = loader.load<javafx.scene.Parent>()
        val controller = loader.getController<StartupWindowController>()
        controller.setStage(stage)

        stage.apply {
            icons.add(Image(javaClass.classLoader.getResourceAsStream("icons/sadje.jpg")))
            scene = Scene(root)
            title = "Welcome to SmarTex IDE"
            show()
        }
    }
}
