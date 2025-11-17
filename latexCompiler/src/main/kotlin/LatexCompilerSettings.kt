package com.smartex.latexcompiler

import com.smartex.settings.ModuleSettings
import com.smartex.settings.SettingsManager

class LatexCompilerSettings : ModuleSettings {
    override val moduleName = "LaTeX Compiler"
    override val schemaResource = "schemas/LatexCompilerSettings.schema.json"
    override val projectSettingsFileName = "latex.json"

    lateinit var settings: Map<String, Any?>

    init {
        SettingsManager.register(this)
    }


    override fun onSettingsLoaded(data: Map<String, Any?>) {
        settings = data
    }

    val compiler: String
        get() = settings["compiler"] as? String ?: "latex"

    val mainFile: String?
        get() = settings["mainFile"]!! as? String

    val outputFile: String
        get() = settings["outputFile"] as? String ?: "main.pdf"

    val executionOrder: List<String>
        get() = (settings["executionOrder"] as? List<*>)?.filterIsInstance<String>() ?: listOf("compile")

    override fun toString(): String = "{name: $moduleName, schema: $schemaResource}"
}
