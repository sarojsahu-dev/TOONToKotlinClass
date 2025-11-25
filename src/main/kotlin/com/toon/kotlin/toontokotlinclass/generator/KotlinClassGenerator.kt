package com.toon.kotlin.toontokotlinclass.generator

import com.toon.kotlin.toontokotlinclass.parser.NamingUtils
import com.toon.kotlin.toontokotlinclass.parser.ToonNode

class KotlinClassGenerator(
    private val useVal: Boolean,
    private val nullable: Boolean,
    private val framework: AnnotationFramework
) {

    private val nestedClasses = mutableListOf<String>()

    fun generateClass(rootName: String, nodes: List<ToonNode>): String {
        val mainClass = buildClass(rootName, nodes)
        val allClasses = nestedClasses + mainClass

        val imports = AnnotationGenerator.requiredImports(framework)
            .joinToString("\n") { "import $it" }

        return """
            $imports

            ${allClasses.joinToString("\n\n")}
        """.trimIndent()
    }

    private fun buildClass(name: String, nodes: List<ToonNode>): String {
        val className = name.replaceFirstChar { it.uppercase() }
        val props = mutableListOf<String>()

        for (node in nodes) {
            when (node) {

                is ToonNode.Property -> {
                    val originalKey = node.key
                    val propName = NamingUtils.toCamelCase(originalKey)
                    val annotation = AnnotationGenerator.generate(framework, originalKey)

                    val type = TypeMapper.mapPrimitive(node.value!!)
                    val nullableSuffix = if (nullable) "?" else ""
                    val v = if (useVal) "val" else "var"

                    props += """
                        $annotation
                        $v $propName: $type$nullableSuffix
                    """.trimIndent()
                }

                is ToonNode.ListNode -> {
                    val originalKey = node.key
                    val propName = NamingUtils.toCamelCase(originalKey)
                    val annotation = AnnotationGenerator.generate(framework, originalKey)

                    val type = TypeMapper.mapList(node.items)
                    val v = if (useVal) "val" else "var"

                    props += """
                        $annotation
                        $v $propName: $type
                    """.trimIndent()
                }

                is ToonNode.ObjectNode -> {
                    val nestedName = node.key.replaceFirstChar { it.uppercase() }
                    val nestedClass = buildClass(nestedName, node.children)

                    nestedClasses += nestedClass

                    val propName = NamingUtils.toCamelCase(node.key)
                    val annotation = AnnotationGenerator.generate(framework, node.key)
                    val v = if (useVal) "val" else "var"

                    props += """
                        $annotation
                        $v $propName: $nestedName
                    """.trimIndent()
                }

                is ToonNode.ObjectListNode -> {
                    val classNameObj = node.key.replaceFirstChar { it.uppercase() }
                    val nestedObj = buildObjectListClass(node)
                    nestedClasses += nestedObj

                    val propName = NamingUtils.toCamelCase(node.key)
                    val annotation = AnnotationGenerator.generate(framework, node.key)
                    val v = if (useVal) "val" else "var"

                    props += """
                        $annotation
                        $v $propName: List<$classNameObj>
                    """.trimIndent()
                }
            }
        }

        return """
            data class $className(
                ${props.joinToString(",\n\n    ")}
            )
        """.trimIndent()
    }

    private fun buildObjectListClass(node: ToonNode.ObjectListNode): String {

        val className = node.key.replaceFirstChar { it.uppercase() }

        val props = node.headers.map { header ->
            val propName = NamingUtils.toCamelCase(header)
            val annotation = AnnotationGenerator.generate(framework, header)
            val type = "String"
            val v = if (useVal) "val" else "var"

            """
                $annotation
                $v $propName: $type
            """.trimIndent()
        }

        return """
            data class $className(
                ${props.joinToString(",\n\n    ")}
            )
        """.trimIndent()
    }
}