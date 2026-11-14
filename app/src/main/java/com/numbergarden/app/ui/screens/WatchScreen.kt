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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.numbergarden.app.R
import com.numbergarden.app.data.ContentLoadException
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.model.Lesson
import com.numbergarden.app.ui.components.CompareArt
import com.numbergarden.app.ui.components.ContentErrorScreen
import com.numbergarden.app.ui.components.CountArt
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.WatchArt
import com.numbergarden.app.ui.theme.LocalHighContrast

@Composable
fun WatchScreen(
    container: AppContainer,
    onLesson: (String) -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit,
) {
    var attempt by remember { mutableIntStateOf(0) }
    val result = remember(attempt) {
        runCatching { container.contentRepository.load().lessons }
    }
    val progress by container.progressRepository.progress.collectAsStateWithLifecycle()

    result.onFailure { error ->
        ContentErrorScreen(
            message = (error as? ContentLoadException)?.message ?: error.message,
            onRetry = { attempt += 1 },
            onHome = onHome,
        )
        return
    }

    val lessons = result.getOrThrow()

    GardenScaffold(
        title = stringResource(R.string.watch_title),
        onBack = onBack,
        onHome = onHome,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.watch_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (lessons.isEmpty()) {
                Text(
                    text = stringResource(R.string.watch_no_lessons),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            lessons.forEach { lesson ->
                LessonCard(
                    lesson = lesson,
                    watched = lesson.id in progress.completedLessons,
                    onClick = { onLesson(lesson.id) },
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun LessonCard(
    lesson: Lesson,
    watched: Boolean,
    onClick: () -> Unit,
) {
    val highContrast = LocalHighContrast.current
    val description = stringResource(
        R.string.watch_card_cd,
        lesson.title,
        lesson.summary,
        lesson.durationSec,
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 124.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = description
            },
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 82.dp),
                contentAlignment = Alignment.Center,
            ) {
                when (lesson.skill) {
                    "counting" -> CountArt()
                    "comparing" -> CompareArt()
                    else -> WatchArt()
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = lesson.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.watch_seconds, lesson.durationSec),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (watched) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.watch_watched),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}
