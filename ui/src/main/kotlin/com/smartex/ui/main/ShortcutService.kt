package com.smartex.ui.main

import javafx.scene.Scene
import javafx.scene.input.KeyCombination

class ShortcutService(
    private val scene: Scene
) {
    private val shortcutSettings = ShortcutSettings()

    fun removeShortcuts() = scene.accelerators.clear()

    fun setSave(handler: () -> Unit) {
        val combo = KeyCombination.keyCombination(shortcutSettings.save)
        println(shortcutSettings.save)
        scene.accelerators[combo] = Runnable(handler)
    }
}
