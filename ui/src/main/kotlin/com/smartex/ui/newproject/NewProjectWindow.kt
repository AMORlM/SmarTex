package com.smartex.ui.newproject

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File

object NewProjectWindow {

    /**
     * Shows the dialog and returns the created project directory,
     * or __null__ if cancelled.
     */
    fun show(): File? {
        val stage = Stage(StageStyle.DECORATED)

        val loader = FXMLLoader(javaClass.classLoader.getResource("fxml/NewProjectWindow.fxml"))
        val root = loader.load<BorderPane>()
        val controller = loader.getController<NewProjectWindowController>().apply {
            setStage(stage)
        }

        stage.apply {
            isResizable = false
            scene = Scene(root)
            icons.add(Image(javaClass.classLoader.getResourceAsStream("icons/sadje.jpg")))
            initModality(Modality.APPLICATION_MODAL)
            title = "New Project"
            showAndWait()
        }

        return controller.getResult()
    }
}