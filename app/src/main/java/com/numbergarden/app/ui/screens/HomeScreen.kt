package com.numbergarden.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.numbergarden.app.R
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.ui.components.BigActionTile
import com.numbergarden.app.ui.components.CompareArt
import com.numbergarden.app.ui.components.CountArt
import com.numbergarden.app.ui.components.FlowerArt
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.NumbersArt
import com.numbergarden.app.ui.components.WatchArt
import com.numbergarden.app.ui.theme.LocalHighContrast

@Composable
fun HomeScreen(
    container: AppContainer,
    onWatch: () -> Unit,
    onCount: () -> Unit,
    onCompare: () -> Unit,
    onNumbers: () -> Unit,
    onPlay: () -> Unit,
    onGarden: () -> Unit,
    onGrownUps: () -> Unit,
) {
    val progress by container.progressRepository.progress.collectAsStateWithLifecycle()
    // At large font scales a two-column grid breaks words like "Compare" mid-word.
    // Switch to one full-width tile per row so every label keeps its shape.
    val largeText = LocalDensity.current.fontScale >= 1.5f

    GardenScaffold(title = stringResource(R.string.app_name)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.home_greeting),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (largeText) {
                LargeActionTile(
                    title = stringResource(R.string.home_watch),
                    subtitle = stringResource(R.string.home_watch_hint),
                    onClick = onWatch,
                ) { WatchArt() }
                LargeActionTile(
                    title = stringResource(R.string.home_count),
                    subtitle = stringResource(R.string.home_count_hint),
                    onClick = onCount,
                ) { CountArt() }
                LargeActionTile(
                    title = stringResource(R.string.home_compare),
                    subtitle = stringResource(R.string.home_compare_hint),
                    onClick = onCompare,
                ) { CompareArt() }
                LargeActionTile(
                    title = stringResource(R.string.home_numbers),
                    subtitle = stringResource(R.string.home_numbers_hint),
                    onClick = onNumbers,
                ) { NumbersArt() }
                LargeActionTile(
                    title = stringResource(R.string.home_play),
                    subtitle = stringResource(R.string.home_play_hint),
                    onClick = onPlay,
                ) { CountArt() }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    BigActionTile(
                        title = stringResource(R.string.home_watch),
                        subtitle = stringResource(R.string.home_watch_hint),
                        onClick = onWatch,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    ) { WatchArt() }
                    BigActionTile(
                        title = stringResource(R.string.home_count),
                        subtitle = stringResource(R.string.home_count_hint),
                        onClick = onCount,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    ) { CountArt() }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    BigActionTile(
                        title = stringResource(R.string.home_compare),
                        subtitle = stringResource(R.string.home_compare_hint),
                        onClick = onCompare,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    ) { CompareArt() }
                    BigActionTile(
                        title = stringResource(R.string.home_numbers),
                        subtitle = stringResource(R.string.home_numbers_hint),
                        onClick = onNumbers,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    ) { NumbersArt() }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    BigActionTile(
                        title = stringResource(R.string.home_play),
                        subtitle = stringResource(R.string.home_play_hint),
                        onClick = onPlay,
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    ) { CountArt() }
                }
            }

            GardenHomeCard(
                blooms = progress.blooms,
                onClick = onGarden,
            )

            Spacer(Modifier.height(4.dp))

            OutlinedButton(
                onClick = onGrownUps,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                ),
            ) {
                Text(
                    text = stringResource(R.string.grown_ups),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Text(
                text = stringResource(R.string.grown_ups_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

/**
 * Full-width version of [BigActionTile] used when the system font scale is large.
 * A single column of tiles keeps every label on one line instead of breaking
 * words like "Compare" mid-word in a two-column grid.
 */
@Composable
private fun LargeActionTile(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    art: @Composable () -> Unit,
) {
    val highContrast = LocalHighContrast.current
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 112.dp)
            .semantics(mergeDescendants = true) { role = Role.Button },
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
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(contentAlignment = Alignment.CenterStart) {
                art()
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
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

@Composable
private fun GardenHomeCard(
    blooms: Int,
    onClick: () -> Unit,
) {
    val highContrast = LocalHighContrast.current
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 104.dp)
            .semantics(mergeDescendants = true) {},
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
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
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FlowerArt(bloomed = blooms > 0, size = 58.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.home_garden),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = if (blooms > 0) {
                        pluralStringResource(R.plurals.garden_flowers, blooms, blooms)
                    } else {
                        stringResource(R.string.garden_empty)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            if (blooms > 0) {
                FlowerArt(
                    bloomed = true,
                    size = 34.dp,
                    modifier = Modifier.size(34.dp),
                    petalColor = com.numbergarden.app.ui.theme.FlowerAmber,
                )
            }
        }
    }
}
