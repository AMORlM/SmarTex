package com.smartex.ui.windows

import com.smartex.ui.services.ProjectService
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

class NewProjectWindowController {

    @FXML private lateinit var nameField: TextField
    @FXML private lateinit var locationField: TextField
    @FXML private lateinit var browseButton: Button

    private lateinit var stage: Stage
    private var result: File? = null

    fun setStage(stage: Stage) {
        this.stage = stage
    }

    @FXML
    fun initialize() {
        locationField.text = System.getProperty("user.home")

        browseButton.setOnAction {
            val dir = DirectoryChooser().apply {
                title = "Choose Project Location"
                initialDirectory = File(locationField.text)
            }.showDialog(stage)

            if (dir != null) {
                locationField.text = dir.absolutePath
            }
        }
    }

    @FXML
    fun onCreate() {
        result = ProjectService.createProject(locationField.text, nameField.text)
        stage.close()
    }

    @FXML
    fun onCancel() {
        stage.close()
    }

    fun getResult(): File? = result
}
