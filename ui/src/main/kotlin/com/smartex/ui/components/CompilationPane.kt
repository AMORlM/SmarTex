package com.smartex.ui.components

import com.smartex.latexcompiler.LatexCompiler
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import java.io.File
import java.io.OutputStream
import java.io.PrintStream

class CompilationPane(val projectRoot: File) : BorderPane(){
    lateinit var pdf: PdfViewer

    val mainName: String
    val compiler: LatexCompiler

    val logger = TextArea()
    val ps = PrintStream(TextAreaOutputStream(logger))

    init {
        top = makeToolBar()
        // Placeholder for PDF preview pane
        center = logger
        logger.isEditable  = false

        mainName = "Recipes"

        compiler = LatexCompiler(projectRoot, mainName)
    }

    private fun makeToolBar(): ToolBar {
        // Toolbar  buttons
        val compileButton = Button("Compile").apply {
            setOnAction {
                // Placeholder action for compile button
                println("Compile button clicked")
                logger.clear()

                Thread {
                    System.setOut(ps)
                    compiler.compile()
                    loadPdfViewer(File(projectRoot, "${mainName}.pdf"))
                    Platform.runLater {
                        center = pdf
                    }
                }.apply {
                    isDaemon = true
                }.start()
            }
        }

        val toggleButton = Button("Toggle view").apply {
            setOnAction {
                if(!::pdf.isInitialized){
                    println("PDF Viewer not initialized yet")
                    return@setOnAction
                }
                if (center == logger) {
                    center = pdf
                    println("PDF Viewer shown")
                } else {
                    center = logger
                    println("Logger shown")
                }
            }
        }

        val savePDFButton = Button("Save File").apply {
            setOnAction {
                println("copy PDF to other directory")
            }
        }

        return ToolBar(compileButton, toggleButton, savePDFButton)
    }

    fun loadPdfViewer(file: File) {
        pdf = PdfViewer(file)
    }


    class TextAreaOutputStream(private val textArea: TextArea) : OutputStream() {
        private val buffer = StringBuilder()

        override fun write(b: Int) {
            buffer.append(b.toChar())
            if (b == '\n'.code) {
                val text = buffer.toString()
                buffer.clear()
                Platform.runLater {
                    textArea.appendText(text)
                }
            }
        }

        override fun flush() {
            if (buffer.isNotEmpty()) {
                val text = buffer.toString()
                buffer.clear()
                Platform.runLater {
                    textArea.appendText(text)
                }
            }
        }
    }
}
