package com.smartex.ui.components.tabs

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.model.TwoDimensional

private const val SHOW_REPLACEMENT = "Show replacement"
private const val HIDE_REPLACE = "Hide replacement"

private const val REPLACE_ALL = "Replace all"
private const val REPLACE = "Replace"

private const val COUNT_LABEL = "0/0"

class FindBar(
    private val codeArea: CodeArea
) : VBox(4.0) {

    private val searchField = TextField()
    private val replaceField = TextField()

    private val countLabel = Label(COUNT_LABEL)

    private val previousButton = Button()
    private val nextButton = Button()

    private val expandButton = Button()
    private val closeButton = Button()

    private val replaceButton = Button(REPLACE)
    private val replaceAllButton = Button(REPLACE_ALL)

    private val findRow = HBox(5.0)
    private val replaceRow = HBox(5.0)

    private var matches: List<Int> = emptyList()
    private var currentMatch = -1


    private var replaceVisible = false

    init {
        styleClass += "find-bar"

        setupFindRow()
        setupReplaceRow()
        setupSearchEvents()

        // Only the find row is visible initially.
        children.add(findRow)

        isVisible = false
        isManaged = false
    }

    // ==================================================
    // Setup
    // ==================================================

    private fun setupFindRow() {

        findRow.apply {
            alignment = Pos.CENTER_LEFT
            styleClass += "find-row"
        }

        searchField.apply {
            promptText = "Find"
            prefWidth = 250.0
            styleClass += "find-field"
        }

        // Let the search field consume the available space.
        HBox.setHgrow(searchField, Priority.ALWAYS)

        countLabel.apply {
            text = COUNT_LABEL
            styleClass += "find-count"
            minWidth = 45.0
            alignment = Pos.CENTER
        }

        previousButton.apply {
            text = "↑"
            tooltip = Tooltip("Previous match")
            isFocusTraversable = false
            styleClass += "find-button"

            setOnAction {
                findPrevious()
                restoreSearchFocus()
            }
        }

        nextButton.apply {
            text = "↓"
            tooltip = Tooltip("Next match")
            isFocusTraversable = false
            styleClass += "find-button"

            setOnAction {
                findNext()
                restoreSearchFocus()
            }
        }

        expandButton.apply {
            text = "❯"
            tooltip = Tooltip(SHOW_REPLACEMENT)
            isFocusTraversable = false
            styleClass += "find-expand-button"

            prefWidth = 30.0
            minWidth = 30.0
            maxWidth = 30.0

            setOnAction {
                toggleReplace()
            }
        }

        closeButton.apply {
            text = "X"
            tooltip = Tooltip("Close")
            isFocusTraversable = false
            styleClass += "find-button"

            setOnAction {
                close()
            }
        }

        val spacer = HBox()
        HBox.setHgrow(spacer, Priority.ALWAYS)

        findRow.children.addAll(
            searchField,
            countLabel,
            previousButton,
            nextButton,
            spacer,
            expandButton,
            closeButton
        )
    }

    private fun setupReplaceRow() {

        replaceRow.apply {
            alignment = Pos.CENTER_LEFT
            styleClass += "replace-row"
        }

        replaceField.apply {
            promptText = "Replace with"
            prefWidth = 250.0
            styleClass += "replace-field"
        }

        HBox.setHgrow(replaceField, Priority.ALWAYS)

        replaceButton.apply {
            text = REPLACE
            styleClass += "replace-button"
            isFocusTraversable = false

            setOnAction {
                replaceCurrent()
            }
        }

        replaceAllButton.apply {
            text = REPLACE_ALL
            styleClass += "replace-button"
            isFocusTraversable = false

            setOnAction {
                replaceAll()
            }
        }

        // The replacement controls stay together on the right.
        val spacer = HBox()
        HBox.setHgrow(spacer, Priority.ALWAYS)

        replaceRow.children.addAll(
            replaceField,
            spacer,
            replaceButton,
            replaceAllButton
        )
    }

    private fun setupSearchEvents() {
        searchField.textProperty().addListener { _, _, _ ->
            updateMatches()
            restoreSearchFocus()
        }

        searchField.setOnKeyPressed { event ->
            when (event.code) {
                KeyCode.ENTER -> {
                    if (event.isShiftDown) {
                        findPrevious()
                    } else {
                        findNext()
                    }
                    event.consume()
                }
                KeyCode.ESCAPE -> {
                    close()
                    event.consume()
                }
                else -> Unit
            }
        }

        replaceField.setOnKeyPressed { event ->
            when (event.code) {
                KeyCode.ENTER -> {
                    replaceCurrent()
                    event.consume()
                }
                KeyCode.ESCAPE -> {
                    close()
                    event.consume()
                }
                else -> Unit
            }
        }
    }

    // ==================================================
    // Open / close
    // ==================================================

    /**
     * Opens the normal Find bar.
     */
    fun open() {
        showBar()

        collapseReplace()

        // Keep the search field focused so the user can immediately type the text to search for.
        restoreSearchFocus()
        updateMatches()
    }

    /**
     * Opens Find directly in Replace mode.
     */
    fun openReplace() {
        showBar()

        expandReplace()

        // Keep the search field focused so the user can immediately type the text to search for.
        restoreSearchFocus()
        updateMatches()
    }

    private fun showBar() {
        isVisible = true
        isManaged = true
    }

    fun close() {
        isVisible = false
        isManaged = false

        matches = emptyList()
        currentMatch = -1

        countLabel.text = COUNT_LABEL

        codeArea.requestFocus()
    }

    // ==================================================
    // Replace expansion
    // ==================================================

    private fun toggleReplace() {
        if (replaceVisible) {
            collapseReplace()
        } else {
            expandReplace()
        }
    }

    private fun expandReplace() {
        if (replaceVisible) {
            return
        }

        replaceVisible = true

        children.add(replaceRow)

        expandButton.text = "❮"
        expandButton.tooltip = Tooltip(HIDE_REPLACE)

        // Focus stays on the search field
        restoreSearchFocus()
    }

    private fun collapseReplace() {
        if (!replaceVisible) {
            return
        }

        replaceVisible = false

        children.remove(replaceRow)

        expandButton.text = "❯"
        expandButton.tooltip = Tooltip(SHOW_REPLACEMENT)

        restoreSearchFocus()
    }

    // ==================================================
    // Searching
    // ==================================================

    private fun updateMatches() {
        if (!findMatches()) {
            return
        }

        if (matches.isEmpty()) {
            currentMatch = -1
            countLabel.text = COUNT_LABEL
            return
        }

        // Try to preserve the user's current position.
        val caret = codeArea.caretPosition

        currentMatch = matches.indexOfFirst {
            it >= caret
        }

        if (currentMatch == -1) {
            currentMatch = 0
        }

        selectCurrentMatch()
    }

    private fun findMatches(): Boolean {
        val query = searchField.text

        if (query.isEmpty()) {
            matches = emptyList()
            currentMatch = -1
            countLabel.text = COUNT_LABEL
            return false
        }

        val text = codeArea.text

        matches = buildList {
            var position = 0

            while (position <= text.length - query.length) {
                val index = text.indexOf(
                    query,
                    startIndex = position
                )
                if (index == -1) {
                    break
                }

                add(index)

                // Move by query length rather than one character.
                position = index + query.length
            }
        }
        return true
    }

    private fun findNext() {
        if (matches.isEmpty()) {
            return
        }

        currentMatch = (currentMatch + 1) % matches.size

        selectCurrentMatch()
    }

    private fun findPrevious() {
        if (matches.isEmpty()) {
            return
        }

        currentMatch =
            if (currentMatch <= 0) {
                matches.lastIndex
            } else {
                currentMatch - 1
            }

        selectCurrentMatch()
    }

    private fun selectCurrentMatch() {
        if (currentMatch !in matches.indices) {
            return
        }

        val start = matches[currentMatch]
        val end = start + searchField.text.length

        /*
         * This changes only the CodeArea selection.
         *
         * It does NOT modify the document, so your dirty
         * flag will not be triggered.
         */
        codeArea.selectRange(start, end)

        val position = codeArea.offsetToPosition(
            start,
            TwoDimensional.Bias.Forward
        )

        codeArea.showParagraphAtTop(position.major)

        countLabel.text =
            "${currentMatch + 1}/${matches.size}"
    }

    // ==================================================
    // Replace
    // ==================================================

    private fun replaceCurrent() {
        if (currentMatch !in matches.indices) {
            return
        }

        val query = searchField.text

        if (query.isEmpty()) {
            return
        }

        val replacement = replaceField.text

        val start = matches[currentMatch]
        val end = start + query.length

        codeArea.replaceText(
            start,
            end,
            replacement
        )

        // Recalculate because replacing text changes all following indexes
        updateMatches()
    }

    private fun replaceAll() {
        val query = searchField.text

        if (query.isEmpty()) {
            return
        }

        val replacement = replaceField.text

        // Replace backwards so earlier indexes remain valid.
        matches
            .asReversed()
            .forEach { start ->

                codeArea.replaceText(
                    start,
                    start + query.length,
                    replacement
                )
            }
        updateMatches()
    }

    // ==================================================
    // Focus
    // ==================================================

    private fun restoreSearchFocus() {
        searchField.requestFocus()
        searchField.positionCaret(searchField.text.length)
    }
}