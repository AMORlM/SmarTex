package com.smartex.ui.components.tabs.functionality

import javafx.beans.value.ObservableValue
import java.nio.charset.Charset

interface TextEditorStatusSupport {
    fun getObsEncoder(): ObservableValue<Charset>
    fun getObsLineBreak(): ObservableValue<LineBreak>
    fun getObsLineCol(): ObservableValue<Pos>
}