package com.smartex.ui.settings

import javafx.scene.Scene
import javafx.scene.input.KeyCombination

class ShortcutService(
    private val scene: Scene,
    private val shortcutSettings: ShortcutSettings
) {
    fun clear() = scene.accelerators.clear()

    fun bind(action: EditorAction, handler: () -> Unit) {
        val comboText = shortcutSettings.getShortcut(action) ?: return
        val combo = KeyCombination.keyCombination(comboText)

        scene.accelerators[combo] = Runnable {
            handler()
        }
    }
}

