package com.smartex.syntaxhighlighter

import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import java.util.regex.Pattern

class LatexHighlighter : SyntaxHighlighter {
    override fun highlight(text: String): StyleSpans<Collection<String>> {
        // Patterns for LaTeX
        val commandPattern = """\\[a-zA-Z]+(\*)?"""        // commands like \command or \command*
        val beginEndPattern = """\\(begin|end)\{[a-zA-Z*]+\}"""  // environments
        val mathPattern = """(\$\$.*?\$\$)|(\$.*?\$)|\\\[(.*?)\\\]|\\\((.*?)\\\)""" // math mode
        val tablePattern = """\\\\|&""" // table row end \\ or column &
        val commentPattern = "%[^\n\r]*"   // from % to end of line

        // Combine into one with named capture
        val combinedPattern = Pattern.compile(
            "(?<COMMAND>$commandPattern)|" +
                    "(?<ENV>$beginEndPattern)|" +
                    "(?<MATH>$mathPattern)|" +
                    "(?<TABLE>$tablePattern)|" +
                    "(?<COMMENT>$commentPattern)",
            Pattern.DOTALL  // allow math or commands across line breaks if needed
        )

        val matcher = combinedPattern.matcher(text)
        val spansBuilder = StyleSpansBuilder<Collection<String>>()
        var lastEnd = 0

        while (matcher.find()) {
            val styleClass = when {
                matcher.group("COMMENT") != null -> "latex-comment"
                matcher.group("MATH") != null -> "latex-math"
                matcher.group("ENV") != null -> "latex-env"
                matcher.group("TABLE") != null -> "latex-table"
                matcher.group("COMMAND") != null -> "latex-command"
                else -> null // default style
            }

            val start = matcher.start()
            val end = matcher.end()
            // text before match
            spansBuilder.add(emptyList(), start - lastEnd)
            if (styleClass != null) {
                spansBuilder.add(listOf(styleClass), end - start)
            } else {
                spansBuilder.add(emptyList(), end - start)
            }
            lastEnd = end
        }

        // remainder
        spansBuilder.add(emptyList(), text.length - lastEnd)
        return spansBuilder.create()
    }
}