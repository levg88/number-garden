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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.numbergarden.app.R
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.model.Progress
import com.numbergarden.app.ui.components.GardenPlot
import com.numbergarden.app.ui.components.GardenScaffold

@Composable
fun GardenScreen(
    container: AppContainer,
    onHome: () -> Unit,
) {
    val progress by container.progressRepository.progress.collectAsStateWithLifecycle()

    GardenScaffold(
        title = stringResource(R.string.garden_title),
        onHome = onHome,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = pluralStringResource(
                    R.plurals.garden_flowers,
                    progress.blooms,
                    progress.blooms,
                ),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(R.string.garden_progress, progress.blooms, Progress.MAX_BLOOMS),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (progress.blooms == 0) {
                Text(
                    text = stringResource(R.string.garden_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (progress.blooms >= Progress.MAX_BLOOMS) {
                Text(
                    text = stringResource(R.string.garden_full),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                (0 until Progress.MAX_BLOOMS).chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        row.forEach { index ->
                            GardenPlot(
                                bloomed = index < progress.blooms,
                                index = index,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(3 - row.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
