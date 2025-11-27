package com.smartex.ui.main

import java.io.File
import java.util.prefs.Preferences


object RecentProjectsService {
    private val prefs = Preferences.userNodeForPackage(javaClass)
    private const val KEY = "recentProjects"

    fun getRecent(): List<String> =
        prefs.get(KEY, "")
            .split(";")
            .filter { it.isNotBlank() && File(it).exists() }

    fun add(path: File) {
        val updated = (getRecent().toMutableList().apply {
            remove(path.absolutePath)
            add(0, path.absolutePath)
        }).take(10) // limit

        prefs.put(KEY, updated.joinToString(";"))
    }
}
