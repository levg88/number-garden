package com.numbergarden.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.numbergarden.app.data.ContentRepository
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.domain.PatternSession
import com.numbergarden.app.domain.PatternState
import com.numbergarden.app.model.ProgressRepository
import com.numbergarden.app.model.SessionKind
import com.numbergarden.app.model.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class PatternViewModel(
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    data class Ui(
        val game: PatternState,
        val sessionId: Int,
    )

    sealed interface State {
        data object Loading : State
        data class Ready(val ui: Ui) : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private var sessionCounter = 0
    private var recorded = false

    init {
        startSession()
    }

    fun choose(value: Int) {
        val current = _state.value as? State.Ready ?: return
        val nextGame = PatternSession.select(current.ui.game, value)
        if (nextGame != current.ui.game) {
            _state.value = current.copy(ui = current.ui.copy(game = nextGame))
        }
    }

    fun next() {
        val current = _state.value as? State.Ready ?: return
        val nextGame = PatternSession.next(current.ui.game)
        if (nextGame.completed && !recorded) {
            recorded = true
            progressRepository.recordSession(SessionKind.PATTERN)
        }
        _state.value = current.copy(ui = current.ui.copy(game = nextGame))
    }

    fun restart() {
        startSession()
    }

    fun retryContent() {
        startSession()
    }

    private fun startSession() {
        _state.value = State.Loading
        val result = runCatching { contentRepository.load() }
        result.onSuccess { pack ->
            val total = minOf(settingsRepository.settings.value.roundsPerSession, pack.maxSessionRounds)
            val rounds = PatternSession.buildRounds(
                count = total,
                random = Random.Default,
            )
            sessionCounter += 1
            recorded = false
            _state.value = State.Ready(
                Ui(
                    game = PatternState(rounds = rounds),
                    sessionId = sessionCounter,
                ),
            )
        }.onFailure { error ->
            _state.value = State.Failed(error.message ?: "Content unavailable")
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PatternViewModel(
                    contentRepository = container.contentRepository,
                    progressRepository = container.progressRepository,
                    settingsRepository = container.settingsRepository,
                )
            }
        }
    }
}