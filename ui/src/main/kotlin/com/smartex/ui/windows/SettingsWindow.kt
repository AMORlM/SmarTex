package com.smartex.ui.windows

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File

object SettingsWindow {

    fun show(projectRoot: File) {
        val stage = Stage(StageStyle.DECORATED)

        val controller = SettingsController(projectRoot)

        val tabPane = TabPane()

        controller.getModuleBuffers().forEach { (name, builder) ->
            val tab = Tab(name).apply {
                content = ScrollPane(builder.build()).apply {
                    fitToWidthProperty().set(true)
                    fitToHeightProperty().set(true)
                }
                isClosable = false
            }
            tabPane.tabs.add(tab)
        }

        val saveButton = Button("Save").apply {
            setOnAction {
                controller.saveAll()
                stage.close()
            }
        }

        val cancelButton = Button("cancel").apply {
            setOnAction {
                stage.close()
            }
        }

        val bottomBar = HBox(cancelButton, saveButton).apply {
            alignment = Pos.CENTER_RIGHT
            padding = Insets(10.0)
            spacing = 10.0
        }

        val root = BorderPane().apply {
            stylesheets.add(javaClass.classLoader.getResource("styles/dracula.css")!!.toExternalForm())
            center = tabPane
            bottom = bottomBar
        }

        stage.apply {
            icons.add(Image(javaClass.classLoader.getResourceAsStream("icons/sadje.jpg")))
            scene = Scene(root, 600.0, 500.0)
            initModality(Modality.APPLICATION_MODAL)
            title = "SmarTex Settings"
            showAndWait()
        }
    }
}