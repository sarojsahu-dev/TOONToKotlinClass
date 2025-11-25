package com.toon.kotlin.toontokotlinclass.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JTextArea
import javax.swing.JScrollPane

class ToonGenerateDialog : DialogWrapper(true) {

    private val toonInputArea = JTextArea(12, 40)
    private val previewArea = JTextArea(12, 40)

    private var annotationType: String = "Gson"
    private var useVal = true
    private var useNullable = true

    init {
        title = "TOON → Kotlin Data Class Generator"
        init()
    }

    override fun createCenterPanel(): JComponent {

        return panel {

            row("TOON Input:") {
                cell(JScrollPane(toonInputArea))
                    .align(Align.FILL)
                    .resizableColumn()
            }

            separator()

            row("Annotation Framework:") {
                comboBox(listOf("Gson", "Moshi", "Kotlinx Serialization", "Firebase"))
                    .applyToComponent {
                        selectedItem = annotationType
                        addActionListener {
                            annotationType = selectedItem.toString()
                        }
                    }
            }

            row {
                checkBox("Use val (instead of var)")
                    .applyToComponent {
                        isSelected = useVal
                        addActionListener {
                            useVal = isSelected
                        }
                    }

                checkBox("Nullable fields?")
                    .applyToComponent {
                        isSelected = useNullable
                        addActionListener {
                            useNullable = isSelected
                        }
                    }
            }

            separator()

            row("Kotlin Preview:") {
                cell(JScrollPane(previewArea))
                    .align(Align.FILL)
                    .resizableColumn()
            }
        }
    }

    fun getToonInput(): String = toonInputArea.text
    fun getAnnotationType(): String = annotationType
    fun isUseVal(): Boolean = useVal
    fun isUseNullable(): Boolean = useNullable

    fun setPreview(text: String) {
        previewArea.text = text
    }
}