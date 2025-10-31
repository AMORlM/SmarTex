package com.smartex.ui.components.projecttree

import com.smartex.ui.components.projecttree.ProjectTreeUtils.loadIcon
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeView
import java.io.File

// Preload image icons
private const val IMAGE_ICON = "icons/image.png"
private const val FILE_ICON = "icons/file-text.png"
private const val OPEN_FOLDER_ICON = "icons/folder-open.png"
private const val CLOSED_FOLDER_ICON = "icons/folder-closed.png"

class ProjectTreeCell(private val treeView: TreeView<File>) : TreeCell<File>() {

    override fun updateItem(item: File?, empty: Boolean) {
        super.updateItem(item, empty)

        styleClass.removeAll(ProjectTreeUtils.CellType.entries.map { it.styleClass })

        if (empty || item == null) {
            text = null
            graphic = null
            return
        }

        text = item.name

        // Add style class for CSS targeting
        if (item.isDirectory) {
            graphic = loadIcon(if (treeItem!!.isExpanded) OPEN_FOLDER_ICON else CLOSED_FOLDER_ICON)
            styleClass.add(ProjectTreeUtils.CellType.DIRECTORY.styleClass)
        } else {
            val icon = when (item.extension.lowercase()) {
                "png", "jpg", "jpeg", "gif", "bmp", "webp", "svg" -> IMAGE_ICON
            //"pdf" -> "icons/pdf.png"
            // "md" -> "icons/md.png"
            else -> FILE_ICON
            }
            styleClass.add(ProjectTreeUtils.CellType.FILE.styleClass)
            graphic = loadIcon(icon)
        }

        // Context menu
        contextMenu = ProjectTreeContextMenu.create(treeView, item, treeItem!!)
    }

}
