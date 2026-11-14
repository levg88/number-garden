package com.numbergarden.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.numbergarden.app.R
import com.numbergarden.app.audio.LocalSpeech
import com.numbergarden.app.audio.SoundEffects
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.domain.CountPhase
import com.numbergarden.app.domain.CountingState
import com.numbergarden.app.model.AppSettings
import com.numbergarden.app.model.Progress
import com.numbergarden.app.ui.components.AppleField
import com.numbergarden.app.ui.components.ContentErrorScreen
import com.numbergarden.app.ui.components.SessionCompletionScreen
import com.numbergarden.app.ui.components.FlowerArt
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.NumberButton
import com.numbergarden.app.ui.components.rememberMotionAllowed
import com.numbergarden.app.util.numberWord

@Composable
fun MissingScreen(
    container: AppContainer,
    onHome: () -> Unit,
) {
    val viewModel: CountingViewModel = viewModel(
        factory = CountingViewModel.factory(container, com.numbergarden.app.model.SessionKind.MISSING),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val settings by container.settingsRepository.settings.collectAsStateWithLifecycle()
    val progress by container.progressRepository.progress.collectAsStateWithLifecycle()

    when (val current = state) {
        CountingViewModel.State.Loading -> LoadingScreen(onHome)
        is CountingViewModel.State.Failed -> ContentErrorScreen(
            message = current.message,
            onRetry = viewModel::retryContent,
            onHome = onHome,
        )

        is CountingViewModel.State.Ready -> MissingReadyScreen(
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
private fun MissingReadyScreen(
    ui: CountingViewModel.Ui,
    settings: AppSettings,
    progress: Progress,
    onChoose: (Int) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
) {
    val game = ui.game
    val motionAllowed = rememberMotionAllowed(settings.reduceMotion)
    val correctScale = remember(ui.sessionId, game.index) { Animatable(1f) }
    LaunchedEffect(game.phase) {
        if (game.phase == CountPhase.CORRECT && motionAllowed) {
            correctScale.snapTo(1f)
            correctScale.animateTo(1.08f, animationSpec = tween(180))
            correctScale.animateTo(1f, animationSpec = tween(180))
        } else {
            correctScale.snapTo(1f)
        }
    }
    if (game.completed) {
        SessionCompletionScreen(
            title = stringResource(R.string.missing_title),
            gardenFull = progress.blooms >= Progress.MAX_BLOOMS,
            onPlayAgain = onRestart,
            onHome = onHome,
        )
        return
    }

    val speech = LocalSpeech.current
    val prompt = stringResource(R.string.missing_prompt)
    val hint = stringResource(R.string.missing_hint)
    val words = numberWord(game.round.quantity)
    val correct = stringResource(R.string.missing_correct, words)
    val thisIs = stringResource(R.string.missing_this_is, words)
    val tapHint = stringResource(R.string.missing_hint)


    LaunchedEffect(ui.sessionId, game.index) {
        speech.say(prompt)
    }
    LaunchedEffect(ui.sessionId, game.index, game.phase, game.misses) {
        when (game.phase) {
            CountPhase.CORRECT -> {
                if (settings.sound) SoundEffects.correct()
                speech.say(correct)
            }
            CountPhase.TEACH -> speech.say(thisIs)
            CountPhase.ASK -> if (game.misses == 1) speech.say(hint)
        }
    }


    GardenScaffold(
        title = stringResource(R.string.missing_title),
        onHome = onHome,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProgressRow(
                index = game.index,
                total = game.rounds.size,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                (1..5).forEach { n ->
                    val text = if (n == game.round.quantity) "?" else n.toString()
                    Text(
                        text = text,
                        style = MaterialTheme.typography.headlineLarge,
                        color = if (n == game.round.quantity) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            if (game.hintLevel == 0) {
                Text(
                    text = tapHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                game.round.options.forEach { option ->
                    NumberButton(
                        value = option,
                        onClick = { onChoose(option) },
                        enabled = game.phase == CountPhase.ASK,
                        emphasized = game.selected == option && game.phase != CountPhase.ASK,
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer(
                                scaleX = if (game.selected == option && game.phase == CountPhase.CORRECT) correctScale.value else 1f,
                                scaleY = if (game.selected == option && game.phase == CountPhase.CORRECT) correctScale.value else 1f,
                            ),
                    )
                }
            }

            FeedbackMessage(
                text = when {
                    game.phase == CountPhase.CORRECT -> correct
                    game.phase == CountPhase.TEACH -> thisIs
                    game.misses == 1 -> hint
                    else -> ""
                },
                positive = game.phase == CountPhase.CORRECT,
            )

            if (game.phase != CountPhase.ASK) {
                val isLast = game.index == game.rounds.lastIndex
                GardenButton(
                    text = stringResource(
                        if (isLast) R.string.count_finish else R.string.count_next,
                    ),
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

