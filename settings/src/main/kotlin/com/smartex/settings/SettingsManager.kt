package com.smartex.settings

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File


object SettingsManager {
    const val SETTINGS_DIR = ".smartex"

    private val mapper = jacksonObjectMapper()
    private val modules = mutableListOf<ModuleSettings>()

    private lateinit var projectRoot: File
    private lateinit var globalSettingsDir: File

    fun init(projectRoot: File, globalDir: File) {
        this.projectRoot = projectRoot
        this.globalSettingsDir = globalDir
    }


    fun register(module: ModuleSettings) {
        modules += module
        println(modules.toList().toString())
    }

    fun loadAll() {
        modules.forEach { module ->

            val schema = loadSchema(module)
            val defaults = extractDefaultsFromSchema(schema)

            val projectFile =
                File(projectRoot, "$SETTINGS_DIR/${module.projectSettingsFileName}")

            val projectSettings =
                if (projectFile.exists())
                    readJson(projectFile)
                else {
                    // Create file using schema defaults
                    projectFile.parentFile.mkdirs()
                    writeJson(projectFile, defaults)
                    defaults
                }

            // Notify module
            module.onSettingsLoaded(projectSettings)
        }
    }

    fun saveAll(settingsFromUI: Map<String, Map<String, Any?>>) {
        settingsFromUI.forEach { (moduleName, data) ->
            val module =
                modules.find { it.moduleName == moduleName } ?: return@forEach
            val projectFile =
                File(projectRoot, ".smartex/${module.projectSettingsFileName}")
            module.beforeSave(data.toMutableMap())
            writeJson(projectFile, data)
        }
    }

    private fun extractDefaultsFromSchema(schema: Map<String, Any>): Map<String, Any?> {
        return schema.mapValues { (_, rawField) ->
            val f = rawField as Map<*, *>
            f["default"]
        }
    }

    fun loadSchema(module: ModuleSettings): Map<String, Any> =
        mapper.readValue(javaClass.classLoader.getResourceAsStream(module.schemaResource)!!)

    private fun readJson(file: File): Map<String, Any?> =
        mapper.readValue(file)

    private fun writeJson(file: File, data: Any) =
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, data)


    fun getModules() : List<ModuleSettings> =
        modules.toList()


    fun getProjectSettings(module: ModuleSettings): Map<String, Any?> =
        readJson(File(projectRoot, "$SETTINGS_DIR/${module.projectSettingsFileName}"))
}
