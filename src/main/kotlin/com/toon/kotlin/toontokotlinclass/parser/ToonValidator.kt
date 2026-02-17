package com.toon.kotlin.toontokotlinclass.parser

/**
 * Validates TOON input before parsing
 */
object ToonValidator {

    /**
     * Validates TOON input and throws appropriate exceptions if invalid
     * @throws EmptyInputException if input is empty or blank
     * @throws InvalidIndentationException if indentation is inconsistent
     * @throws InvalidToonSyntaxException if syntax is malformed
     */
    fun validate(input: String) {
        // Check for empty input
        if (input.isBlank()) {
            throw EmptyInputException()
        }

        val lines = input.lines().filter { it.isNotBlank() }
        
        // Validate indentation consistency
        validateIndentation(lines)
        
        // Validate basic syntax
        validateSyntax(lines)
        
        // Validate balanced brackets
        validateBalancedBrackets(input)
    }

    private fun validateIndentation(lines: List<String>) {
        lines.forEachIndexed { index, line ->
            val leadingSpaces = line.takeWhile { it == ' ' }.length
            
            // Check if indentation is even (must be multiples of 2)
            if (leadingSpaces % 2 != 0) {
                throw InvalidIndentationException(
                    "Indentation must be in multiples of 2 spaces (found $leadingSpaces spaces)",
                    index + 1
                )
            }
            
            // Check for tabs
            if (line.contains('\t')) {
                throw InvalidIndentationException(
                    "Tabs are not allowed, use spaces for indentation",
                    index + 1
                )
            }
        }
    }

    private fun validateSyntax(lines: List<String>) {
        lines.forEachIndexed { index, line ->
            val trimmed = line.trim()
            
            // Skip empty lines
            if (trimmed.isEmpty()) return@forEachIndexed
            
            // Check for valid line patterns
            val isObjectDeclaration = trimmed.endsWith(":") && !trimmed.contains("[")
            val isProperty = trimmed.contains(":") && !trimmed.endsWith(":")
            val isSimpleList = trimmed.matches(Regex("^.+\\[\\d+]:\\s*.+"))
            val isObjectList = trimmed.matches(Regex(".+\\[\\d+].*\\{.+}.*:"))
            
            if (!isObjectDeclaration && !isProperty && !isSimpleList && !isObjectList) {
                // Check if it's a data row (comma-separated values)
                val isDataRow = trimmed.contains(",") && !trimmed.contains(":")
                
                if (!isDataRow) {
                    throw InvalidToonSyntaxException(
                        "Line does not match any valid TOON pattern: '$trimmed'",
                        index + 1
                    )
                }
            }
        }
    }

    private fun validateBalancedBrackets(input: String) {
        var squareBrackets = 0
        var curlyBraces = 0
        
        input.forEachIndexed { _, char ->
            when (char) {
                '[' -> squareBrackets++
                ']' -> squareBrackets--
                '{' -> curlyBraces++
                '}' -> curlyBraces--
            }
            
            if (squareBrackets < 0) {
                throw InvalidToonSyntaxException("Unmatched closing bracket ']'")
            }
            if (curlyBraces < 0) {
                throw InvalidToonSyntaxException("Unmatched closing brace '}'")
            }
        }
        
        if (squareBrackets != 0) {
            throw InvalidToonSyntaxException("Unmatched square brackets")
        }
        if (curlyBraces != 0) {
            throw InvalidToonSyntaxException("Unmatched curly braces")
        }
    }

    /**
     * Validates that a key is not empty or invalid
     */
    fun validateKey(key: String, line: Int? = null) {
        if (key.isBlank()) {
            throw InvalidToonSyntaxException("Key cannot be empty", line)
        }
        
        if (key.contains(" ") && !key.contains("_")) {
            throw InvalidToonSyntaxException(
                "Key '$key' contains spaces. Use underscores instead.",
                line
            )
        }
    }

    /**
     * Checks for duplicate keys at the same level
     */
    fun checkDuplicateKey(existingKeys: Set<String>, newKey: String, line: Int? = null) {
        if (existingKeys.contains(newKey)) {
            throw DuplicateKeyException(newKey, line)
        }
    }
}
