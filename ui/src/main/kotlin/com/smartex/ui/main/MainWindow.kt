package com.smartex.ui.main

import com.smartex.ui.components.FileTabPane
import com.smartex.ui.components.ProjectTree
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.SplitPane
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage


class MainWindow(private val stage: Stage) {
    private val fileTabPane = FileTabPane()
    private val projectTree = ProjectTree { file -> fileTabPane.openFile(file) }
    private val pdfPreview = Pane().apply { style = "-fx-background-color: #2b2b2b;" }

    private val splitPane = SplitPane(projectTree, fileTabPane, pdfPreview).apply {
        orientation = Orientation.HORIZONTAL
        setDividerPositions(0.15, 0.65)
    }

    fun show() {
        val toolbar = MainToolbar(stage, projectTree, fileTabPane, splitPane)
        val root = BorderPane().apply {
            top = toolbar
            center = splitPane
        }

        val scene = Scene(root, 1280.0, 800.0)
        stage.apply {
            this.scene = scene
            title = "SmarTex"
            icons.add(Image(javaClass.classLoader.getResourceAsStream("icons/sadje.jpg")))
            isMaximized = true
            show()
        }

        root.stylesheets.add(
            javaClass.classLoader.getResource("styles/dracula.css")!!.toExternalForm()
        )
    }
}