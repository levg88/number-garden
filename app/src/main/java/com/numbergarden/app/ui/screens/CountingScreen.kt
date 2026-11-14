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
import com.numbergarden.app.ui.components.FlowerArt
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.NumberButton
import com.numbergarden.app.util.numberWord

@Composable
fun CountingScreen(
    container: AppContainer,
    onHome: () -> Unit,
) {
    val viewModel: CountingViewModel = viewModel(
        factory = CountingViewModel.factory(container),
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

        is CountingViewModel.State.Ready -> CountingReadyScreen(
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
private fun CountingReadyScreen(
    ui: CountingViewModel.Ui,
    settings: AppSettings,
    progress: Progress,
    onChoose: (Int) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
) {
    val game = ui.game
    if (game.completed) {
        CompletionScreen(
            gardenFull = progress.blooms >= Progress.MAX_BLOOMS,
            onPlayAgain = onRestart,
            onHome = onHome,
        )
        return
    }

    val speech = LocalSpeech.current
    val prompt = stringResource(R.string.count_prompt)
    val hint = stringResource(R.string.count_hint)
    val words = numberWord(game.round.quantity)
    val correct = stringResource(R.string.count_correct, words)
    val thisIs = stringResource(R.string.count_this_is, words)
    val tapHint = stringResource(R.string.count_tap_hint)


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

    var countedOrder by remember(ui.sessionId, game.index) { mutableStateOf<List<Int>>(emptyList()) }

    GardenScaffold(
        title = stringResource(R.string.count_title),
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

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val spacing = 8.dp
                val fit = (maxWidth - spacing * 4) / 5f
                val appleSize = minOf(64.dp, fit).coerceAtLeast(44.dp)
                AppleField(
                    count = game.round.quantity,
                    appleSize = appleSize,
                    countedOrdinals = countedOrder,
                    showSequenceHints = game.hintLevel >= 1,
                    onAppleClick = { index ->
                        if (index !in countedOrder) {
                            countedOrder = countedOrder + index
                            speech.say(numberWord(countedOrder.size), add = true)
                        }
                    },
                )
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
                        modifier = Modifier.weight(1f),
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

@Composable
private fun CompletionScreen(
    gardenFull: Boolean,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val speech = LocalSpeech.current
    val done = stringResource(R.string.count_session_done)
    val grew = stringResource(R.string.count_grew_flower)
    val full = stringResource(R.string.garden_full)

    LaunchedEffect(Unit) {
        speech.say(done)
    }

    GardenScaffold(
        title = stringResource(R.string.count_title),
        onHome = onHome,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
        ) {
            FlowerArt(bloomed = true, size = 132.dp)
            Text(
                text = done,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = if (gardenFull) full else grew,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            GardenButton(
                text = stringResource(R.string.count_play_again),
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(),
            )
            GardenButton(
                text = stringResource(R.string.count_back_home),
                onClick = onHome,
                primary = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun ProgressRow(index: Int, total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.count_progress, index + 1, total),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .clearAndSetSemantics {},
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(total) { dot ->
                Box(
                    modifier = Modifier
                        .size(if (dot == index) 12.dp else 9.dp)
                        .background(
                            color = when {
                                dot == index -> MaterialTheme.colorScheme.primary
                                dot < index -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.outlineVariant
                            },
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

@Composable
fun FeedbackMessage(text: String, positive: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (text.isNotBlank()) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (positive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.semantics {
                    liveRegion = LiveRegionMode.Polite
                },
            )
        }
    }
}

@Composable
fun SpeakerButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(60.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_speaker),
            contentDescription = stringResource(R.string.cd_speaker),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
fun LoadingScreen(onHome: () -> Unit) {
    GardenScaffold(
        title = stringResource(R.string.app_name),
        onHome = onHome,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
