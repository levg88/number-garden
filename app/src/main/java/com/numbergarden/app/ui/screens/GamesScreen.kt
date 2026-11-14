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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.numbergarden.app.R
import com.numbergarden.app.ui.components.CountArt
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.NumbersArt
import com.numbergarden.app.ui.theme.LocalHighContrast

@Composable
fun GamesScreen(
    onMatch: () -> Unit,
    onTapNumber: () -> Unit,
    onPattern: () -> Unit,
    onAddition: () -> Unit,
    onShapes: () -> Unit,
    onMissing: () -> Unit,
    onMath: () -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit,
) {
    GardenScaffold(title = stringResource(R.string.games_title), onBack = onBack, onHome = onHome) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.games_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            GameCard(
                title = stringResource(R.string.match_title),
                subtitle = stringResource(R.string.match_hint),
                onClick = onMatch,
            ) { CountArt() }
            GameCard(
                title = stringResource(R.string.tapnum_title),
                subtitle = stringResource(R.string.tapnum_hint),
                onClick = onTapNumber,
            ) { NumbersArt() }
            GameCard(
                title = stringResource(R.string.pattern_title),
                subtitle = stringResource(R.string.pattern_hint),
                onClick = onPattern,
            ) { CountArt() }
            GameCard(
                title = stringResource(R.string.addition_title),
                subtitle = stringResource(R.string.addition_hint),
                onClick = onAddition,
            ) { CountArt() }
            GameCard(
                title = stringResource(R.string.shapes_title),
                subtitle = stringResource(R.string.shapes_hint),
                onClick = onShapes,
            ) { NumbersArt() }
            GameCard(
                title = stringResource(R.string.missing_title),
                subtitle = stringResource(R.string.missing_hint),
                onClick = onMissing,
            ) { NumbersArt() }
            GameCard(
                title = stringResource(R.string.math_title),
                subtitle = stringResource(R.string.math_hint),
                onClick = onMath,
            ) { NumbersArt() }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GameCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    art: @Composable () -> Unit,
) {
    val highContrast = LocalHighContrast.current
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (highContrast) 2.dp else 1.dp,
            color = if (highContrast) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                art()
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}