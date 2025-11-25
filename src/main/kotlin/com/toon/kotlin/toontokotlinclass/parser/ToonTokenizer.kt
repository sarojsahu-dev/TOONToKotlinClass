package com.toon.kotlin.toontokotlinclass.parser

class ToonTokenizer {

    fun tokenize(input: String): List<Pair<Int, String>> {
        val result = mutableListOf<Pair<Int, String>>()

        input.lines().forEach { rawLine ->
            if (rawLine.isBlank()) return@forEach

            val indent = ToonIndent.getIndentLevel(rawLine)
            val cleaned = ToonIndent.trimIndent(rawLine)

            result += indent to cleaned
        }

        return result
    }
}