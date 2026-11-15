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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.numbergarden.app.R
import com.numbergarden.app.audio.LocalSpeech
import com.numbergarden.app.audio.SoundEffects
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.domain.PositionPhase
import com.numbergarden.app.domain.PositionSession
import com.numbergarden.app.model.AppSettings
import com.numbergarden.app.model.Progress
import com.numbergarden.app.ui.components.AppleGlyph
import com.numbergarden.app.ui.components.ContentErrorScreen
import com.numbergarden.app.ui.components.FlowerArt
import com.numbergarden.app.ui.components.GameMotionTheme
import com.numbergarden.app.ui.components.GameSceneHeader
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.LeafGlyph
import com.numbergarden.app.ui.components.SessionCompletionScreen
import com.numbergarden.app.ui.theme.FlowerAmber
import com.numbergarden.app.ui.theme.FlowerRose

@Composable
fun PositionScreen(
    container: AppContainer,
    onHome: () -> Unit,
) {
    val viewModel: PositionViewModel = viewModel(factory = PositionViewModel.factory(container))
    val state by viewModel.state.collectAsStateWithLifecycle()
    val settings by container.settingsRepository.settings.collectAsStateWithLifecycle()
    val progress by container.progressRepository.progress.collectAsStateWithLifecycle()

    when (val current = state) {
        PositionViewModel.State.Loading -> LoadingScreen(onHome)
        is PositionViewModel.State.Failed -> ContentErrorScreen(
            message = current.message,
            onRetry = viewModel::retryContent,
            onHome = onHome,
        )
        is PositionViewModel.State.Ready -> PositionReadyScreen(
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
private fun PositionReadyScreen(
    ui: PositionViewModel.Ui,
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
            title = stringResource(R.string.positions_title),
            gardenFull = progress.blooms >= Progress.MAX_BLOOMS,
            theme = GameMotionTheme.POSITIONS,
            reduceMotion = settings.reduceMotion,
            onPlayAgain = onRestart,
            onHome = onHome,
        )
        return
    }

    val speech = LocalSpeech.current
    val round = game.round
    val ordinal = stringResource(ordinalStringRes(round.target))
    val prompt = stringResource(R.string.positions_prompt, ordinal)
    val hint = stringResource(R.string.positions_hint)
    val correct = stringResource(R.string.positions_correct, ordinal)
    val thisIs = stringResource(R.string.positions_this_is, ordinal)

    LaunchedEffect(ui.sessionId, game.index) {
        speech.say(prompt)
    }
    LaunchedEffect(game.phase, game.misses) {
        when (game.phase) {
            PositionPhase.CORRECT -> {
                if (settings.sound) SoundEffects.correct()
                speech.say(correct)
            }
            PositionPhase.TEACH -> speech.say(thisIs)
            PositionPhase.ASK -> if (game.misses == 1) speech.say(hint)
        }
    }

    GardenScaffold(title = stringResource(R.string.positions_title), onHome = onHome) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProgressRow(index = game.index, total = game.rounds.size)

            GameSceneHeader(
                theme = GameMotionTheme.POSITIONS,
                reduceMotion = settings.reduceMotion,
                celebrating = game.phase != PositionPhase.ASK,
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
                round.items.forEachIndexed { position, item ->
                    PositionItemCard(
                        position = position,
                        item = item,
                        selected = game.selected == position,
                        revealCorrect = game.phase != PositionPhase.ASK,
                        enabled = game.phase == PositionPhase.ASK,
                        onClick = { onChoose(position) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            FeedbackMessage(
                text = when {
                    game.phase == PositionPhase.CORRECT -> correct
                    game.phase == PositionPhase.TEACH -> thisIs
                    game.misses == 1 -> hint
                    else -> ""
                },
                positive = game.phase == PositionPhase.CORRECT,
            )

            if (game.phase != PositionPhase.ASK) {
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

private fun ordinalStringRes(position: Int): Int = when (position) {
    0 -> R.string.position_first
    1 -> R.string.position_second
    else -> R.string.position_third
}

private fun ordinalCdStringRes(position: Int): Int = when (position) {
    0 -> R.string.cd_position_first
    1 -> R.string.cd_position_second
    else -> R.string.cd_position_third
}

private fun itemNameRes(item: Int): Int = when (item) {
    0 -> R.string.cd_apple_shape
    1 -> R.string.cd_flower_shape
    else -> R.string.cd_leaf_shape
}

@Composable
private fun PositionItemCard(
    position: Int,
    item: Int,
    selected: Boolean,
    revealCorrect: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val highContrast = com.numbergarden.app.ui.theme.LocalHighContrast.current
    val description = stringResource(
        R.string.cd_position_item,
        stringResource(ordinalCdStringRes(position)),
        stringResource(itemNameRes(item)),
    )
    val emphasized = selected && revealCorrect
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .heightIn(min = 128.dp)
            .semantics {
                role = androidx.compose.ui.semantics.Role.Button
                contentDescription = description
            },
        shape = MaterialTheme.shapes.large,
        color = if (emphasized) scheme.primaryContainer else scheme.surface,
        border = BorderStroke(
            width = if (emphasized || highContrast) 3.dp else 1.dp,
            color = when {
                emphasized -> scheme.primary
                highContrast -> scheme.outline
                else -> scheme.outlineVariant
            },
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (item) {
                0 -> AppleGlyph(size = 58.dp, modifier = Modifier.size(58.dp))
                1 -> FlowerArt(
                    bloomed = true,
                    size = 62.dp,
                    petalColor = FlowerRose,
                    modifier = Modifier.size(62.dp),
                )
                else -> LeafGlyph(size = 62.dp, modifier = Modifier.size(62.dp))
            }
        }
    }
}
