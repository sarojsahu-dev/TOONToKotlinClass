package com.toon.kotlin.toontokotlinclass.parser

/**
 * Base exception for TOON parsing errors
 */
sealed class ToonException(message: String) : Exception(message)

/**
 * Thrown when TOON syntax is invalid
 */
class InvalidToonSyntaxException(message: String, val line: Int? = null) : ToonException(
    if (line != null) "Invalid TOON syntax at line $line: $message" else "Invalid TOON syntax: $message"
)

/**
 * Thrown when indentation is inconsistent or invalid
 */
class InvalidIndentationException(message: String, val line: Int? = null) : ToonException(
    if (line != null) "Invalid indentation at line $line: $message" else "Invalid indentation: $message"
)

/**
 * Thrown when duplicate keys are found at the same level
 */
class DuplicateKeyException(val key: String, val line: Int? = null) : ToonException(
    if (line != null) "Duplicate key '$key' at line $line" else "Duplicate key '$key'"
)

/**
 * Thrown when object list schema doesn't match data rows
 */
class SchemaMismatchException(message: String, val line: Int? = null) : ToonException(
    if (line != null) "Schema mismatch at line $line: $message" else "Schema mismatch: $message"
)

/**
 * Thrown when input is empty or blank
 */
class EmptyInputException : ToonException("TOON input cannot be empty")
