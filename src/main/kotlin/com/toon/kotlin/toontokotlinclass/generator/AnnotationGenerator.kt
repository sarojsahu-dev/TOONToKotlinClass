package com.toon.kotlin.toontokotlinclass.generator

/**
 * Generates serialization annotations and their required imports
 * based on the selected [AnnotationFramework].
 *
 * Each framework produces framework-specific annotations like
 * `@SerializedName`, `@Json`, `@JsonProperty`, etc.
 */
object AnnotationGenerator {

    /**
     * Generates the annotation string for a given key.
     *
     * @param framework The selected annotation framework
     * @param key The original TOON/JSON field name
     * @param customAnnotation Optional user-provided annotation template (used with [AnnotationFramework.Custom])
     * @return The annotation string, or empty string if framework is None
     */
    fun generate(framework: AnnotationFramework, key: String, customAnnotation: String = ""): String {
        return when (framework) {
            AnnotationFramework.None,
            AnnotationFramework.NoneCamelCase ->
                ""

            AnnotationFramework.Gson ->
                """@SerializedName("$key")"""

            AnnotationFramework.Jackson ->
                """@JsonProperty("$key")"""

            AnnotationFramework.Fastjson ->
                """@JSONField(name = "$key")"""

            AnnotationFramework.MoshiReflect,
            AnnotationFramework.MoshiCodegen ->
                """@Json(name = "$key")"""

            AnnotationFramework.LoganSquare ->
                """@JsonField(name = arrayOf("$key"))"""

            AnnotationFramework.Kotlinx ->
                """@SerialName("$key")"""

            AnnotationFramework.Firebase ->
                """@PropertyName("$key")"""

            AnnotationFramework.Custom -> {
                if (customAnnotation.isNotBlank()) {
                    customAnnotation.replace("%s", key)
                } else {
                    """@SerializedName("$key")"""
                }
            }
        }
    }

    /**
     * Returns the list of import statements required for the given framework.
     *
     * @param framework The selected annotation framework
     * @return List of fully-qualified import paths
     */
    fun imports(framework: AnnotationFramework): List<String> {
        return when (framework) {
            AnnotationFramework.None,
            AnnotationFramework.NoneCamelCase ->
                emptyList()

            AnnotationFramework.Gson ->
                listOf("com.google.gson.annotations.SerializedName")

            AnnotationFramework.Jackson ->
                listOf("com.fasterxml.jackson.annotation.JsonProperty")

            AnnotationFramework.Fastjson ->
                listOf("com.alibaba.fastjson.annotation.JSONField")

            AnnotationFramework.MoshiReflect,
            AnnotationFramework.MoshiCodegen ->
                listOf("com.squareup.moshi.Json")

            AnnotationFramework.LoganSquare ->
                listOf("com.bluelinelabs.logansquare.annotation.JsonField")

            AnnotationFramework.Kotlinx ->
                listOf("kotlinx.serialization.SerialName", "kotlinx.serialization.Serializable")

            AnnotationFramework.Firebase ->
                listOf("com.google.firebase.firestore.PropertyName")

            AnnotationFramework.Custom ->
                emptyList() // User manages their own imports
        }
    }

    /**
     * Returns additional class-level annotations for frameworks that need them.
     * For example, kotlinx.serialization requires @Serializable on the class.
     *
     * @param framework The selected annotation framework
     * @return The class-level annotation string, or empty string if none needed
     */
    fun classAnnotation(framework: AnnotationFramework): String {
        return when (framework) {
            AnnotationFramework.Kotlinx -> "@Serializable"
            AnnotationFramework.LoganSquare -> "@JsonObject"
            else -> ""
        }
    }

    /**
     * Returns additional class-level imports for frameworks that need them.
     *
     * @param framework The selected annotation framework
     * @return List of additional import paths for class-level annotations
     */
    fun classImports(framework: AnnotationFramework): List<String> {
        return when (framework) {
            AnnotationFramework.LoganSquare ->
                listOf("com.bluelinelabs.logansquare.annotation.JsonObject")
            else -> emptyList()
        }
    }
}