package com.kev.lydia.ui.list.components

/** Characters of [text] matching [query] (case-insensitive, first occurrence), or null. */
fun matchRange(text: String, query: String): IntRange? {
    if (query.isEmpty()) return null
    val start = text.indexOf(query, ignoreCase = true)
    return if (start < 0) null else start until start + query.length
}
