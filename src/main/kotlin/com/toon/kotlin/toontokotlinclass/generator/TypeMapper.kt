package com.toon.kotlin.toontokotlinclass.generator

object TypeMapper {

    fun mapPrimitive(value: Any): String {
        return when (value) {
            is Int -> "Int"
            is Double -> "Double"
            is Boolean -> "Boolean"
            else -> "String"
        }
    }

    fun mapList(items: List<String>): String {
        return "List<String>"
    }

    fun mapObjectList(className: String): String {
        return "List<$className>"
    }
}