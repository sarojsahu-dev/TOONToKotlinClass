package com.toon.kotlin.toontokotlinclass.generator

import com.toon.kotlin.toontokotlinclass.parser.NamingUtils
import com.toon.kotlin.toontokotlinclass.parser.ToonNode
import com.toon.kotlin.toontokotlinclass.parser.TypeInference
import com.toon.kotlin.toontokotlinclass.ui.AdvancedSettingsDialog

/**
 * Generates Kotlin data class code from TOON AST nodes.
 *
 * Supports multiple annotation frameworks, configurable code style, and
 * fine-grained generation options via [AdvancedSettingsDialog] settings.
 *
 * @property settings Full advanced settings controlling code generation behaviour
 * @property packageName Optional package name to add to generated files
 */
class KotlinClassGenerator(
    private val settings: AdvancedSettingsDialog,
    private val packageName: String = ""
) {

    // Convenience accessors for frequently used settings
    private val useVal get() = settings.useVal
    private val nullable get() = settings.nullableType == AdvancedSettingsDialog.NullableType.NULLABLE
    private val framework get() = settings.annotationFramework
    private val indent get() = " ".repeat(settings.indentSpaces)

    private val files = mutableMapOf<String, String>()

    /**
     * Generates Kotlin files for all top-level objects in the AST.
     *
     * @param top Map of top-level object names to their AST nodes
     * @return Map of filenames to generated Kotlin code
     */
    fun generateAll(top: Map<String, ToonNode.ObjectNode>): Map<String, String> {
        files.clear()

        top.forEach { (key, node) ->
            val className = applyClassNaming(key.replaceFirstChar { it.uppercase() })
            generateClassFile(className, node.children)
        }

        return files
    }

    /**
     * Generates a Kotlin data class file from a list of ToonNode children.
     *
     * @param className The name of the class to generate
     * @param nodes The list of child nodes (properties, lists, nested objects)
     */
    private fun generateClassFile(className: String, nodes: List<ToonNode>) {

        val imports = AnnotationGenerator.imports(framework).toMutableList()
        imports += AnnotationGenerator.classImports(framework)

        // Add @Keep import if enabled
        if (settings.enableKeepAnnotation) {
            imports += "android.support.annotation.Keep"
        }
        if (settings.enableKeepAnnotationAndroidX) {
            imports += "androidx.annotation.Keep"
        }

        // Add Parcelable imports if enabled
        if (settings.enableParcelableSupport) {
            imports += "android.os.Parcelable"
            imports += "kotlinx.parcelize.Parcelize"
        }

        // Add @Expose import if enabled
        if (settings.addGsonExposeAnnotation) {
            imports += "com.google.gson.annotations.Expose"
        }

        // Collect and optionally sort nodes
        val processedNodes = if (settings.enableOrderByAlphabetical) {
            nodes.sortedBy { it.key }
        } else {
            nodes
        }

        val props = mutableListOf<String>()

        processedNodes.forEach { node ->
            when (node) {
                is ToonNode.Property -> {
                    val propName = resolvePropertyName(node.key)
                    val type = TypeMapper.mapPrimitive(node.value)
                    val ann = buildPropertyAnnotation(node.key)
                    val v = if (useVal) "val" else "var"
                    val q = resolveNullability(type, node.value)
                    val default = resolveDefaultValue(type, node.value, q)

                    props += buildPropertyString(ann, v, propName, type, q, default)
                }

                is ToonNode.ListNode -> {
                    val propName = resolvePropertyName(node.key)
                    val type = TypeMapper.mapList()
                    val ann = buildPropertyAnnotation(node.key)
                    val v = if (useVal) "val" else "var"
                    val default = resolveDefaultValue(type, "", "")

                    props += buildPropertyString(ann, v, propName, type, "", default)
                }

                is ToonNode.ObjectNode -> {
                    val nestedClassName = applyClassNaming(node.key.replaceFirstChar { it.uppercase() })

                    // Generate as inner class or separate file
                    if (settings.enableInnerClassModel) {
                        // Will be handled after props
                    } else {
                        generateClassFile(nestedClassName, node.children)
                    }

                    val propName = resolvePropertyName(node.key)
                    val ann = buildPropertyAnnotation(node.key)
                    val v = if (useVal) "val" else "var"

                    props += buildPropertyString(ann, v, propName, nestedClassName, "", "")
                }

                is ToonNode.ObjectListNode -> {
                    val singular = applyClassNaming(singularize(node.key).replaceFirstChar { it.uppercase() })

                    if (!settings.enableInnerClassModel) {
                        generateObjectListFile(singular, node)
                    }

                    val propName = resolvePropertyName(node.key)
                    val ann = buildPropertyAnnotation(node.key)
                    val v = if (useVal) "val" else "var"
                    val type = "List<$singular>"

                    props += buildPropertyString(ann, v, propName, type, "", "")
                }
            }
        }

        val body = props.joinToString(",\n\n")

        // Build inner classes if enabled
        val innerClasses = if (settings.enableInnerClassModel) {
            buildInnerClasses(processedNodes)
        } else {
            ""
        }

        val fullText = buildClassText(className, imports.distinct(), body, innerClasses)
        files["$className.kt"] = fullText
    }

    /**
     * Generates a Kotlin data class for object list items.
     */
    private fun generateObjectListFile(className: String, node: ToonNode.ObjectListNode) {

        val imports = AnnotationGenerator.imports(framework).toMutableList()
        imports += AnnotationGenerator.classImports(framework)

        if (settings.enableKeepAnnotation) imports += "android.support.annotation.Keep"
        if (settings.enableKeepAnnotationAndroidX) imports += "androidx.annotation.Keep"
        if (settings.enableParcelableSupport) {
            imports += "android.os.Parcelable"
            imports += "kotlinx.parcelize.Parcelize"
        }
        if (settings.addGsonExposeAnnotation) imports += "com.google.gson.annotations.Expose"

        val props = mutableListOf<String>()
        val firstRow = node.rows.firstOrNull()

        // Optionally sort headers alphabetically
        val headerIndices = if (settings.enableOrderByAlphabetical) {
            node.headers.indices.sortedBy { node.headers[it] }
        } else {
            node.headers.indices.toList()
        }

        headerIndices.forEach { idx ->
            val header = node.headers[idx]
            val sample = firstRow?.getOrNull(idx) ?: ""
            val inferred = TypeInference.inferValue(sample)
            val type = TypeMapper.mapPrimitive(inferred)
            val propName = resolvePropertyName(header)
            val ann = buildPropertyAnnotation(header)
            val v = if (useVal) "val" else "var"
            val q = resolveNullability(type, inferred)
            val default = resolveDefaultValue(type, inferred, q)

            props += buildPropertyString(ann, v, propName, type, q, default)
        }

        val body = props.joinToString(",\n\n")
        val fullText = buildClassText(className, imports.distinct(), body, "")
        files["$className.kt"] = fullText
    }

    // ─── Helper Methods ──────────────────────────────────────────────────

    /**
     * Resolves the property name, applying camel case and prefix/suffix.
     */
    private fun resolvePropertyName(key: String): String {
        var name = if (settings.enableCamelCase) {
            NamingUtils.toCamelCase(key)
        } else {
            key
        }
        if (settings.propertyPrefix.isNotEmpty()) name = settings.propertyPrefix + name
        if (settings.propertySuffix.isNotEmpty()) name = name + settings.propertySuffix
        return name
    }

    /**
     * Applies class prefix/suffix to the class name.
     */
    private fun applyClassNaming(name: String): String {
        var result = name
        if (settings.classPrefix.isNotEmpty()) result = settings.classPrefix + result
        if (settings.classSuffix.isNotEmpty()) result = result + settings.classSuffix
        return result
    }

    /**
     * Builds the annotation string for a property, including @Expose if enabled.
     */
    private fun buildPropertyAnnotation(key: String): String {
        val parts = mutableListOf<String>()

        val mainAnn = AnnotationGenerator.generate(framework, key, settings.customAnnotationTemplate)
        if (mainAnn.isNotEmpty()) {
            // Check if we should skip annotation when property name matches camel case
            val shouldSkip = settings.onlyCreateAnnotationsWhenNeeded &&
                    NamingUtils.toCamelCase(key) == key

            if (!shouldSkip) {
                parts += mainAnn
            }
        }

        if (settings.addGsonExposeAnnotation) {
            parts += "@Expose"
        }

        return parts.joinToString("\n$indent")
    }

    /**
     * Builds a full property string with annotation, keyword, name, type, and default value.
     */
    private fun buildPropertyString(
        annotation: String,
        keyword: String,
        name: String,
        type: String,
        nullability: String,
        default: String
    ): String {
        return if (settings.replaceConstructorWithMemberVars) {
            // Member variable style: no constructor, just class body with vars
            buildString {
                if (annotation.isNotEmpty()) {
                    if (settings.keepAnnotationAndPropertyInSameLine) {
                        append("$indent$annotation $keyword $name: $type$nullability$default")
                    } else {
                        append("$indent$annotation\n")
                        append("$indent$keyword $name: $type$nullability$default")
                    }
                } else {
                    append("$indent$keyword $name: $type$nullability$default")
                }
            }
        } else {
            // Constructor parameter style (default)
            buildString {
                if (annotation.isNotEmpty()) {
                    if (settings.keepAnnotationAndPropertyInSameLine) {
                        append("$indent$annotation $keyword $name: $type$nullability$default")
                    } else {
                        append("$indent$annotation\n")
                        append("$indent$keyword $name: $type$nullability$default")
                    }
                } else {
                    append("$indent$keyword $name: $type$nullability$default")
                }
            }
        }
    }

    /**
     * Resolves nullability suffix based on settings.
     */
    private fun resolveNullability(type: String, value: Any): String {
        return when (settings.nullableType) {
            AdvancedSettingsDialog.NullableType.NON_NULLABLE -> {
                if (settings.forcePrimitiveNonNullable && isPrimitiveType(type)) "" else ""
            }
            AdvancedSettingsDialog.NullableType.NULLABLE -> "?"
            AdvancedSettingsDialog.NullableType.AUTO -> {
                // Auto: nullable if value is null/empty, non-nullable otherwise
                val trimmed = value.toString().trim()
                if (trimmed.isEmpty() || trimmed.equals("null", ignoreCase = true)) "?" else ""
            }
        }
    }

    /**
     * Resolves default value based on strategy setting.
     */
    private fun resolveDefaultValue(type: String, value: Any, nullability: String): String {
        return when (settings.defaultValueStrategy) {
            AdvancedSettingsDialog.DefaultValueStrategy.NO_DEFAULT -> {
                if (settings.forceInitDefaultWithOriginValue) {
                    " = ${formatDefaultForType(type, value)}"
                } else {
                    ""
                }
            }
            AdvancedSettingsDialog.DefaultValueStrategy.NON_NULL_DEFAULT -> {
                " = ${getTypeDefault(type)}"
            }
            AdvancedSettingsDialog.DefaultValueStrategy.NULL_WHEN_NULLABLE -> {
                if (nullability == "?") " = null" else ""
            }
        }
    }

    /**
     * Returns the default value for a given type.
     */
    private fun getTypeDefault(type: String): String {
        return when (type) {
            "Int" -> "0"
            "Long" -> "0L"
            "Double" -> "0.0"
            "Float" -> "0.0f"
            "Boolean" -> "false"
            "String" -> "\"\""
            else -> {
                if (type.startsWith("List<")) "emptyList()"
                else "$type()" // Attempt default constructor
            }
        }
    }

    /**
     * Formats an actual value as a Kotlin literal for "force init with origin value".
     */
    private fun formatDefaultForType(type: String, value: Any): String {
        return when (type) {
            "Int" -> (value as? Int)?.toString() ?: "0"
            "Long" -> "${(value as? Long) ?: 0}L"
            "Double" -> (value as? Double)?.toString() ?: "0.0"
            "Float" -> "${(value as? Float) ?: 0.0}f"
            "Boolean" -> (value as? Boolean)?.toString() ?: "false"
            "String" -> "\"${value}\""
            else -> "\"\""
        }
    }

    /**
     * Checks if a type is a Kotlin primitive type.
     */
    private fun isPrimitiveType(type: String): Boolean {
        return type in listOf("Int", "Long", "Double", "Float", "Boolean")
    }

    /**
     * Builds inner classes recursively from nested object nodes.
     */
    private fun buildInnerClasses(nodes: List<ToonNode>): String {
        val innerParts = mutableListOf<String>()

        nodes.forEach { node ->
            when (node) {
                is ToonNode.ObjectNode -> {
                    val innerClassName = applyClassNaming(node.key.replaceFirstChar { it.uppercase() })

                    // Temporarily redirect generation to capture inner class output
                    val savedFiles = files.toMap()
                    files.clear()
                    generateClassFile(innerClassName, node.children)
                    val innerContent = files["$innerClassName.kt"] ?: ""
                    files.clear()
                    files.putAll(savedFiles)

                    // Strip imports and add indentation for inner class
                    val classBody = innerContent.lines()
                        .dropWhile { it.startsWith("import") || it.isBlank() }
                        .joinToString("\n") { "$indent$it" }

                    innerParts += "\n$classBody"
                }

                is ToonNode.ObjectListNode -> {
                    val singular = applyClassNaming(singularize(node.key).replaceFirstChar { it.uppercase() })
                    val savedFiles = files.toMap()
                    files.clear()
                    generateObjectListFile(singular, node)
                    val innerContent = files["$singular.kt"] ?: ""
                    files.clear()
                    files.putAll(savedFiles)

                    val classBody = innerContent.lines()
                        .dropWhile { it.startsWith("import") || it.isBlank() }
                        .joinToString("\n") { "$indent$it" }

                    innerParts += "\n$classBody"
                }

                else -> { /* No inner class needed */ }
            }
        }

        return innerParts.joinToString("\n")
    }

    /**
     * Builds the complete class text with imports, class annotations, and body.
     */
    private fun buildClassText(
        className: String,
        imports: List<String>,
        body: String,
        innerClasses: String
    ): String {
        return buildString {
            // Imports
            if (imports.isNotEmpty()) {
                imports.forEach { append("import $it\n") }
                append("\n")
            }

            // Comment with original TOON (if enabled)
            if (settings.enableComment) {
                append("/**\n")
                append(" * Auto-generated Kotlin class from TOON format.\n")
                append(" */\n")
            }

            // Class-level annotations
            if (settings.enableKeepAnnotation || settings.enableKeepAnnotationAndroidX) {
                append("@Keep\n")
            }
            if (settings.enableParcelableSupport) {
                append("@Parcelize\n")
            }

            val classAnnotation = AnnotationGenerator.classAnnotation(framework)
            if (classAnnotation.isNotEmpty()) {
                append("$classAnnotation\n")
            }

            // Visibility modifier
            val visibility = if (settings.letClassesBeInternal) "internal " else ""

            // Class keyword
            val classKeyword = if (settings.disableKotlinDataClass) "class" else "data class"

            // Parent class / interface
            val parentParts = mutableListOf<String>()
            if (settings.parentClassTemplate.isNotEmpty()) {
                parentParts += settings.parentClassTemplate
            }
            if (settings.enableParcelableSupport) {
                parentParts += "Parcelable"
            }
            val inheritance = if (parentParts.isNotEmpty()) " : ${parentParts.joinToString(", ")}" else ""

            if (settings.replaceConstructorWithMemberVars) {
                // Member variable style (non-constructor)
                append("${visibility}${classKeyword} $className$inheritance {\n\n")
                append(body)
                if (innerClasses.isNotEmpty()) {
                    append("\n$innerClasses")
                }
                append("\n}")
            } else {
                // Constructor parameter style (default)
                append("${visibility}${classKeyword} $className(\n")
                append(body)
                append("\n)$inheritance")

                if (innerClasses.isNotEmpty()) {
                    append(" {\n$innerClasses\n}")
                }
            }
        }
    }

    /**
     * Simple singularization for list item class names.
     */
    private fun singularize(word: String): String {
        return when {
            word.endsWith("ies") -> word.dropLast(3) + "y"
            word.endsWith("s") -> word.dropLast(1)
            else -> word
        }
    }
}