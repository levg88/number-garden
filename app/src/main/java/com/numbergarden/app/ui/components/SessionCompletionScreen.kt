package com.numbergarden.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.numbergarden.app.R
import com.numbergarden.app.audio.LocalSpeech

@Composable
fun SessionCompletionScreen(
    title: String,
    gardenFull: Boolean,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
    theme: GameMotionTheme = GameMotionTheme.GARDEN,
    reduceMotion: Boolean = false,
) {
    val speech = LocalSpeech.current
    val done = stringResource(R.string.count_session_done)
    val grew = stringResource(R.string.count_grew_flower)
    val full = stringResource(R.string.garden_full)
    val motionAllowed = rememberMotionAllowed(false)
    val flowerScale = remember { Animatable(if (motionAllowed) 0.6f else 1f) }
    val textAlpha = remember { Animatable(if (motionAllowed) 0f else 1f) }
    LaunchedEffect(motionAllowed) {
        if (motionAllowed) {
            flowerScale.animateTo(1f, animationSpec = tween(700))
            textAlpha.animateTo(1f, animationSpec = tween(500))
        } else {
            flowerScale.snapTo(1f)
            textAlpha.snapTo(1f)
        }
    }

    LaunchedEffect(Unit) {
        speech.say(done)
    }

    GardenScaffold(title = title, onHome = onHome) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
        ) {
            GameSceneHeader(
                theme = theme,
                reduceMotion = reduceMotion,
                celebrating = true,
            )
            FlowerArt(
                bloomed = true,
                size = 132.dp,
                petalColor = com.numbergarden.app.ui.theme.FlowerRose,
                modifier = Modifier.graphicsLayer(scaleX = flowerScale.value, scaleY = flowerScale.value),
            )
            Text(
                text = done,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer(alpha = textAlpha.value)
                    .semantics { heading() },
            )
            Text(
                text = if (gardenFull) full else grew,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer(alpha = textAlpha.value),
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
