package com.numbergarden.app.data

import android.content.Context
import android.content.SharedPreferences
import com.numbergarden.app.model.Progress
import com.numbergarden.app.model.ProgressRepository
import com.numbergarden.app.model.SessionKind
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PrefsProgressRepository(context: Context) : ProgressRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _progress = MutableStateFlow(load())
    override val progress: StateFlow<Progress> = _progress.asStateFlow()

    private fun load(): Progress = Progress(
        completedLessons = prefs.getStringSet(KEY_LESSONS, emptySet())?.toSet().orEmpty(),
        countingSessions = prefs.getInt(KEY_COUNTING, 0).coerceAtLeast(0),
        compareSessions = prefs.getInt(KEY_COMPARE, 0).coerceAtLeast(0),
        matchSessions = prefs.getInt(KEY_MATCH, 0).coerceAtLeast(0),
        tapNumberSessions = prefs.getInt(KEY_TAP, 0).coerceAtLeast(0),
        patternSessions = prefs.getInt(KEY_PATTERN, 0).coerceAtLeast(0),
        additionSessions = prefs.getInt(KEY_ADDITION, 0).coerceAtLeast(0),
        shapesSessions = prefs.getInt(KEY_SHAPES, 0).coerceAtLeast(0),
        missingSessions = prefs.getInt(KEY_MISSING, 0).coerceAtLeast(0),
        mathSessions = prefs.getInt(KEY_MATH, 0).coerceAtLeast(0),
        orderSessions = prefs.getInt(KEY_ORDER, 0).coerceAtLeast(0),
        bondSessions = prefs.getInt(KEY_BONDS, 0).coerceAtLeast(0),
        positionSessions = prefs.getInt(KEY_POSITIONS, 0).coerceAtLeast(0),
    )

    override fun markLessonWatched(lessonId: String) {
        if (lessonId.isBlank()) return
        _progress.update { current ->
            if (lessonId in current.completedLessons) {
                current
            } else {
                current.copy(completedLessons = current.completedLessons + lessonId)
            }
        }
        prefs.edit()
            .putStringSet(KEY_LESSONS, _progress.value.completedLessons)
            .apply()
    }

    override fun recordSession(kind: SessionKind) {
        _progress.update { current ->
            when (kind) {
                SessionKind.COUNTING -> current.copy(countingSessions = current.countingSessions + 1)
                SessionKind.COMPARE -> current.copy(compareSessions = current.compareSessions + 1)
                SessionKind.MATCH -> current.copy(matchSessions = current.matchSessions + 1)
                SessionKind.TAP_NUMBER -> current.copy(tapNumberSessions = current.tapNumberSessions + 1)
                SessionKind.PATTERN -> current.copy(patternSessions = current.patternSessions + 1)
                SessionKind.ADDITION -> current.copy(additionSessions = current.additionSessions + 1)
                SessionKind.SHAPES -> current.copy(shapesSessions = current.shapesSessions + 1)
                SessionKind.MISSING -> current.copy(missingSessions = current.missingSessions + 1)
                SessionKind.MATH -> current.copy(mathSessions = current.mathSessions + 1)
                SessionKind.ORDER -> current.copy(orderSessions = current.orderSessions + 1)
                SessionKind.BONDS -> current.copy(bondSessions = current.bondSessions + 1)
                SessionKind.POSITIONS -> current.copy(positionSessions = current.positionSessions + 1)
            }
        }
        val value = _progress.value
        prefs.edit()
            .putInt(KEY_COUNTING, value.countingSessions)
            .putInt(KEY_COMPARE, value.compareSessions)
            .putInt(KEY_MATCH, value.matchSessions)
            .putInt(KEY_TAP, value.tapNumberSessions)
            .putInt(KEY_PATTERN, value.patternSessions)
            .putInt(KEY_ADDITION, value.additionSessions)
            .putInt(KEY_SHAPES, value.shapesSessions)
            .putInt(KEY_MISSING, value.missingSessions)
            .putInt(KEY_MATH, value.mathSessions)
            .putInt(KEY_ORDER, value.orderSessions)
            .putInt(KEY_BONDS, value.bondSessions)
            .putInt(KEY_POSITIONS, value.positionSessions)
            .apply()
    }

    override fun reset() {
        _progress.value = Progress()
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_NAME = "number_garden_progress"
        const val KEY_LESSONS = "completed_lessons"
        const val KEY_COUNTING = "counting_sessions"
        const val KEY_COMPARE = "compare_sessions"
        const val KEY_MATCH = "match_sessions"
        const val KEY_TAP = "tap_number_sessions"
        const val KEY_PATTERN = "pattern_sessions"
        const val KEY_ADDITION = "addition_sessions"
        const val KEY_SHAPES = "shapes_sessions"
        const val KEY_MISSING = "missing_sessions"
        const val KEY_MATH = "math_sessions"
        const val KEY_ORDER = "order_sessions"
        const val KEY_BONDS = "bonds_sessions"
        const val KEY_POSITIONS = "positions_sessions"
    }
}
