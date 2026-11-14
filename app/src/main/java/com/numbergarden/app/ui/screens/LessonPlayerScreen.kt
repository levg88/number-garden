package com.numbergarden.app.ui.screens

import android.content.Context
import android.content.ContextWrapper
import android.media.MediaPlayer
import android.net.Uri
import android.view.WindowManager
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.numbergarden.app.R
import com.numbergarden.app.audio.LocalSpeech
import com.numbergarden.app.data.AssetVideoCache
import com.numbergarden.app.data.ContentLoadException
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.model.Lesson
import com.numbergarden.app.model.ProgressRepository
import com.numbergarden.app.ui.components.ContentErrorScreen
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private class VideoHolder {
    var view: VideoView? = null
}

@Composable
fun LessonPlayerScreen(
    container: AppContainer,
    lessonId: String,
    onBack: () -> Unit,
    onHome: () -> Unit,
) {
    val result = remember(lessonId) {
        runCatching { container.contentRepository.load().lessons.find { it.id == lessonId } }
    }
    val settings by container.settingsRepository.settings.collectAsStateWithLifecycle()

    val lesson = result.getOrNull()
    if (result.isFailure || lesson == null) {
        ContentErrorScreen(
            message = (result.exceptionOrNull() as? ContentLoadException)?.message
                ?: "Video not found",
            onRetry = onBack,
            onHome = onHome,
        )
        return
    }

    PlayerContent(
        lesson = lesson,
        soundEnabled = settings.sound,
        progressRepository = container.progressRepository,
        onBack = onBack,
        onHome = onHome,
    )
}

@Composable
private fun PlayerContent(
    lesson: Lesson,
    soundEnabled: Boolean,
    progressRepository: ProgressRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
) {
    val context = LocalContext.current
    val speech = LocalSpeech.current
    val posterRes = when (lesson.id) {
        "count_to_3" -> R.drawable.poster_count_to_3
        "more_and_less" -> R.drawable.poster_more_and_less
        else -> R.drawable.poster_count_to_3
    }
    val holder = remember(lesson.id) { VideoHolder() }
    var videoUri by remember(lesson.id) { mutableStateOf<Uri?>(null) }
    var videoError by remember(lesson.id) { mutableStateOf(false) }
    var mediaPlayer by remember(lesson.id) { mutableStateOf<MediaPlayer?>(null) }
    var prepared by remember(lesson.id) { mutableStateOf(false) }
    var isPlaying by remember(lesson.id) { mutableStateOf(false) }
    var finished by remember(lesson.id) { mutableStateOf(false) }
    var captionIndex by remember(lesson.id) { mutableIntStateOf(-1) }
    var savedPosition by rememberSaveable(lesson.id) { mutableIntStateOf(0) }

    val activity = remember(context) { context.findActivity() }

    LaunchedEffect(lesson.id) {
        val result = withContext(Dispatchers.IO) {
            runCatching { AssetVideoCache.ensure(context.applicationContext, lesson.video) }
        }
        result.onSuccess { videoUri = it }.onFailure { videoError = true }
    }

    DisposableEffect(lesson.id) {
        onDispose {
            holder.view?.let { view ->
                savedPosition = view.currentPosition
                view.stopPlayback()
            }
            speech.stop()
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    DisposableEffect(isPlaying) {
        if (isPlaying) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                holder.view?.pause()
                isPlaying = false
                speech.stop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(soundEnabled, prepared) {
        val volume = if (soundEnabled) 0.35f else 0f
        mediaPlayer?.setVolume(volume, volume)
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            val position = holder.view?.currentPosition ?: 0
            captionIndex = lesson.captions.indexOfLast { it.atMs <= position }
            delay(120)
        }
    }

    LaunchedEffect(captionIndex, lesson.id) {
        if (captionIndex in lesson.captions.indices) {
            speech.say(lesson.captions[captionIndex].text)
        }
    }

    LaunchedEffect(finished) {
        if (finished) progressRepository.markLessonWatched(lesson.id)
    }

    val playPause: () -> Unit = {
        val view = holder.view
        if (view != null && prepared) {
            if (finished) {
                view.seekTo(0)
                savedPosition = 0
                finished = false
                captionIndex = -1
                view.start()
                isPlaying = true
            } else if (view.isPlaying) {
                view.pause()
                savedPosition = view.currentPosition
                isPlaying = false
            } else {
                view.start()
                isPlaying = true
            }
        }
    }

    val replay: () -> Unit = {
        val view = holder.view
        if (view != null && prepared) {
            view.seekTo(0)
            savedPosition = 0
            finished = false
            captionIndex = -1
            view.start()
            isPlaying = true
        }
    }

    val currentCaption = if (captionIndex in lesson.captions.indices) {
        lesson.captions[captionIndex].text
    } else {
        ""
    }

    GardenScaffold(
        title = lesson.title,
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(posterRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                if (videoUri != null) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setBackgroundColor(android.graphics.Color.BLACK)
                                holder.view = this
                                setOnPreparedListener { player ->
                                    mediaPlayer = player
                                    prepared = true
                                    val volume = if (soundEnabled) 0.35f else 0f
                                    player.setVolume(volume, volume)
                                    if (savedPosition > 0) seekTo(savedPosition) else seekTo(1)
                                }
                                setOnErrorListener { _, _, _ ->
                                    videoError = true
                                    true
                                }
                                setOnCompletionListener {
                                    isPlaying = false
                                    finished = true
                                    captionIndex = lesson.captions.lastIndex
                                    savedPosition = 0
                                }
                                setVideoURI(videoUri)
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                } else if (videoError) {
                    Text(
                        text = stringResource(R.string.lesson_video_missing),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp),
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Text(
                            text = stringResource(R.string.lesson_loading),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                        )
                    }
                }

                if (prepared && !isPlaying) {
                    Surface(
                        onClick = playPause,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                        border = BorderStroke(
                            2.dp,
                            if (finished) {
                                MaterialTheme.colorScheme.outline
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                        ),
                    ) {
                        Box(
                            modifier = Modifier.size(84.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (finished) Icons.Filled.Refresh else Icons.Filled.PlayArrow,
                                contentDescription = stringResource(
                                    if (finished) R.string.cd_replay else R.string.cd_play,
                                ),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(46.dp),
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 92.dp)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = currentCaption.ifBlank { stringResource(R.string.lesson_caption_empty) },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            if (finished) {
                Text(
                    text = stringResource(R.string.lesson_the_end),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics { heading() },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GardenButton(
                    text = stringResource(
                        if (isPlaying) R.string.lesson_pause else R.string.lesson_play,
                    ),
                    onClick = playPause,
                    enabled = prepared,
                    modifier = Modifier.weight(1f),
                )
                GardenButton(
                    text = stringResource(R.string.lesson_replay),
                    onClick = replay,
                    primary = false,
                    enabled = prepared,
                    modifier = Modifier.weight(1f),
                )
            }

            GardenButton(
                text = stringResource(R.string.lesson_back_to_videos),
                onClick = onBack,
                primary = false,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

private tailrec fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
