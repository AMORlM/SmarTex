package com.smartex.ui

import com.smartex.settings.SettingsWindow
import com.smartex.ui.components.CompilationPane
import com.smartex.ui.components.FileTabPane
import com.smartex.ui.components.ProjectTree
import javafx.application.Application
import javafx.geometry.Orientation
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.control.ToolBar
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Screen
import javafx.stage.Stage
import java.io.File


class MainApp : Application() {
    override fun start(stage: Stage) {
        // Placeholder for PDF preview pane
        val pdfPreview = Pane().apply {
            style = "-fx-background-color: #f4f4f4;"
        }

        // File editor tabs pane
        val fileTabPane = FileTabPane()

        // Project tree view
        val projectTree = ProjectTree { file -> fileTabPane.openFile(file) }

        // Split pane to hold both the file editor and PDF preview
        val splitPane = SplitPane().apply {
            orientation = Orientation.HORIZONTAL
            items.addAll(projectTree, fileTabPane, pdfPreview)
            setDividerPositions(0.15, 0.65)
        }

        val toolbar = makeToolBar(stage, splitPane, projectTree, fileTabPane)


        val root = BorderPane().apply {
            top = toolbar
            center = splitPane // Use the split pane in the center
        }

        // Set windowed size to WINDOWED_SCALE% of the screen size
        val screenBounds: Rectangle2D = Screen.getPrimary().visualBounds
        val scene = Scene(root, screenBounds.width * WINDOWED_SCALE, screenBounds.height * WINDOWED_SCALE)

        stage.apply {
            // Set the scene and maximize the window
            this.scene = scene
            isMaximized = true
            title = "SmarTex"

            // Set application icon
            icons.add(Image(javaClass.classLoader.getResourceAsStream("icons/sadje.jpg")))
            show()
        }

        root.stylesheets.addAll(
            javaClass.getResource("/styles/dracula.css")?.toExternalForm()
        )
    }

    private fun makeToolBar(
        stage: Stage,
        splitPane: SplitPane,
        projectTree: ProjectTree,
        fileTabPane: FileTabPane
    ): ToolBar {
        // Toolbar  buttons
        val openDirButton = Button("Open Directory").apply {
            setOnAction {
                val selectedDirectory = DirectoryChooser().apply {
                    title = "Open Directory"
                    initialDirectory = File(System.getProperty("user.home"))
                }.showDialog(stage)

                selectedDirectory?.let {
                    getOrGenerateSettings(it)
                    projectTree.populateFromDirectory(it)
                    splitPane.items[2] = CompilationPane(it)
                }
            }
        }

        val saveButton = Button("Save File").apply {
            setOnAction {
                fileTabPane.saveCurrentFile()
            }
        }

        val settingsButton = Button("Settings").apply {
            setOnAction {
                SettingsWindow.show()
            }
        }

        return ToolBar(openDirButton, saveButton, settingsButton)
    }

    fun getOrGenerateSettings(rootDir: File) {
        val settingsDir = File(rootDir, SETTINGS_DIR)
        if (!settingsDir.exists()) {
            println("Settings not found. Creating...")
            settingsDir.mkdir()
        }
    }
}
