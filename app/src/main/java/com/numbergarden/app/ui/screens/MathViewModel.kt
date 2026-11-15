package com.numbergarden.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.numbergarden.app.data.ContentRepository
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.domain.MathSession
import com.numbergarden.app.domain.MathState
import com.numbergarden.app.model.ProgressRepository
import com.numbergarden.app.model.SessionKind
import com.numbergarden.app.model.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class MathViewModel(
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    data class Ui(val game: MathState, val sessionId: Int)

    sealed interface State {
        data object Loading : State
        data class Ready(val ui: Ui) : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private var sessionCounter = 0
    private var recorded = false

    init { startSession() }

    fun choose(value: Int) {
        val current = _state.value as? State.Ready ?: return
        val next = MathSession.select(current.ui.game, value)
        if (next != current.ui.game) _state.value = current.copy(ui = current.ui.copy(game = next))
    }

    fun next() {
        val current = _state.value as? State.Ready ?: return
        val next = MathSession.next(current.ui.game)
        if (next.completed && !recorded) {
            recorded = true
            progressRepository.recordSession(SessionKind.MATH)
        }
        _state.value = current.copy(ui = current.ui.copy(game = next))
    }

    fun restart() = startSession()
    fun retryContent() = startSession()

    private fun startSession() {
        _state.value = State.Loading
        val result = runCatching { contentRepository.load() }
        result.onSuccess { pack ->
            val total = minOf(settingsRepository.settings.value.roundsPerSession, pack.maxSessionRounds)
            val rounds = MathSession.buildRounds(total, Random.Default)
            sessionCounter += 1
            recorded = false
            _state.value = State.Ready(Ui(MathState(rounds = rounds), sessionCounter))
        }.onFailure { error ->
            _state.value = State.Failed(error.message ?: "Content unavailable")
        }
    }

    companion object {
        fun factory(container: com.numbergarden.app.di.AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MathViewModel(
                    contentRepository = container.contentRepository,
                    progressRepository = container.progressRepository,
                    settingsRepository = container.settingsRepository,
                )
            }
        }
    }
}