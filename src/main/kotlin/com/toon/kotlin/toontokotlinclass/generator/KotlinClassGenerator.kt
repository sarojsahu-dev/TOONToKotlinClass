package com.toon.kotlin.toontokotlinclass.generator

import com.toon.kotlin.toontokotlinclass.parser.NamingUtils
import com.toon.kotlin.toontokotlinclass.parser.ToonNode
import com.toon.kotlin.toontokotlinclass.parser.TypeInference

/**
 * Generates Kotlin data class code from TOON AST nodes.
 * 
 * Supports multiple annotation frameworks (Gson, Moshi, Kotlinx Serialization, Firebase)
 * and configurable code style (val/var, nullable/non-nullable).
 * 
 * @property useVal If true, generates `val` properties; otherwise `var`
 * @property nullable If true, adds `?` to all property types
 * @property framework The annotation framework to use for serialization annotations
 * @property packageName Optional package name to add to generated files
 */
class KotlinClassGenerator(
    private val useVal: Boolean,
    private val nullable: Boolean,
    private val framework: AnnotationFramework,
    private val packageName: String = ""
) {

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
            val className = key.replaceFirstChar { it.uppercase() }
            generateClassFile(className, node.children)
        }

        return files
    }

    /**
     * Generates a Kotlin data class file from a list of ToonNode children
     * @param className The name of the class to generate
     * @param nodes The list of child nodes (properties, lists, nested objects)
     */
    private fun generateClassFile(className: String, nodes: List<ToonNode>) {

        val imports = AnnotationGenerator.imports(framework)
        val props = mutableListOf<String>()

        nodes.forEach { node ->

            when (node) {

                is ToonNode.Property -> {
                    val propName = NamingUtils.toCamelCase(node.key)
                    val type = TypeMapper.mapPrimitive(node.value)
                    val ann = AnnotationGenerator.generate(framework, node.key)
                    val v = if (useVal) "val" else "var"
                    val q = if (nullable) "?" else ""

                    props += buildString {
                        append("    $ann\n")
                        append("    $v $propName: $type$q")
                    }
                }

                is ToonNode.ListNode -> {
                    val propName = NamingUtils.toCamelCase(node.key)
                    val type = TypeMapper.mapList()
                    val ann = AnnotationGenerator.generate(framework, node.key)
                    val v = if (useVal) "val" else "var"

                    props += buildString {
                        append("    $ann\n")
                        append("    $v $propName: $type")
                    }
                }

                is ToonNode.ObjectNode -> {
                    val nestedClassName = node.key.replaceFirstChar { it.uppercase() }

                    // Generate nested file
                    generateClassFile(nestedClassName, node.children)

                    val propName = NamingUtils.toCamelCase(node.key)
                    val ann = AnnotationGenerator.generate(framework, node.key)
                    val v = if (useVal) "val" else "var"

                    props += buildString {
                        append("    $ann\n")
                        append("    $v $propName: $nestedClassName")
                    }
                }

                is ToonNode.ObjectListNode -> {
                    val singular = singularize(node.key).replaceFirstChar { it.uppercase() }

                    // Generate list object file
                    generateObjectListFile(singular, node)

                    val propName = NamingUtils.toCamelCase(node.key)
                    val ann = AnnotationGenerator.generate(framework, node.key)
                    val v = if (useVal) "val" else "var"
                    val type = "List<$singular>"

                    props += buildString {
                        append("    $ann\n")
                        append("    $v $propName: $type")
                    }
                }
            }
        }

        val body = props.joinToString(",\n\n")

        val fullText = buildString {
            if (imports.isNotEmpty()) {
                imports.forEach { append("import $it\n") }
                append("\n")
            }
            append("data class $className(\n")
            append(body)
            append("\n)")
        }

        files["$className.kt"] = fullText
    }

    /**
     * Generates a Kotlin data class for object list items
     * @param className The name of the class (singular form)
     * @param node The ObjectListNode containing headers and row data
     */
    private fun generateObjectListFile(className: String, node: ToonNode.ObjectListNode) {

        val imports = AnnotationGenerator.imports(framework)
        val props = mutableListOf<String>()
        val firstRow = node.rows.firstOrNull()

        node.headers.forEachIndexed { idx, header ->
            val sample = firstRow?.getOrNull(idx) ?: ""
            val inferred = TypeInference.inferValue(sample)
            val type = TypeMapper.mapPrimitive(inferred)
            val propName = NamingUtils.toCamelCase(header)
            val ann = AnnotationGenerator.generate(framework, header)
            val v = if (useVal) "val" else "var"
            val q = if (nullable) "?" else ""

            props += buildString {
                append("    $ann\n")
                append("    $v $propName: $type$q")
            }
        }

        val body = props.joinToString(",\n\n")

        val fullText = buildString {
            if (imports.isNotEmpty()) {
                imports.forEach { append("import $it\n") }
                append("\n")
            }
            append("data class $className(\n")
            append(body)
            append("\n)")
        }

        files["$className.kt"] = fullText
    }

    private fun singularize(word: String): String {
        return when {
            word.endsWith("ies") -> word.dropLast(3) + "y"
            word.endsWith("s") -> word.dropLast(1)
            else -> word
        }
    }
}