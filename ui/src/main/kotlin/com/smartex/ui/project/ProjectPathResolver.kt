package com.smartex.ui.project

import java.io.File


class ProjectPathResolver(
    private val projectRoot: File
) {

    fun relative(file: File): String {
        return projectRoot.toPath()
            .toAbsolutePath()
            .normalize()
            .relativize(
                file.toPath()
                    .toAbsolutePath()
                    .normalize()
            )
            .toString()
            .replace(File.separatorChar, '/')
    }

    fun resolve(path: String): File {
        return projectRoot.toPath()
            .resolve(path)
            .normalize()
            .toFile()
    }
}