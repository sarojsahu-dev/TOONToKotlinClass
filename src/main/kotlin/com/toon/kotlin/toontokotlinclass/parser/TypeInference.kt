package com.toon.kotlin.toontokotlinclass.parser

object TypeInference {

    fun inferValue(raw: String): Any {
        val trimmed = raw.trim()
        
        // Handle empty/null values
        if (trimmed.isEmpty() || trimmed.equals("null", ignoreCase = true)) {
            return ""  // Default to empty string for null/empty
        }

        // boolean
        if (trimmed.equals("true", ignoreCase = true)) return true
        if (trimmed.equals("false", ignoreCase = true)) return false

        // Long (ends with L or l)
        if (trimmed.matches(Regex("^-?\\d+[Ll]$"))) {
            return trimmed.dropLast(1).toLongOrNull() ?: trimmed
        }
        
        // Float (ends with F or f)
        if (trimmed.matches(Regex("^-?\\d+\\.\\d+[Ff]$"))) {
            return trimmed.dropLast(1).toFloatOrNull() ?: trimmed
        }

        // int
        if (trimmed.matches(Regex("^-?\\d+$"))) {
            return trimmed.toIntOrNull() ?: trimmed
        }

        // double (decimal number)
        if (trimmed.matches(Regex("^-?\\d+\\.\\d+$"))) {
            return trimmed.toDoubleOrNull() ?: trimmed
        }

        // otherwise -> string
        return trimmed
    }
}