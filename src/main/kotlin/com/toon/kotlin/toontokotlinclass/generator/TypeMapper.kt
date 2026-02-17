package com.toon.kotlin.toontokotlinclass.generator

object TypeMapper {

    fun mapPrimitive(value: Any): String {
        return when (value) {
            is Int -> "Int"
            is Long -> "Long"
            is Double -> "Double"
            is Float -> "Float"
            is Boolean -> "Boolean"
            else -> "String"
        }
    }

    fun mapList(): String = "List<String>"

    fun mapObjectList(className: String): String = "List<$className>"
}