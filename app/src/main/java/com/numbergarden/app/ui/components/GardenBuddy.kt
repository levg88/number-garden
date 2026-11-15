@file:Suppress("DEPRECATION")

package com.numbergarden.app.ui.components

import android.graphics.Color as AndroidColor
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit
import com.numbergarden.app.R
import com.numbergarden.app.rive.RiveRuntime
import com.numbergarden.app.ui.theme.FlowerLeaf
import com.numbergarden.app.ui.theme.FlowerRose
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lumi, the garden helper.
 *
 * The character is a real Rive state machine (`garden_buddy.riv`). Each game passes its
 * own [GameMotionTheme], so the aura and halo change with the activity, while the
 * character keeps a single visual identity. A correct answer fires the Rive trigger and
 * the state machine plays its authored "happy" transition.
 *
 * If the runtime or the asset cannot start (old device, vendor ROM, corrupt cache) the
 * composable quietly falls back to a hand-drawn face, so play never depends on it.
 */
@Composable
fun GardenBuddy(
    theme: GameMotionTheme,
    reduceMotion: Boolean,
    celebrating: Boolean,
    modifier: Modifier = Modifier,
) {
    val motionAllowed = rememberMotionAllowed(reduceMotion)
    val scale = remember { Animatable(1f) }
    var riveReady by remember { mutableStateOf(false) }

    LaunchedEffect(celebrating) {
        if (celebrating && motionAllowed) {
            scale.snapTo(1f)
            scale.animateTo(1.16f, animationSpec = spring(dampingRatio = 0.42f, stiffness = 320f))
            scale.animateTo(1f, animationSpec = tween(420))
        } else {
            scale.snapTo(1f)
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension * 0.42f
                drawCircle(
                    color = theme.accent.copy(alpha = 0.14f),
                    radius = radius,
                    center = center,
                )
                drawCircle(
                    color = theme.accent.copy(alpha = 0.24f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = size.minDimension * 0.025f),
                )
                repeat(4) { index ->
                    val angle = index * (Math.PI / 2).toFloat() + 0.5f
                    drawCircle(
                        color = FlowerLeaf.copy(alpha = 0.45f),
                        radius = size.minDimension * 0.035f,
                        center = center + androidx.compose.ui.geometry.Offset(
                            cos(angle) * radius * 0.9f,
                            sin(angle) * radius * 0.9f,
                        ),
                    )
                }
            }

            if (!riveReady) {
                BuddyFaceFallback(accent = theme.accent)
            }

            if (RiveRuntime.available) {
                AndroidView(
                    factory = { context ->
                        RiveAnimationView(context).apply {
                            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                            runCatching {
                                setRiveResource(
                                    resId = R.raw.garden_buddy,
                                    artboardName = "New Artboard",
                                    stateMachineName = "State Machine 1",
                                    fit = Fit.CONTAIN,
                                    autoplay = true,
                                )
                            }
                            postDelayed(
                                {
                                    riveReady = runCatching { controller.file != null }.getOrDefault(false)
                                },
                                700,
                            )
                        }
                    },
                    update = { view ->
                        val wasCelebrating = view.tag as? Boolean ?: false
                        if (celebrating && !wasCelebrating) {
                            runCatching {
                                view.fireState("State Machine 1", "Trigger 1")
                            }
                        }
                        view.tag = celebrating
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun BuddyFaceFallback(accent: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val faceRadius = size.minDimension * 0.27f
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = faceRadius,
            center = center,
        )
        drawCircle(
            color = FlowerRose.copy(alpha = 0.55f),
            radius = faceRadius * 0.16f,
            center = center + androidx.compose.ui.geometry.Offset(-faceRadius * 0.38f, -faceRadius * 0.12f),
        )
        drawCircle(
            color = FlowerRose.copy(alpha = 0.55f),
            radius = faceRadius * 0.16f,
            center = center + androidx.compose.ui.geometry.Offset(faceRadius * 0.38f, -faceRadius * 0.12f),
        )
        drawArc(
            color = accent.copy(alpha = 0.75f),
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = center + androidx.compose.ui.geometry.Offset(-faceRadius * 0.38f, -faceRadius * 0.20f),
            size = androidx.compose.ui.geometry.Size(faceRadius * 0.76f, faceRadius * 0.56f),
            style = Stroke(width = faceRadius * 0.08f),
        )
    }
}
