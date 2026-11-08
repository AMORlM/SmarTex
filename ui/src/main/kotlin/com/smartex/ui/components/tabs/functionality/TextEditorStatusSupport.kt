package com.smartex.ui.components.tabs.functionality

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import java.nio.charset.Charset

interface TextEditorStatusSupport {
    fun getObsEncoder(): SimpleObjectProperty<Charset>
    fun getObsLineBreak(): SimpleObjectProperty<LineBreak>
    fun getObsLineCol(): ObservableValue<Pos>
}