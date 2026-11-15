package com.numbergarden.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.numbergarden.app.R
import com.numbergarden.app.audio.LocalSpeech
import com.numbergarden.app.audio.SoundEffects
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.domain.BondPhase
import com.numbergarden.app.model.AppSettings
import com.numbergarden.app.model.Progress
import com.numbergarden.app.ui.components.ContentErrorScreen
import com.numbergarden.app.ui.components.GameMotionTheme
import com.numbergarden.app.ui.components.GameSceneHeader
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.NumberButton
import com.numbergarden.app.ui.components.SessionCompletionScreen
import com.numbergarden.app.ui.theme.FlowerAmber
import com.numbergarden.app.ui.theme.FlowerCenter
import com.numbergarden.app.ui.theme.FlowerLeaf

@Composable
fun BondScreen(
    container: AppContainer,
    onHome: () -> Unit,
) {
    val viewModel: BondViewModel = viewModel(factory = BondViewModel.factory(container))
    val state by viewModel.state.collectAsStateWithLifecycle()
    val settings by container.settingsRepository.settings.collectAsStateWithLifecycle()
    val progress by container.progressRepository.progress.collectAsStateWithLifecycle()

    when (val current = state) {
        BondViewModel.State.Loading -> LoadingScreen(onHome)
        is BondViewModel.State.Failed -> ContentErrorScreen(
            message = current.message,
            onRetry = viewModel::retryContent,
            onHome = onHome,
        )
        is BondViewModel.State.Ready -> BondReadyScreen(
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
private fun BondReadyScreen(
    ui: BondViewModel.Ui,
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
            title = stringResource(R.string.bonds_title),
            gardenFull = progress.blooms >= Progress.MAX_BLOOMS,
            theme = GameMotionTheme.BONDS,
            reduceMotion = settings.reduceMotion,
            onPlayAgain = onRestart,
            onHome = onHome,
        )
        return
    }

    val speech = LocalSpeech.current
    val round = game.round
    val prompt = stringResource(R.string.bonds_prompt, round.target)
    val hint = stringResource(R.string.bonds_hint)
    val sentence = stringResource(
        if (game.phase == BondPhase.CORRECT) R.string.bonds_correct else R.string.bonds_this_is,
        round.left,
        round.answer,
        round.target,
    )

    LaunchedEffect(ui.sessionId, game.index) {
        speech.say(prompt)
    }
    LaunchedEffect(game.phase, game.misses) {
        when (game.phase) {
            BondPhase.CORRECT -> {
                if (settings.sound) SoundEffects.correct()
                speech.say(sentence)
            }
            BondPhase.TEACH -> speech.say(sentence)
            BondPhase.ASK -> if (game.misses == 1) speech.say(hint)
        }
    }

    GardenScaffold(title = stringResource(R.string.bonds_title), onHome = onHome) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProgressRow(index = game.index, total = game.rounds.size)

            GameSceneHeader(
                theme = GameMotionTheme.BONDS,
                reduceMotion = settings.reduceMotion,
                celebrating = game.phase != BondPhase.ASK,
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

            BondDiagram(
                target = round.target,
                left = round.left,
                right = game.selected,
                revealed = game.phase != BondPhase.ASK,
            )

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
                round.options.forEach { option ->
                    NumberButton(
                        value = option,
                        onClick = { onChoose(option) },
                        enabled = game.phase == BondPhase.ASK,
                        emphasized = game.selected == option && game.phase != BondPhase.ASK,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            FeedbackMessage(
                text = when {
                    game.phase == BondPhase.CORRECT -> sentence
                    game.phase == BondPhase.TEACH -> sentence
                    game.misses == 1 -> hint
                    else -> ""
                },
                positive = game.phase == BondPhase.CORRECT,
            )

            if (game.phase != BondPhase.ASK) {
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
private fun BondDiagram(
    target: Int,
    left: Int,
    right: Int?,
    revealed: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .semantics {
                contentDescription = "Number bond. $left and ${right ?: "unknown"} make $target."
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val top = Offset(size.width * 0.50f, size.height * 0.25f)
            val leftCenter = Offset(size.width * 0.27f, size.height * 0.72f)
            val rightCenter = Offset(size.width * 0.73f, size.height * 0.72f)
            val lineColor = FlowerLeaf.copy(alpha = 0.60f)
            drawLine(lineColor, top, leftCenter, strokeWidth = size.height * 0.018f, cap = StrokeCap.Round)
            drawLine(lineColor, top, rightCenter, strokeWidth = size.height * 0.018f, cap = StrokeCap.Round)
            listOf(top, leftCenter, rightCenter).forEach { center ->
                drawCircle(
                    color = FlowerAmber.copy(alpha = 0.16f),
                    radius = size.height * 0.19f,
                    center = center,
                )
                drawCircle(
                    color = FlowerAmber.copy(alpha = 0.55f),
                    radius = size.height * 0.19f,
                    center = center,
                    style = Stroke(width = size.height * 0.012f),
                )
            }
            drawCircle(
                color = FlowerCenter.copy(alpha = 0.75f),
                radius = size.height * 0.045f,
                center = top + Offset(0f, -size.height * 0.28f),
            )
        }
        Text(
            text = target.toString(),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp),
        )
        Text(
            text = left.toString(),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 44.dp, bottom = 22.dp),
        )
        Text(
            text = if (revealed && right != null) right.toString() else stringResource(R.string.bonds_missing),
            style = MaterialTheme.typography.displaySmall,
            color = if (revealed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 44.dp, bottom = 22.dp),
        )
    }
}
