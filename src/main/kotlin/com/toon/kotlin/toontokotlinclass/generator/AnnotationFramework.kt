package com.toon.kotlin.toontokotlinclass.generator

enum class AnnotationFramework(val displayName: String) {

    Gson("Gson"),
    Moshi("Moshi"),
    Kotlinx("Kotlinx Serialization"),
    Firebase("Firebase");

    override fun toString(): String = displayName

    companion object {
        fun fromDisplayName(name: String): AnnotationFramework {
            return entries.firstOrNull { it.displayName == name } ?: Gson
        }
    }
}
