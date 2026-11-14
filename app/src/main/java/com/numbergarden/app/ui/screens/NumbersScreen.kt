package com.numbergarden.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.numbergarden.app.R
import com.numbergarden.app.audio.LocalSpeech
import com.numbergarden.app.data.ContentLoadException
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.model.NumberCard
import com.numbergarden.app.ui.components.ContentErrorScreen
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.TenFrame

@Composable
fun NumbersScreen(
    container: AppContainer,
    onHome: () -> Unit,
) {
    var attempt by remember { mutableIntStateOf(0) }
    val result = remember(attempt) {
        runCatching { container.contentRepository.load().numberCards.sortedBy { it.value } }
    }

    result.onFailure { error ->
        ContentErrorScreen(
            message = (error as? ContentLoadException)?.message ?: error.message,
            onRetry = { attempt += 1 },
            onHome = onHome,
        )
        return
    }

    val cards = result.getOrThrow()
    if (cards.isEmpty()) {
        ContentErrorScreen(
            message = "No number cards",
            onRetry = { attempt += 1 },
            onHome = onHome,
        )
        return
    }

    var index by rememberSaveable { mutableIntStateOf(0) }
    val safeIndex = index.coerceIn(cards.indices)
    val card = cards[safeIndex]
    val speech = LocalSpeech.current
    val sayAgain = stringResource(R.string.numbers_say_again)
    val cardDescription = stringResource(R.string.numbers_card_cd, card.value, card.word)
    // Side-by-side navigation labels break mid-word ("Previou/s") at 200% font scale.
    val largeText = LocalDensity.current.fontScale >= 1.5f

    LaunchedEffect(card.value) {
        speech.say(card.narration)
    }

    GardenScaffold(
        title = stringResource(R.string.numbers_title),
        onHome = onHome,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.count_progress, safeIndex + 1, cards.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = card.value.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .semantics(mergeDescendants = true) {
                        contentDescription = cardDescription
                        heading()
                    },
            )
            Text(
                text = card.word.replaceFirstChar { it.uppercaseChar() },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            TenFrame(filled = card.value)

            CardSayButton(
                label = sayAgain,
                onClick = { speech.say(card.narration) },
            )

            if (largeText) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    GardenButton(
                        text = stringResource(R.string.numbers_previous),
                        onClick = { index = (safeIndex - 1).coerceAtLeast(0) },
                        enabled = safeIndex > 0,
                        primary = false,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    GardenButton(
                        text = stringResource(R.string.numbers_next),
                        onClick = { index = (safeIndex + 1).coerceAtMost(cards.lastIndex) },
                        enabled = safeIndex < cards.lastIndex,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    GardenButton(
                        text = stringResource(R.string.numbers_previous),
                        onClick = { index = (safeIndex - 1).coerceAtLeast(0) },
                        enabled = safeIndex > 0,
                        primary = false,
                        modifier = Modifier.weight(1f),
                    )
                    GardenButton(
                        text = stringResource(R.string.numbers_next),
                        onClick = { index = (safeIndex + 1).coerceAtMost(cards.lastIndex) },
                        enabled = safeIndex < cards.lastIndex,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Text(
                text = stringResource(R.string.numbers_tap),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CardSayButton(label: String, onClick: () -> Unit) {
    GardenButton(
        text = label,
        onClick = onClick,
        primary = false,
        modifier = Modifier.fillMaxWidth(),
    )
}
