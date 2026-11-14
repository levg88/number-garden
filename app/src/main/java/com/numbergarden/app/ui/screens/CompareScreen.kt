package com.numbergarden.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.numbergarden.app.R
import com.numbergarden.app.audio.LocalSpeech
import com.numbergarden.app.audio.SoundEffects
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.domain.ComparePhase
import com.numbergarden.app.domain.GroupSide
import com.numbergarden.app.model.AppSettings
import com.numbergarden.app.model.CompareTarget
import com.numbergarden.app.model.Progress
import com.numbergarden.app.ui.components.AppleField
import com.numbergarden.app.ui.components.ContentErrorScreen
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.SessionCompletionScreen
import com.numbergarden.app.util.numberWord

@Composable
fun CompareScreen(
    container: AppContainer,
    onHome: () -> Unit,
) {
    val viewModel: CompareViewModel = viewModel(
        factory = CompareViewModel.factory(container),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val settings by container.settingsRepository.settings.collectAsStateWithLifecycle()
    val progress by container.progressRepository.progress.collectAsStateWithLifecycle()

    when (val current = state) {
        CompareViewModel.State.Loading -> LoadingScreen(onHome)
        is CompareViewModel.State.Failed -> ContentErrorScreen(
            message = current.message,
            onRetry = viewModel::retryContent,
            onHome = onHome,
        )

        is CompareViewModel.State.Ready -> CompareReadyScreen(
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
private fun CompareReadyScreen(
    ui: CompareViewModel.Ui,
    settings: AppSettings,
    progress: Progress,
    onChoose: (GroupSide) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
) {
    val game = ui.game
    if (game.completed) {
        SessionCompletionScreen(
            title = stringResource(R.string.compare_title),
            gardenFull = progress.blooms >= Progress.MAX_BLOOMS,
            onPlayAgain = onRestart,
            onHome = onHome,
        )
        return
    }

    val speech = LocalSpeech.current
    val target = game.round.target
    val prompt = stringResource(
        if (target == CompareTarget.MORE) {
            R.string.compare_prompt_more
        } else {
            R.string.compare_prompt_fewer
        },
    )
    val hint = stringResource(R.string.compare_hint)
    val tryAgain = stringResource(R.string.compare_try_again)
    val sentence = comparisonSentence(game.round.left, game.round.right, target, teach = false)
    val teachSentence = comparisonSentence(game.round.left, game.round.right, target, teach = true)

    LaunchedEffect(ui.sessionId, game.index) {
        speech.say(prompt)
    }
    LaunchedEffect(ui.sessionId, game.index, game.phase, game.misses) {
        when (game.phase) {
            ComparePhase.CORRECT -> {
                if (settings.sound) SoundEffects.correct()
                speech.say(sentence)
            }
            ComparePhase.TEACH -> speech.say(teachSentence)
            ComparePhase.ASK -> if (game.misses == 1) speech.say("$tryAgain $hint")
        }
    }

    GardenScaffold(
        title = stringResource(R.string.compare_title),
        onHome = onHome,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ProgressRow(index = game.index, total = game.rounds.size)

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

            CompareGroup(
                number = 1,
                count = game.round.left,
                highlighted = game.phase != ComparePhase.ASK &&
                    game.round.correctSide == GroupSide.LEFT,
                revealed = game.phase == ComparePhase.TEACH,
                showCount = game.hintLevel >= 1,
                onChoose = { onChoose(GroupSide.LEFT) },
                enabled = game.phase == ComparePhase.ASK,
            )
            CompareGroup(
                number = 2,
                count = game.round.right,
                highlighted = game.phase != ComparePhase.ASK &&
                    game.round.correctSide == GroupSide.RIGHT,
                revealed = game.phase == ComparePhase.TEACH,
                showCount = game.hintLevel >= 1,
                onChoose = { onChoose(GroupSide.RIGHT) },
                enabled = game.phase == ComparePhase.ASK,
            )

            FeedbackMessage(
                text = when {
                    game.phase == ComparePhase.CORRECT -> sentence
                    game.phase == ComparePhase.TEACH -> teachSentence
                    game.misses == 1 -> "$tryAgain $hint"
                    else -> ""
                },
                positive = game.phase == ComparePhase.CORRECT,
            )

            if (game.phase != ComparePhase.ASK) {
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
private fun CompareGroup(
    number: Int,
    count: Int,
    highlighted: Boolean,
    revealed: Boolean,
    showCount: Boolean,
    enabled: Boolean,
    onChoose: () -> Unit,
) {
    val label = pluralStringResource(R.plurals.compare_group_cd, count, number, count)
    val thisGroup = stringResource(R.string.compare_this_group)
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onChoose,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 132.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = if (revealed) "$label $thisGroup" else label
            },
        shape = MaterialTheme.shapes.large,
        color = if (highlighted) scheme.primaryContainer else scheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (highlighted) 3.dp else 1.dp,
            color = if (highlighted) scheme.primary else scheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (showCount) count.toString() else " ",
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onSurfaceVariant,
                )
                if (highlighted) {
                    Text(
                        text = thisGroup,
                        style = MaterialTheme.typography.labelMedium,
                        color = scheme.primary,
                        textAlign = TextAlign.End,
                    )
                }
            }
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {},
            ) {
                AppleField(
                    count = count,
                    appleSize = 42.dp,
                )
            }
        }
    }
}

/**
 * Pure word choice for a comparison sentence, so the visible text and the spoken
 * sentence cannot disagree about which quantity is more or fewer.
 */
internal fun comparisonWordPair(
    left: Int,
    right: Int,
    target: CompareTarget,
): Pair<String, String> {
    val greater = numberWord(maxOf(left, right), capitalized = true)
    val lesser = numberWord(minOf(left, right))
    val lesserCapitalized = numberWord(minOf(left, right), capitalized = true)
    val greaterLower = numberWord(maxOf(left, right))
    return when (target) {
        CompareTarget.MORE -> greater to lesser
        CompareTarget.FEWER -> lesserCapitalized to greaterLower
    }
}

@Composable
private fun comparisonSentence(
    left: Int,
    right: Int,
    target: CompareTarget,
    teach: Boolean,
): String {
    val (first, second) = comparisonWordPair(left, right, target)
    return when (target) {
        CompareTarget.MORE -> stringResource(
            if (teach) R.string.compare_teach_more else R.string.compare_correct_more,
            first,
            second,
        )

        CompareTarget.FEWER -> stringResource(
            if (teach) R.string.compare_teach_fewer else R.string.compare_correct_fewer,
            first,
            second,
        )
    }
}
