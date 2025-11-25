package com.toon.kotlin.toontokotlinclass.parser

object ToonIndent {
    fun getIndentLevel(line: String): Int {
        return line.takeWhile { it == ' ' }.length / 2   // 2 spaces = 1 level
    }

    fun trimIndent(line: String): String {
        return line.trim()
    }
}