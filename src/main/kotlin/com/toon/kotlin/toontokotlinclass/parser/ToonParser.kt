package com.toon.kotlin.toontokotlinclass.parser

class ToonParser {

    fun parse(input: String): List<ToonNode> {
        val tokenizer = ToonTokenizer()
        val tokens = tokenizer.tokenize(input)

        val root = mutableListOf<ToonNode>()
        val stack = ArrayDeque<MutableList<ToonNode>>()
        stack.add(root)

        var lastIndent = 0

        for ((indent, line) in tokens) {
            val current = stack.last()

            // Move up levels if needed
            while (indent < lastIndent) {
                stack.removeLast()
                lastIndent--
            }

            // Detect nested object key:
            if (line.endsWith(":")) {
                val key = line.removeSuffix(":")
                val obj = ToonNode.ObjectNode(key)
                current.add(obj)
                stack.add(obj.children)
                lastIndent = indent + 1
                continue
            }

            // Detect list of objects: key[3]{id,name,...}:
            if (line.matches(Regex("^.+\\[\\d+]\\{.+}:.+"))) {
                val (key, headers) = parseObjectListHeader(line)
                val rows = parseObjectListRows(tokens, indent)
                current.add(ToonNode.ObjectListNode(key, headers, rows))
                continue
            }

            // Detect simple list: key[3]: a,b,c
            if (line.contains("[") && line.contains("]:")) {
                val (key, items) = parseSimpleList(line)
                current.add(ToonNode.ListNode(key, items))
                continue
            }

            // Default = property
            val (key, value) = parseProperty(line)
            current.add(ToonNode.Property(key, value))

            lastIndent = indent
        }

        return root
    }

    private fun parseProperty(line: String): Pair<String, Any> {
        val parts = line.split(":", limit = 2)
        val key = parts[0].trim()
        val valueRaw = parts.getOrNull(1)?.trim().orEmpty()
        val value = TypeInference.inferValue(valueRaw)
        return key to value
    }

    private fun parseSimpleList(line: String): Pair<String, List<String>> {
        val key = line.substringBefore("[").trim()
        val itemsRaw = line.substringAfter("]:").trim()
        val items = itemsRaw.split(",").map { it.trim() }
        return key to items
    }

    private fun parseObjectListHeader(line: String): Pair<String, List<String>> {
        val key = line.substringBefore("[").trim()
        val headerPart = line.substringAfter("{").substringBefore("}")
        val headers = headerPart.split(",").map { it.trim() }
        return key to headers
    }

    private fun parseObjectListRows(
        tokens: List<Pair<Int, String>>,
        baseIndent: Int
    ): List<List<String>> {
        val rows = mutableListOf<List<String>>()

        for ((indent, line) in tokens) {
            if (indent <= baseIndent) continue
            if (line.contains(",")) {
                rows += line.split(",").map { it.trim() }
            }
        }

        return rows
    }
}