package com.toon.kotlin.toontokotlinclass.parser

class ToonTokenizer {

    fun tokenize(input: String): List<Pair<Int, String>> {
        val result = mutableListOf<Pair<Int, String>>()

        input.lines().forEach { raw ->
            if (raw.isBlank()) return@forEach

            val indent = ToonIndent.getIndentLevel(raw)
            val cleaned = ToonIndent.trimIndent(raw)

            result += indent to cleaned
        }

        return result
    }
}