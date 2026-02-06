package com.smartex.ui.settings

enum class EditorAction(val id: String) {
    SAVE("saveFile"),
    BOLD("latex.bold"),
    ITALIC("latex.italic"),
    UNDERLINE("latex.underline"),
    ITEMIZE("latex.itemize"),
    ENUMERATE("latex.enumerate"),
    SECTION("latex.section"),
    SUBSECTION("latex.subsection"),
    PARAGRAPH("latex.paragraph"),
    INLINE_MATH("latex.inlineMath"),
    EQUATION("latex.equation"),
    CITE("latex.cite"),
    REFERENCE("latex.ref"),
    HREF("latex.href"),
    FIGURE("latex.figure")
}
