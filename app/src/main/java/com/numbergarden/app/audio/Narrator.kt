package com.numbergarden.app.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.staticCompositionLocalOf
import com.numbergarden.app.model.SettingsRepository
import java.util.Locale

interface Speech {
    fun say(text: String, add: Boolean = false)
    fun stop()
}

object NoopSpeech : Speech {
    override fun say(text: String, add: Boolean) = Unit
    override fun stop() = Unit
}

val LocalSpeech = staticCompositionLocalOf<Speech> { NoopSpeech }

/**
 * Thin wrapper around the platform text-to-speech engine.
 * Narration is optional, never required to play, and fails quietly.
 */
class Narrator(
    context: Context,
    private val settingsRepository: SettingsRepository,
) : Speech {

    private data class Pending(val text: String, val add: Boolean)

    private var engine: TextToSpeech? = null
    private var ready = false
    private var released = false
    private var pending: Pending? = null

    init {
        engine = TextToSpeech(context.applicationContext) { status ->
            if (released) return@TextToSpeech
            ready = status == TextToSpeech.SUCCESS
            if (ready) {
                val languageResult = engine?.setLanguage(Locale.US) ?: TextToSpeech.LANG_NOT_SUPPORTED
                if (languageResult == TextToSpeech.LANG_MISSING_DATA ||
                    languageResult == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    engine?.setLanguage(Locale.getDefault())
                }
                engine?.setSpeechRate(0.88f)
                engine?.setPitch(1.0f)
                engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) = Unit
                    override fun onDone(utteranceId: String?) = Unit

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) = Unit
                })
                pending?.let { speakNow(it.text, it.add) }
            }
            pending = null
        }
    }

    override fun say(text: String, add: Boolean) {
        if (text.isBlank()) return
        if (!settingsRepository.settings.value.voice) return
        if (!ready) {
            pending = Pending(text, add)
            return
        }
        speakNow(text, add)
    }

    private fun speakNow(text: String, add: Boolean) {
        val mode = if (add) TextToSpeech.QUEUE_ADD else TextToSpeech.QUEUE_FLUSH
        engine?.speak(text, mode, null, "ng-${text.hashCode()}")
    }

    override fun stop() {
        pending = null
        engine?.stop()
    }

    fun release() {
        released = true
        pending = null
        engine?.stop()
        engine?.shutdown()
        engine = null
        ready = false
    }
}
