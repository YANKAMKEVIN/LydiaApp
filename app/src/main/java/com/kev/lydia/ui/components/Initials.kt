package com.kev.lydia.ui.components

// Titles RandomUser puts in front of names ("Mr John Doe", "Mademoiselle Léa Dubois", ...).
private val NameTitles = setOf(
    "mr", "mrs", "ms", "miss", "mx", "dr", "monsieur", "madame", "mademoiselle", "mlle", "mme"
)

/** Up to two initials for [fullName] (first and last name), ignoring a leading title. */
fun initialsOf(fullName: String): String {
    val words = fullName.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    val names = words.filterNot { it.trimEnd('.').lowercase() in NameTitles }.ifEmpty { words }
    return when (names.size) {
        0 -> ""
        1 -> names.first().take(1).uppercase()
        else -> (names.first().take(1) + names.last().take(1)).uppercase()
    }
}
