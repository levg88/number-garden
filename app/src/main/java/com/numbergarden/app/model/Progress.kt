package com.numbergarden.app.model

import kotlinx.coroutines.flow.StateFlow

data class AppSettings(
    val voice: Boolean = true,
    val sound: Boolean = true,
    val reduceMotion: Boolean = false,
    val highContrast: Boolean = false,
    val roundsPerSession: Int = 10,
    val voiceStyle: String = "friendly",
)

interface SettingsRepository {
    val settings: StateFlow<AppSettings>

    fun setVoice(enabled: Boolean)
    fun setSound(enabled: Boolean)
    fun setReduceMotion(enabled: Boolean)
    fun setHighContrast(enabled: Boolean)
    fun setRoundsPerSession(rounds: Int)
    fun setVoiceStyle(style: String)
}

enum class SessionKind {
    COUNTING,
    COMPARE,
    MATCH,
    TAP_NUMBER,
    PATTERN,
    ADDITION,
    SHAPES,
    MISSING,
}

data class Progress(
    val completedLessons: Set<String> = emptySet(),
    val countingSessions: Int = 0,
    val compareSessions: Int = 0,
    val matchSessions: Int = 0,
    val tapNumberSessions: Int = 0,
    val patternSessions: Int = 0,
    val additionSessions: Int = 0,
    val shapesSessions: Int = 0,
    val missingSessions: Int = 0,
) {
    val totalSessions: Int get() =
        countingSessions + compareSessions + matchSessions + tapNumberSessions + patternSessions +
            additionSessions + shapesSessions + missingSessions

    /** The garden is deliberately finite and never resets on its own. */
    val blooms: Int get() = totalSessions.coerceAtMost(MAX_BLOOMS)

    companion object {
        const val MAX_BLOOMS = 12
    }
}

interface ProgressRepository {
    val progress: StateFlow<Progress>

    fun markLessonWatched(lessonId: String)
    fun recordSession(kind: SessionKind)
    fun reset()
}
