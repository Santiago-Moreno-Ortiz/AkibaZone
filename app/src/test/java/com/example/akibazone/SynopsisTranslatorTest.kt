package com.example.akibazone

import com.example.akibazone.data.mapper.SynopsisTranslationEngine
import com.example.akibazone.data.mapper.SynopsisTranslator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SynopsisTranslatorTest {

    @Test
    fun translatesNormalizedEnglishSynopsisToSpanish() = runBlocking {
        var receivedText: String? = null
        val translator = SynopsisTranslator(
            SynopsisTranslationEngine { text ->
                receivedText = text
                "Una historia sobre cazarrecompensas espaciales."
            }
        )

        val result = translator.translateToSpanish("<p>A story about <b>space bounty hunters</b>.</p>")

        assertEquals("A story about space bounty hunters.", receivedText)
        assertEquals("Una historia sobre cazarrecompensas espaciales.", result)
    }

    @Test
    fun skipsTranslationWhenSynopsisIsMissing() = runBlocking {
        var calls = 0
        val translator = SynopsisTranslator(
            SynopsisTranslationEngine {
                calls++
                "No debería ejecutarse"
            }
        )

        assertNull(translator.translateToSpanish("  "))
        assertEquals(0, calls)
    }

    @Test
    fun reusesCachedTranslation() = runBlocking {
        var calls = 0
        val translator = SynopsisTranslator(
            SynopsisTranslationEngine {
                calls++
                "Sinopsis traducida."
            }
        )

        assertEquals("Sinopsis traducida.", translator.translateToSpanish("English synopsis."))
        assertEquals("Sinopsis traducida.", translator.translateToSpanish("English synopsis."))
        assertEquals(1, calls)
    }
}
