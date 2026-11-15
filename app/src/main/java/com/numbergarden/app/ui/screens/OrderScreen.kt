package com.numbergarden.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.numbergarden.app.R
import com.numbergarden.app.audio.LocalSpeech
import com.numbergarden.app.audio.SoundEffects
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.domain.OrderPhase
import com.numbergarden.app.model.AppSettings
import com.numbergarden.app.model.Progress
import com.numbergarden.app.ui.components.ContentErrorScreen
import com.numbergarden.app.ui.components.GameMotionTheme
import com.numbergarden.app.ui.components.GameSceneHeader
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.SessionCompletionScreen
import com.numbergarden.app.util.numberWord

@Composable
fun OrderScreen(
    container: AppContainer,
    onHome: () -> Unit,
) {
    val viewModel: OrderViewModel = viewModel(factory = OrderViewModel.factory(container))
    val state by viewModel.state.collectAsStateWithLifecycle()
    val settings by container.settingsRepository.settings.collectAsStateWithLifecycle()
    val progress by container.progressRepository.progress.collectAsStateWithLifecycle()

    when (val current = state) {
        OrderViewModel.State.Loading -> LoadingScreen(onHome)
        is OrderViewModel.State.Failed -> ContentErrorScreen(
            message = current.message,
            onRetry = viewModel::retryContent,
            onHome = onHome,
        )
        is OrderViewModel.State.Ready -> OrderReadyScreen(
            ui = current.ui,
            settings = settings,
            progress = progress,
            onChoose = viewModel::choose,
            onNext = viewModel::next,
            onRestart = viewModel::restart,
            onHome = onHome,
        )
    }
}

@Composable
private fun OrderReadyScreen(
    ui: OrderViewModel.Ui,
    settings: AppSettings,
    progress: Progress,
    onChoose: (Int) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
) {
    val game = ui.game
    if (game.completed) {
        SessionCompletionScreen(
            title = stringResource(R.string.order_title),
            gardenFull = progress.blooms >= Progress.MAX_BLOOMS,
            theme = GameMotionTheme.ORDER,
            reduceMotion = settings.reduceMotion,
            onPlayAgain = onRestart,
            onHome = onHome,
        )
        return
    }

    val speech = LocalSpeech.current
    val prompt = stringResource(R.string.order_prompt)
    val hint = stringResource(R.string.order_hint)
    val ordered = game.round.values.sorted()
    val thisIs = stringResource(R.string.order_this_is, ordered.joinToString(", "))
    val lastPlaced = game.placed.lastOrNull()
    val correct = stringResource(
        R.string.order_correct,
        lastPlaced?.let { numberWord(it) } ?: "",
    )

    LaunchedEffect(ui.sessionId, game.index) {
        speech.say(prompt)
    }
    LaunchedEffect(game.placed.size) {
        if (game.phase == OrderPhase.ASK && game.placed.isNotEmpty()) {
            if (settings.sound) SoundEffects.correct()
            speech.say(numberWord(game.placed.last()))
        }
    }
    LaunchedEffect(game.phase, game.misses) {
        when (game.phase) {
            OrderPhase.CORRECT -> {
                if (settings.sound) SoundEffects.correct()
                speech.say(correct)
            }
            OrderPhase.TEACH -> speech.say(thisIs)
            OrderPhase.ASK -> if (game.misses == 1) speech.say(hint)
        }
    }

    GardenScaffold(title = stringResource(R.string.order_title), onHome = onHome) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProgressRow(index = game.index, total = game.rounds.size)

            GameSceneHeader(
                theme = GameMotionTheme.ORDER,
                reduceMotion = settings.reduceMotion,
                celebrating = game.phase != OrderPhase.ASK,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() },
                )
                SpeakerButton(onClick = { speech.say(prompt) })
            }

            Text(
                text = stringResource(
                    R.string.order_step,
                    game.placed.size.coerceAtLeast(0),
                    game.round.values.size,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                game.round.values.forEach { value ->
                    val placed = value in game.placed
                    OrderTile(
                        value = value,
                        placed = placed,
                        enabled = !placed && game.phase == OrderPhase.ASK,
                        emphasized = placed || (game.phase == OrderPhase.TEACH && value == game.smallest),
                        onClick = { onChoose(value) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (game.hintLevel == 0) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            FeedbackMessage(
                text = when {
                    game.phase == OrderPhase.CORRECT -> correct
                    game.phase == OrderPhase.TEACH -> thisIs
                    game.misses == 1 -> hint
                    else -> ""
                },
                positive = game.phase == OrderPhase.CORRECT,
            )

            if (game.phase != OrderPhase.ASK) {
                GardenButton(
                    text = stringResource(
                        if (game.index == game.rounds.lastIndex) R.string.count_finish else R.string.count_next,
                    ),
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun OrderTile(
    value: Int,
    placed: Boolean,
    enabled: Boolean,
    emphasized: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .heightIn(min = 92.dp)
            .semantics {
                role = androidx.compose.ui.semantics.Role.Button
                contentDescription = if (placed) "$value. Placed." else "$value."
            },
        shape = MaterialTheme.shapes.large,
        color = if (emphasized) scheme.primaryContainer else scheme.surface,
        border = BorderStroke(
            width = if (emphasized) 3.dp else 1.dp,
            color = if (emphasized) scheme.primary else scheme.outlineVariant,
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = scheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}
