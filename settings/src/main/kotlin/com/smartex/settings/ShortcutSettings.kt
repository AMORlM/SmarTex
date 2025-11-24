package com.smartex.settings

class ShortcutSettings: ModuleSettings {
    override val moduleName = "shortcuts"
    override val schemaResource = "schemas/ideShortcuts.schema.json"
    override val projectSettingsFileName = "shortcuts.json"

    lateinit var settings: Map<String, Any?>

    init {
        SettingsManager.register(this)
    }

    override fun onSettingsLoaded(data: Map<String, Any?>) {
        settings = data
    }

    val save: String
        get() = settings["saveFile"] as? String ?: "Ctrl+S"
}