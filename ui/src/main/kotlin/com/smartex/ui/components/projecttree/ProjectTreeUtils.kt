package com.smartex.ui.components.projecttree

import javafx.scene.control.TreeItem
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.effect.Blend
import javafx.scene.effect.BlendMode
import javafx.scene.effect.ColorInput
import java.io.File

object ProjectTreeUtils {

    const val ICON_SIZE = 16.0

    enum class CellType(val styleClass: String) {
        FILE("file-cell"),
        DIRECTORY("dir-cell")
    }

    fun buildTreeItem(file: File): TreeItem<File> {
        val item = TreeItem(file)
        if (file.isDirectory) {
            val children = file.listFiles()?.sortedWith(
                compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() }
            ) ?: emptyList()
            item.children.setAll(children.map { buildTreeItem(it) })
        }
        return item
    }

    fun refreshBranch(item: TreeItem<File>) {
        val dir = item.value
        if (dir.isDirectory) {
            val children = dir.listFiles()?.sortedWith(
                compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() }
            ) ?: emptyList()
            item.children.setAll(children.map { buildTreeItem(it) })
        }
    }

    fun loadIcon(iconPath: String): ImageView {
        val icon = javaClass.classLoader.getResource(iconPath)?.toExternalForm()
        val imgView = ImageView(icon).apply {
            fitHeight = ICON_SIZE
            fitWidth = ICON_SIZE
        }

        // Apply color overlay
        val colorInput = ColorInput(0.0, 0.0, imgView.image.width, imgView.image.height, Color.GHOSTWHITE)
        val blend = Blend().apply {
            mode = BlendMode.SRC_ATOP
            bottomInput = imgView.effect
            topInput = colorInput
        }
        imgView.effect = blend

        return imgView
    }
}
