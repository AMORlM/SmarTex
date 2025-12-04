package com.smartex.ui.settings

import com.smartex.settings.SettingsManager
import java.io.File


class SettingsController(projectRoot: File) {

    // Holds UI builders per module
    private val moduleBuffers = mutableMapOf<String, SettingsUIBuilder>()

    init {
        SettingsManager.getModules().forEach { module ->
            val schema = SettingsManager.loadSchema(module)
            val projectSettings = SettingsManager.getProjectSettings(module)
            val builder = SettingsUIBuilder(projectRoot, schema, projectSettings)
            moduleBuffers[module.moduleName] = builder
        }
    }

    fun getModuleBuffers(): Map<String, SettingsUIBuilder> = moduleBuffers

    fun saveAll() {
        val allData = moduleBuffers.mapValues { it.value.writeBuffer.toMap() }
        SettingsManager.saveAll(allData)
        SettingsManager.loadAll()
    }
}
