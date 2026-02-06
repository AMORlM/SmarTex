package com.smartex.ui.settings

import com.smartex.settings.ModuleSettings
import com.smartex.settings.SettingsManager

object ShortcutSettings: ModuleSettings {
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

    fun getShortcut(action: EditorAction): String? = shortcuts[action.id]
}