package com.toon.kotlin.toontokotlinclass.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTabbedPane
import com.toon.kotlin.toontokotlinclass.generator.AnnotationFramework
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

/**
 * Advanced settings holder and dialog for fine-grained control
 * over code generation options.
 *
 * Provides a 4-tab interface (Property, Annotation, Other, Extensions)
 * matching the JsonToKotlinClass advanced settings layout.
 *
 * Settings are stored as plain properties. Call [showAndGet] to open
 * a dialog that lets the user edit them.
 */
class AdvancedSettingsDialog {

    // ─── Property Tab Settings ───────────────────────────────────────────
    var useVal = true
    var nullableType = NullableType.NON_NULLABLE
    var defaultValueStrategy = DefaultValueStrategy.NO_DEFAULT

    // ─── Annotation Tab Settings ─────────────────────────────────────────
    var annotationFramework = AnnotationFramework.None
    var customAnnotationTemplate = ""

    // ─── Other Tab Settings ──────────────────────────────────────────────
    var appendOriginalToon = false
    var enableComment = false
    var enableOrderByAlphabetical = true
    var enableInnerClassModel = false
    var enableMapTypeForPrimitiveKey = false
    var onlyCreateAnnotationsWhenNeeded = false
    var autoDetectSchema = true
    var indentSpaces = 4
    var parentClassTemplate = ""

    // ─── Extensions Tab Settings ─────────────────────────────────────────
    var enableKeepAnnotation = false
    var enableKeepAnnotationAndroidX = true
    var keepAnnotationAndPropertyInSameLine = false
    var enableParcelableSupport = false
    var propertyPrefix = ""
    var propertySuffix = ""
    var classPrefix = ""
    var classSuffix = ""
    var forcePrimitiveNonNullable = false
    var forceInitDefaultWithOriginValue = false
    var disableKotlinDataClass = false
    var replaceConstructorWithMemberVars = false
    var enableAnonymousAnalytic = false
    var enableCamelCase = false
    var makeStaticFromJsonObject = false
    var classesNonNullable = false
    var classesNonNullableList = ""
    var letClassesBeInternal = false
    var addGsonExposeAnnotation = false

    /**
     * Displays the Advanced Settings dialog and returns true if the user clicked OK.
     */
    fun showAndGet(): Boolean {
        // Build the panel BEFORE creating the dialog to surface any errors early
        val tabbedPanel = buildTabbedPanel()

        val dialog = object : DialogWrapper(true) {
            init {
                title = "Advanced"
                init()
            }

            override fun createCenterPanel(): JComponent = tabbedPanel
        }
        return dialog.showAndGet()
    }

    /**
     * Builds the main tabbed panel with 4 tabs: Property, Annotation, Other, Extensions.
     */
    private fun buildTabbedPanel(): JComponent {
        val tabbedPane = JBTabbedPane()
        tabbedPane.addTab("Property", buildPropertyTab())
        tabbedPane.addTab("Annotation", buildAnnotationTab())
        tabbedPane.addTab("Other", buildOtherTab())
        tabbedPane.addTab("Extensions", buildExtensionsTab())
        tabbedPane.preferredSize = Dimension(620, 550)
        return tabbedPane
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Helpers for building radio button groups
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Creates a vertical radio button group and returns the panel.
     * Calls [onSelected] when a radio button is clicked.
     */
    private fun <T> makeRadioGroup(
        options: List<Pair<String, T>>,
        selected: T,
        onSelected: (T) -> Unit
    ): JPanel {
        val panel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }
        val group = ButtonGroup()

        options.forEach { (label, value) ->
            val rb = JRadioButton(label, value == selected)
            rb.addActionListener { onSelected(value) }
            group.add(rb)
            panel.add(rb)
        }
        return panel
    }

    /**
     * Creates a 2-column radio button grid and returns the panel.
     */
    private fun <T> makeRadioGrid(
        options: List<Pair<String, T>>,
        selected: T,
        onSelected: (T) -> Unit
    ): JPanel {
        val rows = (options.size + 1) / 2
        val panel = JPanel(GridLayout(rows, 2, 20, 12))
        val group = ButtonGroup()

        options.forEach { (label, value) ->
            val rb = JRadioButton(label, value == selected)
            rb.addActionListener { onSelected(value) }
            group.add(rb)
            panel.add(rb)
        }
        return panel
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Property Tab
    // ═══════════════════════════════════════════════════════════════════

    private fun buildPropertyTab(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(12, 16, 12, 16)

        // Keyword section
        panel.add(makeSection("Keyword", makeRadioGroup(
            listOf("Val" to true, "Var" to false),
            useVal
        ) { useVal = it }))

        panel.add(makeSeparator())

        // Type section
        panel.add(makeSection("Type", makeRadioGroup(
            listOf(
                "Non-Nullable" to NullableType.NON_NULLABLE,
                "Nullable" to NullableType.NULLABLE,
                "Auto Determine Nullable Or Not From TOON Value" to NullableType.AUTO
            ),
            nullableType
        ) { nullableType = it }))

        panel.add(makeSeparator())

        // Default Value Strategy section
        panel.add(makeSection("Default Value Strategy", makeRadioGroup(
            listOf(
                "Don't Init With Default Value" to DefaultValueStrategy.NO_DEFAULT,
                "Init With Non-Null Default Value (Avoid Null)" to DefaultValueStrategy.NON_NULL_DEFAULT,
                "Init With Default Value Null When Property Is Nullable" to DefaultValueStrategy.NULL_WHEN_NULLABLE
            ),
            defaultValueStrategy
        ) { defaultValueStrategy = it }))

        panel.add(Box.createVerticalGlue())
        return JScrollPane(panel)
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Annotation Tab
    // ═══════════════════════════════════════════════════════════════════

    private fun buildAnnotationTab(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(16, 16, 16, 16)

        // 2-column radio grid
        panel.add(makeRadioGrid(
            listOf(
                "None" to AnnotationFramework.None,
                "None (Camel Case)" to AnnotationFramework.NoneCamelCase,
                "Gson" to AnnotationFramework.Gson,
                "Jackson" to AnnotationFramework.Jackson,
                "Fastjson" to AnnotationFramework.Fastjson,
                "MoShi (Reflect)" to AnnotationFramework.MoshiReflect,
                "MoShi (Codegen)" to AnnotationFramework.MoshiCodegen,
                "LoganSquare" to AnnotationFramework.LoganSquare,
                "kotlinx.serialization" to AnnotationFramework.Kotlinx,
                "Others by customize" to AnnotationFramework.Custom,
                "Firebase" to AnnotationFramework.Firebase
            ),
            annotationFramework
        ) { annotationFramework = it })

        panel.add(Box.createVerticalStrut(12))

        // Custom annotation template row
        val templatePanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(JLabel("Custom annotation template: "))
            val field = JTextField(customAnnotationTemplate, 30)
            field.toolTipText = "Use %s as placeholder for field name, e.g. @MyAnnotation(\"%s\")"
            field.addActionListener { customAnnotationTemplate = field.text }
            add(field)
        }
        panel.add(templatePanel)

        panel.add(Box.createVerticalGlue())
        return JScrollPane(panel)
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Other Tab
    // ═══════════════════════════════════════════════════════════════════

    private fun buildOtherTab(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(12, 16, 12, 16)

        panel.add(makeCheckBox("Append original TOON", appendOriginalToon) { appendOriginalToon = it })
        panel.add(makeCheckBox("Enable Comment", enableComment) { enableComment = it })
        panel.add(makeCheckBox("Enable Order By Alphabetical", enableOrderByAlphabetical) { enableOrderByAlphabetical = it })
        panel.add(makeCheckBox("Enable Inner Class Model", enableInnerClassModel) { enableInnerClassModel = it })
        panel.add(makeCheckBox("Enable Map Type when Field Key Is Primitive Type", enableMapTypeForPrimitiveKey) { enableMapTypeForPrimitiveKey = it })
        panel.add(makeCheckBox("Only create annotations when needed", onlyCreateAnnotationsWhenNeeded) { onlyCreateAnnotationsWhenNeeded = it })
        panel.add(makeCheckBox("Auto detect TOON Schema", autoDetectSchema) { autoDetectSchema = it })

        panel.add(makeSeparator())

        // Indent row
        val indentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = JPanel.LEFT_ALIGNMENT
            add(JLabel("Indent (number of space):  "))
            val field = JTextField(indentSpaces.toString(), 4)
            field.maximumSize = Dimension(60, 30)
            field.addActionListener { indentSpaces = field.text.toIntOrNull() ?: 4 }
            add(field)
            add(Box.createHorizontalGlue())
        }
        panel.add(indentPanel)
        panel.add(Box.createVerticalStrut(8))

        // Parent class template row
        val templatePanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = JPanel.LEFT_ALIGNMENT
            add(JLabel("Parent Class Template:  "))
            val field = JTextField(parentClassTemplate, 30)
            field.addActionListener { parentClassTemplate = field.text }
            add(field)
        }
        panel.add(templatePanel)

        panel.add(Box.createVerticalGlue())
        return JScrollPane(panel)
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Extensions Tab
    // ═══════════════════════════════════════════════════════════════════

    private fun buildExtensionsTab(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(8, 16, 8, 16)

        panel.add(makeCheckBox("Add @Keep Annotation On Class", enableKeepAnnotation) { enableKeepAnnotation = it })
        panel.add(makeCheckBox("Add @Keep Annotation On Class (AndroidX)", enableKeepAnnotationAndroidX) { enableKeepAnnotationAndroidX = it })
        panel.add(makeCheckBox("Keep Annotation And Property In Same Line", keepAnnotationAndPropertyInSameLine) { keepAnnotationAndPropertyInSameLine = it })
        panel.add(makeCheckBox("Enable Parcelable Support", enableParcelableSupport) { enableParcelableSupport = it })

        panel.add(makeSeparator())

        // Prefix/Suffix fields
        panel.add(makeCheckBoxWithField("Prefix append before every property:", propertyPrefix) { propertyPrefix = it })
        panel.add(makeCheckBoxWithField("Suffix append after every property:", propertySuffix) { propertySuffix = it })
        panel.add(makeCheckBoxWithField("Prefix append before every class name:", classPrefix) { classPrefix = it })
        panel.add(makeCheckBoxWithField("Suffix append after every class name:", classSuffix) { classSuffix = it })

        panel.add(makeSeparator())

        panel.add(makeCheckBox("Force Primitive Type Property Non-Nullable", forcePrimitiveNonNullable) { forcePrimitiveNonNullable = it })
        panel.add(makeCheckBox("Force init Default Value With Origin TOON Value", forceInitDefaultWithOriginValue) { forceInitDefaultWithOriginValue = it })
        panel.add(makeCheckBox("Disable Kotlin Data Class", disableKotlinDataClass) { disableKotlinDataClass = it })
        panel.add(makeCheckBox("Replace constructor parameters by member variables", replaceConstructorWithMemberVars) { replaceConstructorWithMemberVars = it })
        panel.add(makeCheckBox("Enable anonymous analytic", enableAnonymousAnalytic) { enableAnonymousAnalytic = it })
        panel.add(makeCheckBox("Let properties' name to be camel case", enableCamelCase) { enableCamelCase = it })
        panel.add(makeCheckBox("Make a static function that can build from JSONObject", makeStaticFromJsonObject) { makeStaticFromJsonObject = it })

        panel.add(makeSeparator())

        panel.add(makeCheckBoxWithField("Classes non-nullable:", classesNonNullableList) { classesNonNullableList = it })
        panel.add(makeCheckBox("Let classes to be internal", letClassesBeInternal) { letClassesBeInternal = it })
        panel.add(makeCheckBox("Add Gson @Expose Annotation", addGsonExposeAnnotation) { addGsonExposeAnnotation = it })

        panel.add(Box.createVerticalGlue())
        return JScrollPane(panel)
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Swing Helper Methods
    // ═══════════════════════════════════════════════════════════════════

    private fun makeCheckBox(label: String, selected: Boolean, onChange: (Boolean) -> Unit): JCheckBox {
        return JCheckBox(label, selected).apply {
            alignmentX = JCheckBox.LEFT_ALIGNMENT
            addActionListener { onChange(isSelected) }
        }
    }

    private fun makeCheckBoxWithField(label: String, value: String, onChange: (String) -> Unit): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = JPanel.LEFT_ALIGNMENT
            val cb = JCheckBox(label)
            add(cb)
            val field = JTextField(value, 15)
            field.maximumSize = Dimension(200, 30)
            field.addActionListener { onChange(field.text) }
            add(field)
            add(Box.createHorizontalGlue())
        }
    }

    private fun makeSection(title: String, content: JPanel): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = JPanel.LEFT_ALIGNMENT
            val label = JLabel(title)
            label.font = label.font.deriveFont(java.awt.Font.BOLD)
            add(label)
            add(Box.createVerticalStrut(4))
            content.alignmentX = JPanel.LEFT_ALIGNMENT
            add(content)
        }
    }

    private fun makeSeparator(): JComponent {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = JPanel.LEFT_ALIGNMENT
            add(Box.createVerticalStrut(6))
            add(JSeparator().apply {
                maximumSize = Dimension(Int.MAX_VALUE, 2)
                alignmentX = JSeparator.LEFT_ALIGNMENT
            })
            add(Box.createVerticalStrut(6))
        }
    }

    // ─── Enums ───────────────────────────────────────────────────────────

    enum class NullableType {
        NON_NULLABLE,
        NULLABLE,
        AUTO
    }

    enum class DefaultValueStrategy {
        NO_DEFAULT,
        NON_NULL_DEFAULT,
        NULL_WHEN_NULLABLE
    }
}
