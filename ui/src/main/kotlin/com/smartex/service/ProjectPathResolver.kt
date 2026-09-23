package com.smartex.service

import java.io.File


class ProjectPathResolver(
    private val projectRoot: File
) {

    fun relative(file: File): String {
        return projectRoot.toPath()
            .toAbsolutePath()
            .relativize(
                file.toPath()
                    .toAbsolutePath()
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