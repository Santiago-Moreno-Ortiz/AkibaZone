package com.example.akibazone

import com.example.akibazone.data.mapper.SynopsisNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SynopsisNormalizerTest {
    @Test
    fun removesHtmlAndNormalizesWhitespace() {
        assertEquals(
            "Una historia <especial> con formato.",
            SynopsisNormalizer.normalize("<p>Una <i>historia</i> &lt;especial&gt;<br><b>con formato.</b></p>")
        )
    }

    @Test
    fun ignoresEmptyAndPlaceholderDescriptions() {
        assertNull(SynopsisNormalizer.firstAvailable(null, "  ", "No description available."))
        assertNull(SynopsisNormalizer.normalize("Sin descripción disponible"))
    }

    @Test
    fun keepsOriginalLanguageWhenNoSpanishValueExists() {
        assertEquals(
            "An English synopsis.",
            SynopsisNormalizer.firstAvailable("An English synopsis.")
        )
    }

    @Test
    fun prefersTheFirstUsableSourceValue() {
        assertEquals(
            "Sinopsis en español.",
            SynopsisNormalizer.firstAvailable("", "Sinopsis en español.", "English synopsis.")
        )
    }
}
