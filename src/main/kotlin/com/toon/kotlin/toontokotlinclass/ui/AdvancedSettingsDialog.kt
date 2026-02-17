package com.toon.kotlin.toontokotlinclass.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import com.toon.kotlin.toontokotlinclass.generator.AnnotationFramework
import java.awt.Dimension
import javax.swing.JComponent

/**
 * Advanced settings dialog with tabbed interface for fine-grained control
 * over code generation options.
 */
class AdvancedSettingsDialog : DialogWrapper(true) {

    // Property Tab Settings
    var useVal = true
    var nullableType = NullableType.NON_NULLABLE
    var defaultValueStrategy = DefaultValueStrategy.NO_DEFAULT
    
    // Annotation Tab Settings
    var annotationFramework = AnnotationFramework.Gson
    
    // Other Tab Settings
    var enableOrderByAlphabetical = true
    var enableInnerClassModel = false
    var indentSpaces = 4
    var parentClassTemplate = ""
    
    // Extensions Tab Settings
    var enableKeepAnnotation = false
    var propertyPrefix = ""
    var propertySuffix = ""
    var classPrefix = ""
    var classSuffix = ""
    var forcePrimitiveNonNullable = false
    var enableCamelCase = true

    init {
        title = "Advanced Settings"
        init()
        window.preferredSize = Dimension(600, 500)
        window.pack()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            
            // Property Tab
            group("Property") {
                
                buttonsGroup {
                    row {
                        label("Keyword:")
                    }
                    row {
                        radioButton("Val", true).apply {
                            component.addActionListener { useVal = true }
                        }
                    }
                    row {
                        radioButton("Var", false).apply {
                            component.addActionListener { useVal = false }
                        }
                    }
                }
                
                separator()
                
                buttonsGroup {
                    row {
                        label("Type:")
                    }
                    row {
                        radioButton("Non-Nullable", true).apply {
                            component.addActionListener { nullableType = NullableType.NON_NULLABLE }
                        }
                    }
                    row {
                        radioButton("Nullable", false).apply {
                            component.addActionListener { nullableType = NullableType.NULLABLE }
                        }
                    }
                    row {
                        radioButton("Auto Determine Nullable From Value", false).apply {
                            component.addActionListener { nullableType = NullableType.AUTO }
                        }
                    }
                }
                
                separator()
                
                buttonsGroup {
                    row {
                        label("Default Value Strategy:")
                    }
                    row {
                        radioButton("Don't Init With Default Value", true).apply {
                            component.addActionListener { defaultValueStrategy = DefaultValueStrategy.NO_DEFAULT }
                        }
                    }
                    row {
                        radioButton("Init With Non-Null Default Value (Avoid Null)", false).apply {
                            component.addActionListener { defaultValueStrategy = DefaultValueStrategy.NON_NULL_DEFAULT }
                        }
                    }
                    row {
                        radioButton("Init With Default Value Null When Property Is Nullable", false).apply {
                            component.addActionListener { defaultValueStrategy = DefaultValueStrategy.NULL_WHEN_NULLABLE }
                        }
                    }
                }
            }
            
            // Annotation Tab
            group("Annotation") {
                buttonsGroup {
                    row {
                        radioButton("Gson", annotationFramework == AnnotationFramework.Gson).apply {
                            component.addActionListener { annotationFramework = AnnotationFramework.Gson }
                        }
                    }
                    row {
                        radioButton("Moshi", annotationFramework == AnnotationFramework.Moshi).apply {
                            component.addActionListener { annotationFramework = AnnotationFramework.Moshi }
                        }
                    }
                    row {
                        radioButton("Kotlinx Serialization", annotationFramework == AnnotationFramework.Kotlinx).apply {
                            component.addActionListener { annotationFramework = AnnotationFramework.Kotlinx }
                        }
                    }
                    row {
                        radioButton("Firebase", annotationFramework == AnnotationFramework.Firebase).apply {
                            component.addActionListener { annotationFramework = AnnotationFramework.Firebase }
                        }
                    }
                }
            }
            
            // Other Tab
            group("Other") {
                row {
                    checkBox("Enable Order By Alphabetical")
                        .applyToComponent {
                            isSelected = enableOrderByAlphabetical
                            addActionListener { enableOrderByAlphabetical = isSelected }
                        }
                }
                row {
                    checkBox("Enable Inner Class Model")
                        .applyToComponent {
                            isSelected = enableInnerClassModel
                            addActionListener { enableInnerClassModel = isSelected }
                        }
                }
                
                row {
                    label("Indent (number of spaces):")
                    intTextField(1..8)
                        .applyToComponent {
                            text = indentSpaces.toString()
                            addActionListener { 
                                indentSpaces = text.toIntOrNull() ?: 4
                            }
                        }
                }
                
                row {
                    label("Parent Class Template:")
                    textField()
                        .applyToComponent {
                            text = parentClassTemplate
                            addActionListener {
                                parentClassTemplate = text
                            }
                        }
                        .resizableColumn()
                }
            }
            
            // Extensions Tab
            group("Extensions") {
                row {
                    checkBox("Add @Keep Annotation On Class (AndroidX)")
                        .applyToComponent {
                            isSelected = enableKeepAnnotation
                            addActionListener { enableKeepAnnotation = isSelected }
                        }
                }
                
                row {
                    checkBox("Prefix append before every property:")
                    textField()
                        .applyToComponent {
                            text = propertyPrefix
                            addActionListener { propertyPrefix = text }
                        }
                }
                
                row {
                    checkBox("Suffix append after every property:")
                    textField()
                        .applyToComponent {
                            text = propertySuffix
                            addActionListener { propertySuffix = text }
                        }
                }
                
                row {
                    checkBox("Prefix append before every class name:")
                    textField()
                        .applyToComponent {
                            text = classPrefix
                            addActionListener { classPrefix = text }
                        }
                }
                
                row {
                    checkBox("Suffix append after every class name:")
                    textField()
                        .applyToComponent {
                            text = classSuffix
                            addActionListener { classSuffix = text }
                        }
                }
                
                row {
                    checkBox("Force Primitive Type Property Non-Nullable")
                        .applyToComponent {
                            isSelected = forcePrimitiveNonNullable
                            addActionListener { forcePrimitiveNonNullable = isSelected }
                        }
                }
                
                row {
                    checkBox("Let properties' name to be camel case")
                        .applyToComponent {
                            isSelected = enableCamelCase
                            addActionListener { enableCamelCase = isSelected }
                        }
                }
            }
        }
    }
    
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
