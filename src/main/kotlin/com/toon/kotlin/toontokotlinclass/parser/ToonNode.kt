package com.toon.kotlin.toontokotlinclass.parser

sealed class ToonNode {

    data class Property(
        val key: String,
        val value: Any?             // String, Boolean, Number, List, Object
    ) : ToonNode()

    data class ObjectNode(
        val key: String,
        val children: MutableList<ToonNode> = mutableListOf()
    ) : ToonNode()

    data class ListNode(
        val key: String,
        val items: List<String>
    ) : ToonNode()

    data class ObjectListNode(
        val key: String,
        val headers: List<String>,
        val rows: List<List<String>>
    ) : ToonNode()
}