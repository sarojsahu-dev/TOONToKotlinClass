package com.toon.kotlin.toontokotlinclass.generator

object AnnotationGenerator {

    fun generate(framework: AnnotationFramework, originalKey: String): String {
        return when (framework) {
            AnnotationFramework.Gson ->
                """@SerializedName("$originalKey")"""

            AnnotationFramework.Moshi ->
                """@Json(name = "$originalKey")"""

            AnnotationFramework.Kotlinx ->
                """@SerialName("$originalKey")"""

            AnnotationFramework.Firebase ->
                """@PropertyName("$originalKey")"""
        }
    }

    fun requiredImports(framework: AnnotationFramework): List<String> {
        return when (framework) {
            AnnotationFramework.Gson -> listOf("com.google.gson.annotations.SerializedName")
            AnnotationFramework.Moshi -> listOf("com.squareup.moshi.Json")
            AnnotationFramework.Kotlinx -> listOf("kotlinx.serialization.SerialName")
            AnnotationFramework.Firebase -> listOf("com.google.firebase.firestore.PropertyName")
        }
    }
}