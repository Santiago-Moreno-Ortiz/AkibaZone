package com.example.akibazone.data.mapper

import org.jsoup.Jsoup

/**
 * Normalizes synopsis text before it reaches the translation layer or UI.
 */
object SynopsisNormalizer {
    private val placeholderPattern = Regex(
        "^(no description available|sin descripción disponible)[.!]?$",
        RegexOption.IGNORE_CASE
    )

    fun firstAvailable(vararg candidates: String?): String? =
        candidates.asSequence()
            .mapNotNull(::normalize)
            .firstOrNull()

    fun normalize(value: String?): String? {
        val cleaned = value
            ?.replace(Regex("(?i)<br\\s*/?>"), " ")
            ?.let(Jsoup::parse)
            ?.text()
            ?.replace(Regex("\\s+"), " ")
            ?.trim()

        return cleaned?.takeUnless { it.isBlank() || placeholderPattern.matches(it) }
    }
}
