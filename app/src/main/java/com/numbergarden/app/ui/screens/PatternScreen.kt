package com.numbergarden.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.numbergarden.app.R
import com.numbergarden.app.audio.LocalSpeech
import com.numbergarden.app.audio.SoundEffects
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.domain.PatternPhase
import com.numbergarden.app.domain.PatternState
import com.numbergarden.app.model.AppSettings
import com.numbergarden.app.model.Progress
import com.numbergarden.app.ui.components.ContentErrorScreen
import com.numbergarden.app.ui.components.FlowerArt
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.rememberMotionAllowed
import com.numbergarden.app.ui.components.SessionCompletionScreen

@Composable
fun PatternScreen(
    container: AppContainer,
    onHome: () -> Unit,
) {
    val viewModel: PatternViewModel = viewModel(
        factory = PatternViewModel.factory(container),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val progress by container.progressRepository.progress.collectAsStateWithLifecycle()
    val settings by container.settingsRepository.settings.collectAsStateWithLifecycle()

    when (val current = state) {
        PatternViewModel.State.Loading -> LoadingScreen(onHome)
        is PatternViewModel.State.Failed -> ContentErrorScreen(
            message = current.message,
            onRetry = viewModel::retryContent,
            onHome = onHome,
        )
        is PatternViewModel.State.Ready -> PatternReadyScreen(
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
private fun PatternReadyScreen(
    ui: PatternViewModel.Ui,
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
        if (game.phase == PatternPhase.CORRECT && motionAllowed) {
            correctScale.snapTo(1f)
            correctScale.animateTo(1.08f, animationSpec = tween(180))
            correctScale.animateTo(1f, animationSpec = tween(180))
        } else {
            correctScale.snapTo(1f)
        }
    }
    if (game.completed) {
        SessionCompletionScreen(
            title = stringResource(R.string.pattern_title),
            gardenFull = progress.blooms >= Progress.MAX_BLOOMS,
            onPlayAgain = onRestart,
            onHome = onHome,
        )
        return
    }

    val speech = LocalSpeech.current
    val prompt = stringResource(R.string.pattern_prompt)
    val hint = stringResource(R.string.pattern_hint)
    val correct = stringResource(R.string.pattern_correct)
    val teach = stringResource(
        R.string.pattern_this_is,
        if (game.round.correct == 0) stringResource(R.string.pattern_apple) else stringResource(R.string.pattern_flower),
    )
    LaunchedEffect(ui.sessionId, game.index) {
        speech.say(prompt)
    }
    LaunchedEffect(ui.sessionId, game.index, game.phase, game.misses) {
        when (game.phase) {
            PatternPhase.CORRECT -> {
                if (settings.sound) SoundEffects.correct()
                speech.say(correct)
            }
            PatternPhase.TEACH -> speech.say(teach)
            PatternPhase.ASK -> if (game.misses == 1) speech.say(hint)
        }
    }

    GardenScaffold(
        title = stringResource(R.string.pattern_title),
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
                game.round.sequence.forEach { value ->
                    ShapeBadge(value = value)
                }
                Text(
                    text = "?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            if (game.hintLevel == 0) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                game.round.options.forEach { option ->
                    ShapeChoiceButton(
                        value = option,
                        onClick = { onChoose(option) },
                        enabled = game.phase == PatternPhase.ASK,
                        emphasized = game.selected == option && game.phase != PatternPhase.ASK,
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer(
                                scaleX = if (game.selected == option && game.phase == PatternPhase.CORRECT) correctScale.value else 1f,
                                scaleY = if (game.selected == option && game.phase == PatternPhase.CORRECT) correctScale.value else 1f,
                            ),
                    )
                }
            }

            FeedbackMessage(
                text = when {
                    game.phase == PatternPhase.CORRECT -> correct
                    game.phase == PatternPhase.TEACH -> teach
                    game.misses == 1 -> hint
                    else -> ""
                },
                positive = game.phase == PatternPhase.CORRECT,
            )

            if (game.phase != PatternPhase.ASK) {
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

@Composable
private fun ShapeBadge(value: Int) {
    val description = if (value == 0) stringResource(R.string.cd_apple_shape) else stringResource(R.string.cd_flower_shape)
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape,
            )
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        if (value == 0) {
            com.numbergarden.app.ui.components.AppleGlyph(size = 34.dp)
        } else {
            com.numbergarden.app.ui.components.FlowerArt(
                bloomed = true,
                size = 34.dp,
                petalColor = com.numbergarden.app.ui.theme.FlowerRose,
            )
        }
    }
}

@Composable
fun ShapeChoiceButton(
    value: Int,
    onClick: () -> Unit,
    enabled: Boolean,
    emphasized: Boolean,
    modifier: Modifier = Modifier,
) {
    val highContrast = com.numbergarden.app.ui.theme.LocalHighContrast.current
    androidx.compose.material3.Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 104.dp),
        shape = MaterialTheme.shapes.large,
        color = if (emphasized) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (emphasized || highContrast) 3.dp else 1.dp,
            color = when {
                emphasized -> MaterialTheme.colorScheme.primary
                highContrast -> MaterialTheme.colorScheme.outline
                else -> MaterialTheme.colorScheme.outlineVariant
            },
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (value == 0) {
                com.numbergarden.app.ui.components.AppleGlyph(size = 44.dp)
            } else {
                com.numbergarden.app.ui.components.FlowerArt(
                    bloomed = true,
                    size = 44.dp,
                    petalColor = com.numbergarden.app.ui.theme.FlowerRose,
                )
            }
        }
    }
}