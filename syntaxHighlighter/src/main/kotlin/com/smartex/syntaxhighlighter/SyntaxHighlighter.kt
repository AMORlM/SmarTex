package com.smartex.syntaxhighlighter

import org.fxmisc.richtext.model.StyleSpans

interface SyntaxHighlighter {
    fun highlight(text: String): StyleSpans<Collection<String>>
}