package com.toon.kotlin.toontokotlinclass.parser

object ToonIndent {

    fun getIndentLevel(line: String): Int {
        val spaces = line.takeWhile { it == ' ' }.length
        return spaces / 2   // 2 spaces = 1 indent level
    }

    fun trimIndent(line: String): String = line.trim()
}