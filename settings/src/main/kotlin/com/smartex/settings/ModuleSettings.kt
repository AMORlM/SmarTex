package com.smartex.settings

interface ModuleSettings {

    val moduleName: String

    /** Schema describing the fields. Global, read-only. */
    val schemaResource: String

    /** Filename where project settings should be saved */
    val projectSettingsFileName: String

    /** Called when settings are loaded */
    fun onSettingsLoaded(data: Map<String, Any?>)

    /** Optional: validate or transform settings before saving */
    fun beforeSave(data: MutableMap<String, Any?>) { }
}
