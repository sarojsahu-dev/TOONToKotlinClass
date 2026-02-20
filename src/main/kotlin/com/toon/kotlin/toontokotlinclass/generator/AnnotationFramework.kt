package com.toon.kotlin.toontokotlinclass.generator

/**
 * Supported annotation frameworks for serialization/deserialization.
 *
 * Each entry maps to a specific library's annotation style used
 * in generated Kotlin data classes.
 */
enum class AnnotationFramework(val displayName: String) {

    None("None"),
    NoneCamelCase("None (Camel Case)"),
    Gson("Gson"),
    Jackson("Jackson"),
    Fastjson("Fastjson"),
    MoshiReflect("MoShi (Reflect)"),
    MoshiCodegen("MoShi (Codegen)"),
    LoganSquare("LoganSquare"),
    Kotlinx("kotlinx.serialization"),
    Custom("Others by customize"),
    Firebase("Firebase");

    override fun toString(): String = displayName
}
