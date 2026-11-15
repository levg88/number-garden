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
import com.numbergarden.app.domain.MathPhase
import com.numbergarden.app.domain.CountingState
import com.numbergarden.app.model.AppSettings
import com.numbergarden.app.model.Progress
import com.numbergarden.app.ui.components.AppleField
import com.numbergarden.app.ui.components.FingerCount
import com.numbergarden.app.ui.components.ContentErrorScreen
import com.numbergarden.app.ui.components.GameMotionTheme
import com.numbergarden.app.ui.components.GameSceneHeader
import com.numbergarden.app.ui.components.SessionCompletionScreen
import com.numbergarden.app.ui.components.FlowerArt
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.NumberButton
import com.numbergarden.app.ui.components.rememberMotionAllowed
import com.numbergarden.app.util.numberWord

@Composable
fun MathScreen(
    container: AppContainer,
    onHome: () -> Unit,
) {
    val viewModel: MathViewModel = viewModel(
        factory = MathViewModel.factory(container),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val settings by container.settingsRepository.settings.collectAsStateWithLifecycle()
    val progress by container.progressRepository.progress.collectAsStateWithLifecycle()

    when (val current = state) {
        MathViewModel.State.Loading -> LoadingScreen(onHome)
        is MathViewModel.State.Failed -> ContentErrorScreen(
            message = current.message,
            onRetry = viewModel::retryContent,
            onHome = onHome,
        )

        is MathViewModel.State.Ready -> MathReadyScreen(
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
private fun MathReadyScreen(
    ui: MathViewModel.Ui,
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
        if (game.phase == MathPhase.CORRECT && motionAllowed) {
            correctScale.snapTo(1f)
            correctScale.animateTo(1.08f, animationSpec = tween(180))
            correctScale.animateTo(1f, animationSpec = tween(180))
        } else {
            correctScale.snapTo(1f)
        }
    }
    if (game.completed) {
        SessionCompletionScreen(
            title = stringResource(R.string.math_title),
            gardenFull = progress.blooms >= Progress.MAX_BLOOMS,
            theme = GameMotionTheme.MATH,
            reduceMotion = settings.reduceMotion,
            onPlayAgain = onRestart,
            onHome = onHome,
        )
        return
    }

    val speech = LocalSpeech.current
    val prompt = stringResource(R.string.math_prompt)
    val hint = stringResource(R.string.math_hint)
    val words = numberWord(game.round.answer)
    val correct = stringResource(R.string.math_correct, words)
    val thisIs = stringResource(R.string.math_this_is, words)
    val tapHint = stringResource(R.string.math_hint)


    LaunchedEffect(ui.sessionId, game.index) {
        speech.say(prompt)
    }
    LaunchedEffect(ui.sessionId, game.index, game.phase, game.misses) {
        when (game.phase) {
            MathPhase.CORRECT -> {
                if (settings.sound) SoundEffects.correct()
                speech.say(correct)
            }
            MathPhase.TEACH -> speech.say(thisIs)
            MathPhase.ASK -> if (game.misses == 1) speech.say(hint)
        }
    }


    GardenScaffold(
        title = stringResource(R.string.math_title),
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

            GameSceneHeader(
                theme = GameMotionTheme.MATH,
                reduceMotion = settings.reduceMotion,
                celebrating = game.phase != MathPhase.ASK,
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

            val round = game.round
            val opSymbol = if (round.operation == 0) stringResource(R.string.math_plus) else stringResource(R.string.math_minus)
            Text(
                text = "${round.left} $opSymbol ${round.right} = ?",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FingerCount(count = round.left, modifier = Modifier.weight(1f))
                Text(text = opSymbol, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                FingerCount(count = round.right, modifier = Modifier.weight(1f))
            }
            if (game.hintLevel >= 1) {
                FingerCount(count = round.answer, modifier = Modifier.fillMaxWidth())
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
                        enabled = game.phase == MathPhase.ASK,
                        emphasized = game.selected == option && game.phase != MathPhase.ASK,
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer(
                                scaleX = if (game.selected == option && game.phase == MathPhase.CORRECT) correctScale.value else 1f,
                                scaleY = if (game.selected == option && game.phase == MathPhase.CORRECT) correctScale.value else 1f,
                            ),
                    )
                }
            }

            FeedbackMessage(
                text = when {
                    game.phase == MathPhase.CORRECT -> correct
                    game.phase == MathPhase.TEACH -> thisIs
                    game.misses == 1 -> hint
                    else -> ""
                },
                positive = game.phase == MathPhase.CORRECT,
            )

            if (game.phase != MathPhase.ASK) {
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

