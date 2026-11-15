package com.numbergarden.app.audio

import android.content.Context
import android.media.AudioAttributes
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

/** A character voice is a pitch/rate/pause recipe. It is intentionally tiny and data-only. */
private data class VoiceCharacter(
    val pitch: Float,
    val rate: Float,
    val pauseMs: Int,
)

private enum class Mood {
    NEUTRAL,
    WARM,
    QUESTION,
    CHEER,
    TEACH,
}

/**
 * The free, offline "voice cast". Android's platform TTS does the speaking; the
 * performance comes from casting a character, splitting lines into acted phrases, and
 * varying pitch, pace, and pauses the way a storyteller would. No voice files are
 * shipped, so the app stays tiny, offline, and caretaker-configurable.
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
    private var utteranceCounter = 0

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
                chooseBestVoice()
                runCatching {
                    engine?.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build(),
                    )
                }
                engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) = Unit
                    override fun onDone(utteranceId: String?) = Unit

                    @Suppress("OVERRIDE_DEPRECATION")
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
        val character = characterFor(settingsRepository.settings.value.voiceStyle)
        val phrases = phrases(text)
        if (phrases.isEmpty()) return
        var first = !add
        phrases.forEachIndexed { index, phrase ->
            val mood = moodFor(phrase, index, phrases.lastIndex)
            applyVoice(character, mood, index)
            val mode = if (first) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            if (!first && character.pauseMs > 0) {
                runCatching {
                    engine?.playSilentUtterance(
                        character.pauseMs.toLong(),
                        TextToSpeech.QUEUE_ADD,
                        "ng-pause-${utteranceCounter++}",
                    )
                }
            }
            engine?.speak(phrase, mode, null, "ng-${utteranceCounter++}-${phrase.hashCode()}")
            first = false
        }
    }

    private fun applyVoice(character: VoiceCharacter, mood: Mood, index: Int) {
        val (pitchShift, rateShift) = when (mood) {
            Mood.NEUTRAL -> 0f to 0f
            Mood.WARM -> 0.03f to -0.06f
            Mood.QUESTION -> 0.10f to -0.03f
            Mood.CHEER -> 0.16f to 0.06f
            Mood.TEACH -> -0.02f to -0.08f
        }
        // A tiny lift on the final phrase of a question makes the line feel spoken,
        // rather than read. It is deliberately subtle.
        val questionLift = if (mood == Mood.QUESTION && index > 0) 0.04f else 0f
        engine?.setPitch((character.pitch + pitchShift + questionLift).coerceIn(0.4f, 2.0f))
        engine?.setSpeechRate((character.rate + rateShift).coerceIn(0.4f, 2.2f))
    }

    private fun characterFor(style: String): VoiceCharacter = when (style) {
        "willow" -> VoiceCharacter(pitch = 1.02f, rate = 0.88f, pauseMs = 260)
        "bramble" -> VoiceCharacter(pitch = 0.84f, rate = 0.92f, pauseMs = 300)
        "zippy" -> VoiceCharacter(pitch = 1.34f, rate = 1.04f, pauseMs = 160)
        "sparkle" -> VoiceCharacter(pitch = 1.48f, rate = 1.00f, pauseMs = 180)
        // Backwards-compatible names from the first version of the app.
        "hero" -> VoiceCharacter(pitch = 0.88f, rate = 0.96f, pauseMs = 260)
        "robot" -> VoiceCharacter(pitch = 0.72f, rate = 1.00f, pauseMs = 160)
        else -> VoiceCharacter(pitch = 1.18f, rate = 0.96f, pauseMs = 210)
    }

    private fun moodFor(phrase: String, index: Int, lastIndex: Int): Mood {
        val lower = phrase.lowercase(Locale.US)
        return when {
            phrase.contains("?") -> Mood.QUESTION
            lower.startsWith("yes") || lower.contains("hooray") || lower.contains("you did it") ->
                Mood.CHEER
            lower.contains("this is") || lower.contains("it is") || lower.contains("make ") ->
                Mood.TEACH
            index == lastIndex && (lower.startsWith("let") || lower.contains("look") ||
                lower.contains("try") || lower.contains("count")) -> Mood.WARM
            else -> Mood.NEUTRAL
        }
    }

    private fun phrases(text: String): List<String> {
        val sentences = Regex("[^.!?]+[.!?]?").findAll(text)
            .map { it.value.trim() }
            .filter { it.isNotEmpty() }
            .toList()
        if (sentences.isEmpty()) return listOf(text.trim())
        return sentences.flatMap { sentence ->
            if (sentence.length <= 58) {
                listOf(sentence)
            } else {
                sentence.split(",", ";", ":", "—")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .map { part -> if (part.lastOrNull()?.isLetter() == true) "$part." else part }
            }
        }
    }

    private fun chooseBestVoice() {
        val tts = engine ?: return
        val current = tts.voice ?: return
        val voices = runCatching { tts.voices }.getOrNull() ?: return
        val candidates = voices.filter {
            it.locale.language.equals(current.locale.language, ignoreCase = true) &&
                it.locale.country.equals(current.locale.country, ignoreCase = true)
        }.ifEmpty {
            voices.filter { it.locale.language.equals(current.locale.language, ignoreCase = true) }
        }
        if (candidates.isEmpty()) return
        val best = candidates
            .filter { !it.isNetworkConnectionRequired }
            .maxByOrNull { it.quality }
            ?: candidates.maxByOrNull { it.quality }
        best?.let { runCatching { tts.voice = it } }
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
