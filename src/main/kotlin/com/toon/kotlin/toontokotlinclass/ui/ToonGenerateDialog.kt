package com.toon.kotlin.toontokotlinclass.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.toon.kotlin.toontokotlinclass.generator.AnnotationFramework
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * Main dialog for TOON to Kotlin data class generation.
 * Provides a split-view interface with live preview and configuration options.
 */
class ToonGenerateDialog : DialogWrapper(true) {

    // Input area for TOON format text
    private val toonInputArea = JTextArea(25, 60).apply {
        lineWrap = true
        wrapStyleWord = true
        font = font.deriveFont(13f)
    }
    
    // Preview area for generated Kotlin code (read-only)
    private val previewArea = JTextArea(25, 60).apply {
        isEditable = false
        lineWrap = false
        font = font.deriveFont(12f)
        background = UIManager.getColor("TextField.background")
    }
    
    // Configuration fields
    private val packageNameField = JTextField("com.example.models", 30)
    private val statusLabel = JLabel(" ")
    
    // Advanced settings
    private val advancedSettings = AdvancedSettingsDialog()

    // Generation options (now linked to advanced settings)
    private var annotationType: AnnotationFramework
        get() = advancedSettings.annotationFramework
        set(value) { advancedSettings.annotationFramework = value }
        
    private var useVal: Boolean
        get() = advancedSettings.useVal
        set(value) { advancedSettings.useVal = value }
        
    private var useNullable: Boolean
        get() = advancedSettings.nullableType == AdvancedSettingsDialog.NullableType.NULLABLE
        set(value) { 
            advancedSettings.nullableType = if (value) {
                AdvancedSettingsDialog.NullableType.NULLABLE
            } else {
                AdvancedSettingsDialog.NullableType.NON_NULLABLE
            }
        }

    // Callbacks for preview and file generation
    var onGenerateClicked: ((Map<String, String>) -> Unit)? = null
    var onUpdatePreview: ((String) -> Map<String, String>)? = null

    private var updateTimer: Timer? = null

    init {
        title = "TOON → Kotlin Data Class Generator"
        setupAutoPreview()
        init()
        
        // Set dialog size
        window.preferredSize = Dimension(1200, 800)
        window.pack()
    }

    /**
     * Sets up automatic preview updates with debouncing
     */
    private fun setupAutoPreview() {
        toonInputArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = schedulePreviewUpdate()
            override fun removeUpdate(e: DocumentEvent?) = schedulePreviewUpdate()
            override fun changedUpdate(e: DocumentEvent?) = schedulePreviewUpdate()
        })
    }

    /**
     * Schedules a preview update with 500ms debounce
     */
    private fun schedulePreviewUpdate() {
        updateTimer?.stop()
        updateTimer = Timer(500) {
            updatePreview()
        }.apply {
            isRepeats = false
            start()
        }
    }

    /**
     * Updates the preview area with generated Kotlin code
     */
    private fun updatePreview() {
        try {
            val toon = toonInputArea.text
            if (toon.isBlank()) {
                setPreview("// Enter TOON format on the left to see preview")
                setStatus("Ready", false)
                return
            }
            
            val previewFiles = onUpdatePreview?.invoke(toon) ?: emptyMap()
            
            if (previewFiles.isEmpty()) {
                setPreview("// No classes generated")
                setStatus("No output generated", true)
            } else {
                setPreview(prettyPrint(previewFiles))
                setStatus("✓ ${previewFiles.size} file(s) ready", false)
            }
        } catch (e: Exception) {
            setPreview("// Error: ${e.message}")
            setStatus("⚠ ${e.message}", true)
        }
    }

    override fun createActions(): Array<Action> {
        
        val generateButton = object : DialogWrapperAction("Generate Files") {
            init {
                putValue(DEFAULT_ACTION, true)
                putValue(MNEMONIC_KEY, KeyEvent.VK_G)
            }
            
            override fun doAction(e: ActionEvent?) {
                try {
                    val toon = toonInputArea.text
                    if (toon.isBlank()) {
                        setStatus("⚠ Input cannot be empty", true)
                        return
                    }
                    
                    val previewFiles = onUpdatePreview?.invoke(toon) ?: emptyMap()
                    if (previewFiles.isEmpty()) {
                        setStatus("⚠ No files to generate", true)
                        return
                    }
                    
                    onGenerateClicked?.invoke(previewFiles)
                    close(0)
                } catch (e: Exception) {
                    setStatus("⚠ Error: ${e.message}", true)
                }
            }
        }

        return arrayOf(generateButton, cancelAction)
    }

    override fun createCenterPanel(): JComponent {
        
        return panel {
            
            // Header
            row {
                label("Convert TOON format to Kotlin data classes").apply {
                    component.font = component.font.deriveFont(14f)
                }
            }
            
            separator()
            
            // Main content: side-by-side input and preview
            row {
                // Left panel: TOON Input
                panel {
                    row {
                        label("TOON Input:").bold()
                        
                        button("Format") {
                            formatToonInput()
                        }
                        
                        button("Clear") {
                            toonInputArea.text = ""
                        }
                        
                        button("Advanced") {
                            if (advancedSettings.showAndGet()) {
                                updatePreview()
                            }
                        }
                    }
                    
                    row {
                        scrollCell(toonInputArea)
                            .align(Align.FILL)
                            .resizableColumn()
                    }.resizableRow()
                    
                }.resizableColumn()
                
                // Right panel: Kotlin Preview
                panel {
                    row {
                        label("Kotlin Preview:").bold()
                    }
                    
                    row {
                        scrollCell(previewArea)
                            .align(Align.FILL)
                            .resizableColumn()
                    }.resizableRow()
                    
                }.resizableColumn()
            }.resizableRow()
            
            separator()
            
            // Configuration options
            row {
                label("Package Name:")
                cell(packageNameField)
                    .resizableColumn()
            }
            
            row {
                label("Annotation Framework:")
                comboBox(AnnotationFramework.entries)
                    .applyToComponent {
                        selectedItem = annotationType
                        addActionListener {
                            annotationType = selectedItem as AnnotationFramework
                            updatePreview()
                        }
                    }
                
                checkBox("Use val")
                    .applyToComponent {
                        isSelected = useVal
                        addActionListener { 
                            useVal = isSelected
                            updatePreview()
                        }
                    }

                checkBox("Nullable fields")
                    .applyToComponent {
                        isSelected = useNullable
                        addActionListener { 
                            useNullable = isSelected
                            updatePreview()
                        }
                    }
            }
            
            // Status bar
            row {
                cell(statusLabel)
                    .align(Align.FILL)
            }
            
            // Help text
            row {
                comment("""
                    <html>
                    <b>Format:</b> Use 2-space indentation | 
                    <b>Objects:</b> key: | 
                    <b>Lists:</b> key[n]: a,b,c | 
                    <b>Object Lists:</b> key[n]{schema}: rows | 
                    <b>Shortcut:</b> Ctrl/Cmd+G to generate
                    </html>
                """.trimIndent())
            }
        }
    }

    // Accessor methods
    fun getAnnotationFramework() = annotationType
    fun isUseVal() = useVal
    fun isUseNullable() = useNullable
    fun getPackageName() = packageNameField.text.trim()

    /**
     * Sets the preview text and resets scroll position
     */
    fun setPreview(text: String) {
        previewArea.text = text
        previewArea.caretPosition = 0
    }
    
    /**
     * Sets the status message with appropriate color
     */
    private fun setStatus(message: String, isError: Boolean) {
        statusLabel.text = message
        statusLabel.foreground = if (isError) {
            UIManager.getColor("Label.errorForeground") ?: java.awt.Color.RED
        } else {
            UIManager.getColor("Label.foreground")
        }
    }

    /**
     * Formats the TOON input with proper indentation
     */
    private fun formatToonInput() {
        val input = toonInputArea.text
        if (input.isBlank()) return
        
        try {
            val lines = input.lines()
            val formatted = StringBuilder()
            
            lines.forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isNotEmpty()) {
                    // Count leading spaces
                    val leadingSpaces = line.takeWhile { it == ' ' }.length
                    // Round to nearest multiple of 2
                    val normalizedIndent = (leadingSpaces / 2) * 2
                    formatted.append(" ".repeat(normalizedIndent))
                    formatted.append(trimmed)
                    formatted.append("\n")
                }
            }
            
            toonInputArea.text = formatted.toString().trimEnd()
            setStatus("✓ Formatted", false)
        } catch (e: Exception) {
            setStatus("⚠ Format failed: ${e.message}", true)
        }
    }

    /**
     * Formats multiple files for preview display
     */
    private fun prettyPrint(map: Map<String, String>): String {
        return map.entries.joinToString("\n\n${"=".repeat(60)}\n\n") {
            "// FILE: ${it.key}\n\n${it.value}"
        }
    }
}
