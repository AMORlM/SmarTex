package com.smartex.ui.components

import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.effect.Blend
import javafx.scene.effect.BlendMode
import javafx.scene.effect.ColorInput
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.util.Callback
import java.io.File

private const val ICON_SIZE = 16.0

// Preload image icons
private const val IMAGE_ICON = "icons/image.png"
private const val FILE_ICON = "icons/file-text.png"
private const val OPEN_FOLDER_ICON = "icons/folder-open.png"
private const val CLOSED_FOLDER_ICON = "icons/folder-closed.png"

class ProjectTree(private val onFileSelected: (File) -> Unit) : TreeView<File>() {
    enum class CellType(val styleClass: String) {
        FILE ("file-cell"),
        DIRECTORY("dir-cell")
    }

    init {
        // Handle file selection
        this.setOnMouseClicked {
            val selectedItem = selectionModel.selectedItem
            if (selectedItem != null && selectedItem.value.isFile) {
                onFileSelected(selectedItem.value)
            }
        }

        // Cell factory to customize cell appearance
        this.cellFactory = Callback<TreeView<File>, TreeCell<File>> {
            object : TreeCell<File>() {
                override fun updateItem(item: File?, empty: Boolean) {
                    super.updateItem(item, empty)

                    // Clear previous styles and text
                    text = null
                    graphic = null
                    styleClass.removeAll(CellType.entries.map { it.styleClass })

                    if (!empty && item != null) {
                        text = item.name

                        // Add style class for CSS targeting
                        if (item.isDirectory) {
                            val openFolderIcon = loadIcon(OPEN_FOLDER_ICON)
                            val closedFolderIcon = loadIcon(CLOSED_FOLDER_ICON)
                            graphic = if (treeItem != null && treeItem.isExpanded) openFolderIcon else closedFolderIcon

                            styleClass.add(CellType.DIRECTORY.styleClass)
                        } else {
                            styleClass.add(CellType.FILE.styleClass)
                            val icon = when (item.extension.lowercase()) {
                                "png", "jpg", "jpeg", "gif", "bmp", "webp", "svg" -> IMAGE_ICON
                                //"pdf" -> "icons/pdf.png"
                                //"md" -> "icons/md.png"
                                else -> FILE_ICON
                            }

                            graphic = loadIcon(icon)
                        }
                    }
                }
            }
        }
    }

    // Populate the tree from a given directory
    fun populateFromDirectory(rootDir: File) {
        val rootItem = buildTreeItem(rootDir)
        this.root = rootItem
        this.isShowRoot = true
        rootItem.isExpanded = true
    }

    // Recursively build tree items
    private fun buildTreeItem(file: File): TreeItem<File> {
        val item = TreeItem(file)

        if (file.isDirectory) {
            val children = file.listFiles()?.sortedWith(
                compareByDescending<File> { it.isDirectory } // directories first
                    .thenBy { it.name.lowercase() }          // then sort alphabetically
            ) ?: emptyList()
            item.children.setAll(children.map { buildTreeItem(it) })
        }
        return item
    }

    // Apply color overlay to icon
    private fun colorizeIcon(iconView: ImageView, color: Color) {
        val imageBounds = iconView.image
        val colorInput = ColorInput(0.0, 0.0, imageBounds.width, imageBounds.height, color)

        val blend = Blend()
        blend.mode = BlendMode.SRC_ATOP
        blend.bottomInput = iconView.effect
        blend.topInput = colorInput

        iconView.effect = blend
    }

    // Load icons
    private fun loadIcon(path: String): ImageView {
        val icon = javaClass.classLoader.getResource(path)?.toExternalForm()

        // Set a graphic/icon based on file type
        val imgView = ImageView(icon).apply {
            fitHeight = ICON_SIZE
            fitWidth = ICON_SIZE
        }

        // Apply color overlay to icons for better visibility
        colorizeIcon(imgView, Color.GHOSTWHITE)

        return imgView
    }
}
