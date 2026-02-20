package com.toon.kotlin.toontokotlinclass.parser

sealed class ToonNode {

    /** Common key shared by all node types */
    abstract val key: String

    /** key: rawValue */
    data class Property(
        override val key: String,
        val value: Any
    ) : ToonNode()

    /** key: nested fields */
    data class ObjectNode(
        override val key: String,
        val children: MutableList<ToonNode> = mutableListOf()
    ) : ToonNode()

    /** key[]: a,b,c */
    data class ListNode(
        override val key: String,
        val items: List<String>
    ) : ToonNode()

    /** key[]{schema}: rows[] */
    data class ObjectListNode(
        override val key: String,
        val headers: List<String>,
        val rows: List<List<String>>
    ) : ToonNode()
}