package com.smartex.latexcompiler

import com.smartex.settings.ModuleSettings
import com.smartex.settings.SettingsManager

class LatexCompilerSettings : ModuleSettings {
    override val moduleName = "LaTeX Compiler"
    override val schemaResource = "/schemas/latexCompilerSettings.schema.json"
    override val projectSettingsFileName = "latex.json"

    lateinit var settings: Map<String, Any?>

    init {
        SettingsManager.register(this)
        settings = SettingsManager.getProjectSettings(this)

    }


    override fun onSettingsLoaded(data: Map<String, Any?>) {
        settings = data
    }

    val compiler: String
        get() = settings["compiler"] as? String ?: "latex"

    var mainFile: String?
        get() = settings["mainFile"] as? String
        set(value) {
            val s = mutableMapOf<String, Any?>().apply {
                putAll(settings)
                this["mainFile"] = value
            }
            settings = s
        }

    val outputFile: String
        get() = settings["outputFile"] as? String ?: "main.pdf"

    val executionOrder: List<String>
        get() = (settings["executionOrder"] as? List<*>)?.filterIsInstance<String>() ?: listOf("compile")

    override fun toString(): String = "{name: $moduleName, schema: $schemaResource}"
}
