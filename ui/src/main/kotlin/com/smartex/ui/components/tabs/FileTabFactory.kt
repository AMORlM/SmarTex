package com.smartex.ui.components.tabs

import com.smartex.service.ProjectPathResolver
import com.smartex.ui.components.tabs.latex.LatexEditorTab
import java.io.File

object FileTabFactory {

    fun createFileTab(file: File, pathResolver: ProjectPathResolver): FileTab? {
        if (!file.exists() || file.isDirectory) return null

        return when (file.extension.lowercase()) {
            "tex", "bib", "sty", "cls", "ist" -> LatexEditorTab(file)
            "png", "jpg", "jpeg", "gif", "bmp", "webp" -> ImageTab(file, pathResolver)
            "pdf" -> PdfViewerTab(file)
            // Extend here with other file types (e.g., PDF viewer tab, log file tab)
            else -> TextFileTab(file)
        }
    }
}
