package com.smartex.ui.main

import com.smartex.settings.ModuleSettings
import com.smartex.settings.SettingsManager

class ShortcutSettings: ModuleSettings {
    override val moduleName = "shortcuts"
    override val schemaResource = "schemas/ideShortcuts.schema.json"
    override val projectSettingsFileName = "shortcuts.json"

    lateinit var shortcuts: Map<String, String?>

    init {
        SettingsManager.register(this)
    }

    override fun onSettingsLoaded(data: Map<String, Any?>) {
        shortcuts = data as Map<String, String?>    // suppress warning
    }

    val save: String
        get() = shortcuts["saveFile"] ?: "Ctrl+S"
}