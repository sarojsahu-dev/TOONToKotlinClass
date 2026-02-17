package com.toon.kotlin.toontokotlinclass.parser

sealed class ToonNode {

    /** key: rawValue */
    data class Property(
        val key: String,
        val value: Any
    ) : ToonNode()

    /** key: nested fields */
    data class ObjectNode(
        val key: String,
        val children: MutableList<ToonNode> = mutableListOf()
    ) : ToonNode()

    /** key[]: a,b,c */
    data class ListNode(
        val key: String,
        val items: List<String>
    ) : ToonNode()

    /** key[]{schema}: rows[] */
    data class ObjectListNode(
        val key: String,
        val headers: List<String>,
        val rows: List<List<String>>
    ) : ToonNode()
}