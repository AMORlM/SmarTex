package com.smartex.ui.main

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.io.File

class MainWindow(private val stage: Stage, private val root: File) {

    fun show() {
        val loader = FXMLLoader(javaClass.getResource("/fxml/MainWindow.fxml"))
        val rootPane = loader.load<BorderPane>()
        val controller = loader.getController<MainWindowController>()
        controller.setter(stage, root)

        stage.apply {
            icons.add(Image(javaClass.classLoader.getResourceAsStream("icons/sadje.jpg")))
            this.scene = Scene(rootPane, 1280.0, 800.0)
            isMaximized = true
            title = "SmarTex"
            show()
        }
    }
}
