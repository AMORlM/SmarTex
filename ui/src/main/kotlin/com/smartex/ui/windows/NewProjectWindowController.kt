package com.smartex.ui.windows

import com.smartex.ui.services.ProjectService
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

class NewProjectWindowController {
    @FXML private lateinit var locationField: TextField
    @FXML private lateinit var nameField: TextField

    private var result: File? = null
    private lateinit var stage: Stage

    fun setStage(stage: Stage) {
        locationField.text = System.getProperty("user.home")
        this.stage = stage
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

    @FXML
    fun onBrowse() {
        val dir = DirectoryChooser().apply {
            title = "Choose Project Location"
            initialDirectory = File(locationField.text)
        }.showDialog(stage)

        dir?.let {
            locationField.text = dir.absolutePath
        }
    }

    fun getResult(): File? = result
}
