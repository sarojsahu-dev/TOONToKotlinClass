package com.toon.kotlin.toontokotlinclass.parser

object TypeInference {

    fun inferValue(raw: String): Any {
        return when {
            raw == "true" -> true
            raw == "false" -> false
            raw.matches(Regex("^-?\\d+$")) -> raw.toInt()
            raw.matches(Regex("^-?\\d+\\.\\d+$")) -> raw.toDouble()
            else -> raw                     // default = string
        }
    }
}