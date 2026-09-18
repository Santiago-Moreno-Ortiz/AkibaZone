package com.example.akibazone.data.mapper

import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

fun interface SynopsisTranslationEngine {
    suspend fun translateEnglishToSpanish(text: String): String
}

class SynopsisTranslator(
    private val engine: SynopsisTranslationEngine = MlKitSynopsisTranslationEngine()
) {
    private val cache = ConcurrentHashMap<String, String>()

    suspend fun translateToSpanish(value: String?): String? {
        val normalized = SynopsisNormalizer.normalize(value) ?: return null
        cache[normalized]?.let { return it }

        val translated = SynopsisNormalizer.normalize(
            engine.translateEnglishToSpanish(normalized)
        ) ?: return null

        cache[normalized] = translated
        return translated
    }
}

private class MlKitSynopsisTranslationEngine : SynopsisTranslationEngine {
    override suspend fun translateEnglishToSpanish(text: String): String {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()
        val translator = Translation.getClient(options)

        return try {
            translator.downloadModelIfNeeded(DownloadConditions.Builder().build()).awaitResult()
            translator.translate(text).awaitResult()
        } finally {
            translator.close()
        }
    }
}

private suspend fun <T> Task<T>.awaitResult(): T =
    suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            when {
                task.isCanceled -> continuation.cancel()
                task.exception != null -> continuation.resumeWithException(task.exception!!)
                else -> continuation.resume(task.result)
            }
        }
    }
