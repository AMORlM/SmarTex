package com.smartex.ui.project

import com.smartex.ui.GLOBAL_SETTINGS_DIR
import java.io.File


object RecentProjectsService {
    private const val FILE_NAME = "recentProjects.tsv"
    private val file = File(GLOBAL_SETTINGS_DIR, FILE_NAME)
    private const val MAX_LIST_SIZE = 10

    fun add(project: File) {
        val updated = (getRecent().toMutableList().apply {
            remove(project)
            add(0, project)
        }).take(MAX_LIST_SIZE) // limit

        val stringBuilder = StringBuilder()
        updated.map { stringBuilder.appendLine(it) }

        file.writeText(stringBuilder.toString())
    }

    fun getRecent(): List<File> {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            return emptyList()
        }

        val listFiles = file.readLines()

        return listFiles.map { File(it) }
    }
}
