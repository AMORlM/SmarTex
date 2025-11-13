package com.smartex.ui.components.filetabs

import com.smartex.ui.components.PdfViewer
import java.io.File

object FileTabFactory {

    fun createFileTab(file: File): FileTab? {
        if (!file.exists() || file.isDirectory) return null

        return when (file.extension.lowercase()) {
            "tex", "bib", "sty", "cls", "ist" -> LatexEditorTab(file)
            "png", "jpg", "jpeg", "gif", "bmp", "webp" -> ImageTab(file)
            //"pdf" -> PdfViewer(file)
            // Extend here with other file types (e.g., PDF viewer tab, log file tab)
            else -> TextFileTab(file)
        }
    }
}
