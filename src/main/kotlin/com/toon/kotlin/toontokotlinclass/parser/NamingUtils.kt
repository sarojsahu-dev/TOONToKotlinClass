package com.toon.kotlin.toontokotlinclass.parser

object NamingUtils {
    fun toCamelCase(input: String): String {
        return input.split("_").mapIndexed { index, part ->
            if (index == 0) part.lowercase()
            else part.replaceFirstChar { it.uppercase() }
        }.joinToString("")
    }
}