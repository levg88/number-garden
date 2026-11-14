package com.numbergarden.app.data

import android.content.Context
import android.content.SharedPreferences
import com.numbergarden.app.model.AppSettings
import com.numbergarden.app.model.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PrefsSettingsRepository(context: Context) : SettingsRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(load())
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun load(): AppSettings = AppSettings(
        voice = prefs.getBoolean(KEY_VOICE, true),
        sound = prefs.getBoolean(KEY_SOUND, true),
        reduceMotion = prefs.getBoolean(KEY_REDUCE_MOTION, false),
        highContrast = prefs.getBoolean(KEY_HIGH_CONTRAST, false),
        roundsPerSession = prefs.getInt(KEY_ROUNDS, 10).coerceIn(1, 100),
    )

    override fun setVoice(enabled: Boolean) {
        _settings.update { it.copy(voice = enabled) }
        prefs.edit().putBoolean(KEY_VOICE, enabled).apply()
    }

    override fun setSound(enabled: Boolean) {
        _settings.update { it.copy(sound = enabled) }
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
    }

    override fun setReduceMotion(enabled: Boolean) {
        _settings.update { it.copy(reduceMotion = enabled) }
        prefs.edit().putBoolean(KEY_REDUCE_MOTION, enabled).apply()
    }

    override fun setHighContrast(enabled: Boolean) {
        _settings.update { it.copy(highContrast = enabled) }
        prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply()
    }

    override fun setRoundsPerSession(rounds: Int) {
        val safe = rounds.coerceIn(1, 100)
        _settings.update { it.copy(roundsPerSession = safe) }
        prefs.edit().putInt(KEY_ROUNDS, safe).apply()
    }

    private companion object {
        const val PREFS_NAME = "number_garden_settings"
        const val KEY_VOICE = "voice"
        const val KEY_SOUND = "sound"
        const val KEY_REDUCE_MOTION = "reduce_motion"
        const val KEY_HIGH_CONTRAST = "high_contrast"
        const val KEY_ROUNDS = "rounds_per_session"
    }
}
