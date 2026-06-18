package com.smartex.ui.components.compilation.log

import com.smartex.logging.LogEntry
import com.smartex.logging.LogLevel

object LogProcesser {
    private val PASS_SEPARATOR = Regex("""^=+ Running .*?: \d+(st|nd|rd|th) pass =+""", RegexOption.MULTILINE)
    private val ENDED_FILE_METADATA = Regex("""\[.*]""")

    private val FILE_OPEN = Regex("""\("?([^")]+\.tex)"?""")
    private val LINE_RANGE = Regex("""lines (\d+)--(\d+)""")
    private val LINE_SINGLE = Regex("""l\.(\d+)""")

    fun detectLevel(line: String): LogLevel? = when {
        line.startsWith("!") -> LogLevel.ERROR
        line.contains("Warning") -> LogLevel.WARN
        line.contains("Overfull") -> LogLevel.INFO
        line.contains("Underfull") -> LogLevel.INFO
        else -> null
    }

    fun process(text: String): List<LogEntry> {
        val lastPass = extractLastPass(text)
        val entries = mutableListOf<LogEntry>()

        val lines = lastPass.lines()

        var currentFile =  lines.firstNotNullOfOrNull { FILE_OPEN.find(it)?.groupValues?.last() }

        var startLog = false

        for (i in 0..<lines.size) {
            val line = lines[i]

            // ignore compiler metadata
            if (!startLog) {
                ENDED_FILE_METADATA.find(line)?.let {
                    startLog = true
                }
                continue
            }

            // Update file context
            FILE_OPEN.findAll(line).lastOrNull()?.let {
                currentFile = it.groupValues.last()
                continue
            }

        val level = detectLevel(line)
            level?.let {
                val title = extractTitle(line)
                val message = extractMessage(lines, i)

                val (start, end) = extractLines(message)

                entries += LogEntry(
                    level = level,
                    title = title,
                    message = message.trim(),
                    source = currentFile,
                    lineStart = start,
                    lineEnd = end,
                    details = message,
                    code = "latex.generic"
                )
            }
        }

        return entries.sortedByDescending { it.level }
    }

    private fun extractLastPass(log: String): String {
        val matches = PASS_SEPARATOR.findAll(log).toList()

        if (matches.isEmpty()) {
            // Single-pass compilation or custom engine
            return log
        }

        val last = matches.last()
        return log.substring(last.range.last + 1)
    }

    private fun extractTitle(line: String): String =
        line.removePrefix("!").take(60)

    private fun extractMessage(lines: List<String>, start: Int): String {
        val sb = StringBuilder()
        for (i in start until minOf(start + 5, lines.size)) {
            if (lines[i].isBlank()) break
            sb.appendLine(lines[i])
        }
        return sb.toString()
    }

    private fun extractLines(text: String): Pair<Int?, Int?> {
        LINE_RANGE.find(text)?.let {
            return it.groupValues[1].toInt() to it.groupValues[2].toInt()
        }
        LINE_SINGLE.find(text)?.let {
            val l = it.groupValues[1].toInt()
            return l to l
        }
        return null to null
    }

}