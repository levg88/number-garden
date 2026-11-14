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
            }
        }
        val value = _progress.value
        prefs.edit()
            .putInt(KEY_COUNTING, value.countingSessions)
            .putInt(KEY_COMPARE, value.compareSessions)
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
    }
}
