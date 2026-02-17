package com.toon.kotlin.toontokotlinclass.generator

object AnnotationGenerator {

    fun generate(framework: AnnotationFramework, key: String): String {
        return when (framework) {
            AnnotationFramework.Gson ->
                """@SerializedName("$key")"""

            AnnotationFramework.Moshi ->
                """@Json(name = "$key")"""

            AnnotationFramework.Kotlinx ->
                """@SerialName("$key")"""

            AnnotationFramework.Firebase ->
                """@PropertyName("$key")"""
        }
    }

    fun imports(framework: AnnotationFramework): List<String> {
        return when (framework) {
            AnnotationFramework.Gson ->
                listOf("com.google.gson.annotations.SerializedName")

            AnnotationFramework.Moshi ->
                listOf("com.squareup.moshi.Json")

            AnnotationFramework.Kotlinx ->
                listOf("kotlinx.serialization.SerialName")

            AnnotationFramework.Firebase ->
                listOf("com.google.firebase.firestore.PropertyName")
        }
    }
}