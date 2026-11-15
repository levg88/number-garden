package com.numbergarden.app.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.numbergarden.app.ui.theme.AppleLeaf
import com.numbergarden.app.ui.theme.AppleRed
import com.numbergarden.app.ui.theme.AppleRedDark
import com.numbergarden.app.ui.theme.AppleStem
import com.numbergarden.app.ui.theme.FlowerAmber
import com.numbergarden.app.ui.theme.FlowerCenter
import com.numbergarden.app.ui.theme.FlowerLeaf
import com.numbergarden.app.ui.theme.FlowerRose
import com.numbergarden.app.ui.theme.FlowerSky
import com.numbergarden.app.ui.theme.GardenAmber
import com.numbergarden.app.ui.theme.GardenGreen
import com.numbergarden.app.ui.theme.GardenLeaf
import com.numbergarden.app.ui.theme.LocalHighContrast
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Every activity owns a different little animated scene. The scenes are intentionally
 * quiet: slow, looping, and decorative. They never carry the answer and they can be
 * frozen completely by the "Reduce motion" setting.
 */
enum class GameMotionTheme(
    val accent: Color,
    val soft: Color,
) {
    COUNTING(AppleRed, Color(0xFFF6E2D8)),
    COMPARE(GardenAmber, Color(0xFFF6EBD8)),
    MATCH(FlowerSky, Color(0xFFE1EDF2)),
    TAP_NUMBER(GardenGreen, Color(0xFFE0EBE1)),
    PATTERN(FlowerRose, Color(0xFFF5E2E0)),
    ADDITION(FlowerAmber, Color(0xFFF7EBD7)),
    SHAPES(FlowerSky, Color(0xFFE2ECF1)),
    MISSING(GardenLeaf, Color(0xFFE5EBDD)),
    MATH(AppleRedDark, Color(0xFFF3E1DC)),
    ORDER(GardenGreen, Color(0xFFE2EDE2)),
    BONDS(FlowerAmber, Color(0xFFF6EAD6)),
    POSITIONS(FlowerRose, Color(0xFFF4E3E1)),
    NUMBERS(GardenGreen, Color(0xFFE4ECE4)),
    GARDEN(FlowerRose, Color(0xFFF5E4E0)),
}

@Composable
fun GameSceneHeader(
    theme: GameMotionTheme,
    reduceMotion: Boolean,
    celebrating: Boolean,
    modifier: Modifier = Modifier,
) {
    val motionAllowed = rememberMotionAllowed(reduceMotion)
    val (sceneTime, sceneBob) = rememberSceneClock(motionAllowed)
    val burst = remember { Animatable(0f) }
    LaunchedEffect(celebrating) {
        if (celebrating && motionAllowed) {
            burst.snapTo(0f)
            burst.animateTo(1f, animationSpec = tween(520))
            burst.animateTo(0f, animationSpec = tween(420))
        } else {
            burst.snapTo(0f)
        }
    }

    val highContrast = LocalHighContrast.current
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = if (highContrast) scheme.surface else theme.soft,
        border = BorderStroke(
            width = if (highContrast) 2.dp else 1.dp,
            color = if (highContrast) scheme.outline else theme.accent.copy(alpha = 0.24f),
        ),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(126.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawScene(
                    theme = theme,
                    t = sceneTime,
                    bob = sceneBob,
                    celebrating = celebrating,
                    highContrast = highContrast,
                )
                drawBurst(burst.value, theme.accent)
            }
            GardenBuddy(
                theme = theme,
                reduceMotion = reduceMotion,
                celebrating = celebrating,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .size(78.dp),
            )
        }
    }
}

@Composable
private fun rememberSceneClock(motionAllowed: Boolean): Pair<Float, Float> {
    if (!motionAllowed) return 0.32f to 0.5f
    val transition = rememberInfiniteTransition(label = "scene-clock")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scene-time",
    )
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scene-bob",
    )
    return time to bob
}

private fun DrawScope.drawScene(
    theme: GameMotionTheme,
    t: Float,
    bob: Float,
    celebrating: Boolean,
    highContrast: Boolean,
) {
    val w = size.width
    val h = size.height
    val ink = if (highContrast) Color.Black else Color(0xFF22302A)
    val accent = theme.accent
    val playW = w * 0.78f
    val twoPi = (2 * PI).toFloat()

    when (theme) {
        GameMotionTheme.COUNTING -> {
            drawGround(playW, h, accent)
            repeat(4) { index ->
                val x = playW * (0.14f + index * 0.17f)
                val y = h * (0.57f + sin(t * twoPi + index * 0.9f) * 0.045f)
                drawApple(Offset(x, y), h * 0.13f, accent)
                drawSparkle(
                    Offset(x - h * 0.20f, y - h * 0.12f + bob * h * 0.03f),
                    h * 0.025f,
                    FlowerCenter,
                )
            }
            drawLeafTrail(t, playW, h, accent)
        }

        GameMotionTheme.COMPARE -> {
            val pivot = Offset(playW * 0.50f, h * 0.68f)
            val tilt = sin(t * twoPi) * 6.5f
            drawGround(playW, h, accent)
            val fulcrum = Path().apply {
                moveTo(pivot.x, pivot.y)
                lineTo(pivot.x - h * 0.13f, h * 0.88f)
                lineTo(pivot.x + h * 0.13f, h * 0.88f)
                close()
            }
            drawPath(fulcrum, ink.copy(alpha = 0.20f))
            rotate(degrees = tilt, pivot = pivot) {
                drawLine(
                    color = ink.copy(alpha = 0.45f),
                    start = Offset(playW * 0.15f, pivot.y),
                    end = Offset(playW * 0.85f, pivot.y),
                    strokeWidth = h * 0.025f,
                    cap = StrokeCap.Round,
                )
                repeat(3) { index ->
                    drawApple(
                        Offset(playW * 0.20f + index * h * 0.075f, pivot.y - h * 0.10f),
                        h * 0.058f,
                        accent,
                    )
                }
                drawApple(Offset(playW * 0.78f, pivot.y - h * 0.10f), h * 0.058f, accent)
            }
        }

        GameMotionTheme.MATCH -> {
            val flip = cos(t * twoPi)
            drawCard(
                center = Offset(playW * 0.34f, h * 0.52f),
                width = h * 0.23f * (0.35f + 0.65f * kotlin.math.abs(flip)),
                height = h * 0.34f,
                fill = accent.copy(alpha = 0.16f),
                stroke = accent,
            )
            drawCard(
                center = Offset(playW * 0.66f, h * 0.52f),
                width = h * 0.23f * (0.35f + 0.65f * kotlin.math.abs(-flip)),
                height = h * 0.34f,
                fill = theme.accent.copy(alpha = 0.16f),
                stroke = accent,
            )
            repeat(3) { index ->
                drawCircle(
                    color = accent,
                    radius = h * 0.022f,
                    center = Offset(playW * 0.34f - h * 0.045f + index * h * 0.045f, h * 0.53f),
                )
            }
            repeat(1) { index ->
                drawCircle(
                    color = accent,
                    radius = h * 0.022f,
                    center = Offset(playW * 0.66f - h * 0.022f + index * h * 0.045f, h * 0.53f),
                )
            }
        }

        GameMotionTheme.TAP_NUMBER -> {
            val numbers = listOf("1", "2", "3")
            numbers.forEachIndexed { index, label ->
                val x = playW * (0.20f + index * 0.20f)
                val y = h * (0.48f + sin(t * twoPi + index * 1.3f) * 0.06f)
                val active = (t * 3f).toInt() % 3 == index
                drawCircle(
                    color = if (active) accent else Color.White,
                    radius = h * 0.13f,
                    center = Offset(x, y),
                )
                drawCircle(
                    color = accent.copy(alpha = if (active) 0.0f else 0.35f),
                    radius = h * 0.13f,
                    center = Offset(x, y),
                    style = Stroke(width = h * 0.012f),
                )
                drawNumber(Offset(x, y + h * 0.02f), label, h * 0.13f, if (active) Color.White else accent)
            }
            drawSparkle(Offset(playW * 0.86f, h * 0.24f + bob * h * 0.05f), h * 0.028f, FlowerCenter)
        }

        GameMotionTheme.PATTERN -> {
            drawGround(playW, h, accent)
            repeat(5) { index ->
                val x = playW * (0.10f + index * 0.16f)
                val y = h * (0.55f + sin(t * twoPi + index * 0.7f) * 0.035f)
                if (index % 2 == 0) {
                    drawApple(Offset(x, y), h * 0.075f, accent)
                } else {
                    drawFlower(Offset(x, y), h * 0.085f, FlowerRose)
                }
                drawLine(
                    color = accent.copy(alpha = 0.25f),
                    start = Offset(x, h * 0.78f),
                    end = Offset(x, y + h * 0.06f),
                    strokeWidth = h * 0.012f,
                )
            }
            val pulse = 0.5f + 0.5f * sin(t * twoPi)
            drawCircle(
                color = theme.accent.copy(alpha = 0.10f + pulse * 0.14f),
                radius = h * 0.13f,
                center = Offset(playW * 0.90f, h * 0.55f),
            )
            drawNumber(Offset(playW * 0.90f, h * 0.57f), "?", h * 0.13f, accent)
        }

        GameMotionTheme.ADDITION -> {
            val slide = (sin(t * twoPi) * 0.5f + 0.5f)
            val leftX = playW * (0.18f + slide * 0.10f)
            val rightX = playW * (0.66f - slide * 0.10f)
            repeat(2) { index ->
                drawCircle(accent, h * 0.048f, Offset(leftX + index * h * 0.10f, h * 0.53f))
            }
            repeat(3) { index ->
                drawCircle(FlowerAmber, h * 0.048f, Offset(rightX + index * h * 0.10f, h * 0.53f))
            }
            drawNumber(Offset(playW * 0.50f, h * 0.50f), "+", h * 0.16f, ink.copy(alpha = 0.55f))
            val pop = 0.4f + 0.6f * (-cos(t * twoPi) * 0.5f + 0.5f)
            drawSparkle(Offset(playW * 0.50f, h * 0.22f), h * 0.04f * pop, FlowerCenter)
        }

        GameMotionTheme.SHAPES -> {
            drawGround(playW, h, accent)
            val shapeY = h * 0.54f
            rotate(degrees = sin(t * twoPi) * 8f, pivot = Offset(playW * 0.22f, shapeY)) {
                drawCircle(accent, h * 0.12f, Offset(playW * 0.22f, shapeY))
            }
            rotate(degrees = -sin(t * twoPi + 1f) * 8f, pivot = Offset(playW * 0.50f, shapeY)) {
                drawRoundRect(
                    color = FlowerAmber,
                    topLeft = Offset(playW * 0.50f - h * 0.11f, shapeY - h * 0.11f),
                    size = Size(h * 0.22f, h * 0.22f),
                    cornerRadius = CornerRadius(h * 0.03f),
                )
            }
            rotate(degrees = sin(t * twoPi + 2f) * 10f, pivot = Offset(playW * 0.78f, shapeY)) {
                val triangle = Path().apply {
                    moveTo(playW * 0.78f, shapeY - h * 0.13f)
                    lineTo(playW * 0.78f + h * 0.13f, shapeY + h * 0.11f)
                    lineTo(playW * 0.78f - h * 0.13f, shapeY + h * 0.11f)
                    close()
                }
                drawPath(triangle, FlowerSky)
            }
        }

        GameMotionTheme.MISSING -> {
            val numbers = listOf("1", "2", "?", "4")
            numbers.forEachIndexed { index, label ->
                val x = playW * (0.14f + index * 0.20f)
                val isMissing = label == "?"
                val pulse = if (isMissing) 0.5f + 0.5f * sin(t * twoPi) else 0f
                drawCircle(
                    color = if (isMissing) {
                        theme.accent.copy(alpha = 0.12f + pulse * 0.14f)
                    } else {
                        Color.White
                    },
                    radius = h * 0.12f,
                    center = Offset(x, h * 0.52f),
                )
                drawCircle(
                    color = accent.copy(alpha = 0.55f),
                    radius = h * 0.12f,
                    center = Offset(x, h * 0.52f),
                    style = Stroke(
                        width = if (isMissing) h * 0.012f else h * 0.008f,
                        pathEffect = if (isMissing) {
                            androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(h * 0.035f, h * 0.025f),
                            )
                        } else {
                            null
                        },
                    ),
                )
                drawNumber(Offset(x, h * 0.54f), label, h * 0.12f, accent)
            }
        }

        GameMotionTheme.MATH -> {
            val orbit = t * twoPi
            drawNumber(Offset(playW * 0.50f, h * 0.50f), "+", h * 0.20f, accent)
            repeat(2) { index ->
                val angle = orbit + index * PI.toFloat()
                drawNumber(
                    Offset(
                        playW * 0.50f + cos(angle) * playW * 0.30f,
                        h * 0.52f + sin(angle) * h * 0.20f,
                    ),
                    if (index == 0) "−" else "=",
                    h * 0.11f,
                    ink.copy(alpha = 0.55f),
                )
            }
            repeat(3) { index ->
                drawCircle(
                    color = FlowerAmber,
                    radius = h * 0.024f,
                    center = Offset(playW * 0.20f + index * h * 0.06f, h * 0.20f),
                )
            }
        }

        GameMotionTheme.ORDER -> {
            val vine = Path().apply {
                moveTo(playW * 0.08f, h * 0.82f)
                cubicTo(
                    playW * 0.30f, h * 0.80f,
                    playW * 0.40f, h * 0.30f,
                    playW * 0.62f, h * 0.35f,
                )
                cubicTo(
                    playW * 0.80f, h * 0.40f,
                    playW * 0.84f, h * 0.75f,
                    playW * 0.94f, h * 0.72f,
                )
            }
            drawPath(vine, FlowerLeaf.copy(alpha = 0.55f), style = Stroke(width = h * 0.025f, cap = StrokeCap.Round))
            val nodes = listOf(
                Offset(playW * 0.12f, h * 0.78f),
                Offset(playW * 0.30f, h * 0.65f),
                Offset(playW * 0.47f, h * 0.38f),
                Offset(playW * 0.68f, h * 0.40f),
                Offset(playW * 0.87f, h * 0.70f),
            )
            nodes.forEachIndexed { index, center ->
                val active = (t * 5f).toInt() % 5 == index
                drawCircle(
                    color = if (active) accent else Color.White,
                    radius = h * 0.095f,
                    center = center,
                )
                drawCircle(
                    color = accent.copy(alpha = 0.55f),
                    radius = h * 0.095f,
                    center = center,
                    style = Stroke(width = h * 0.009f),
                )
                drawNumber(center + Offset(0f, h * 0.015f), "${index + 1}", h * 0.095f, if (active) Color.White else accent)
            }
        }

        GameMotionTheme.BONDS -> {
            val trunkTop = Offset(playW * 0.50f, h * 0.36f)
            drawLine(
                color = FlowerLeaf.copy(alpha = 0.55f),
                start = Offset(playW * 0.50f, h * 0.86f),
                end = trunkTop,
                strokeWidth = h * 0.03f,
                cap = StrokeCap.Round,
            )
            val left = Offset(playW * 0.27f, h * 0.66f)
            val right = Offset(playW * 0.73f, h * 0.66f)
            drawLine(
                color = FlowerLeaf.copy(alpha = 0.55f),
                start = trunkTop,
                end = left,
                strokeWidth = h * 0.02f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = FlowerLeaf.copy(alpha = 0.55f),
                start = trunkTop,
                end = right,
                strokeWidth = h * 0.02f,
                cap = StrokeCap.Round,
            )
            drawNumber(left, "2", h * 0.10f, accent)
            drawNumber(right, "3", h * 0.10f, accent)
            val pulse = 0.5f + 0.5f * sin(t * twoPi)
            drawCircle(
                color = FlowerAmber.copy(alpha = 0.15f + pulse * 0.15f),
                radius = h * 0.13f,
                center = Offset(trunkTop.x, trunkTop.y - h * 0.06f),
            )
            drawNumber(Offset(trunkTop.x, trunkTop.y - h * 0.04f), "5", h * 0.13f, accent)
        }

        GameMotionTheme.POSITIONS -> {
            drawGround(playW, h, accent)
            val podium = listOf(
                Triple(0.28f, h * 0.66f, 1),
                Triple(0.50f, h * 0.58f, 2),
                Triple(0.72f, h * 0.66f, 3),
            )
            podium.forEachIndexed { index, (xF, y, number) ->
                val x = playW * xF
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(x - h * 0.10f, y),
                    size = Size(h * 0.20f, h * 0.22f),
                    cornerRadius = CornerRadius(h * 0.04f),
                )
                drawNumber(Offset(x, y + h * 0.13f), "$number", h * 0.08f, accent)
                if (index == 1) {
                    val sweep = (sin(t * twoPi) * 0.5f + 0.5f)
                    drawOval(
                        color = FlowerCenter.copy(alpha = 0.18f + sweep * 0.12f),
                        topLeft = Offset(x - h * 0.16f, y - h * 0.22f),
                        size = Size(h * 0.32f, h * 0.20f),
                    )
                }
            }
        }

        GameMotionTheme.NUMBERS -> {
            val cell = h * 0.14f
            val startX = playW * 0.50f - cell * 2.5f
            repeat(2) { row ->
                repeat(5) { column ->
                    val index = row * 5 + column
                    val lit = (t * 10f).toInt() % 10 >= index
                    val x = startX + column * cell
                    val y = h * 0.40f + row * cell
                    drawRoundRect(
                        color = if (lit) theme.accent.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.72f),
                        topLeft = Offset(x, y),
                        size = Size(cell * 0.84f, cell * 0.84f),
                        cornerRadius = CornerRadius(h * 0.035f),
                    )
                    if (index < 7) {
                        drawApple(
                            Offset(x + cell * 0.42f, y + cell * 0.44f),
                            cell * 0.16f,
                            if (lit) accent else accent.copy(alpha = 0.28f),
                        )
                    }
                }
            }
        }

        GameMotionTheme.GARDEN -> {
            drawGround(playW, h, accent)
            repeat(5) { index ->
                val x = playW * (0.13f + index * 0.16f)
                val sway = sin(t * twoPi + index * 0.8f) * 0.06f
                drawLine(
                    color = FlowerLeaf.copy(alpha = 0.72f),
                    start = Offset(x, h * 0.86f),
                    end = Offset(x + sway * h, h * 0.52f),
                    strokeWidth = h * 0.018f,
                    cap = StrokeCap.Round,
                )
                drawFlower(
                    Offset(x + sway * h, h * 0.40f + sin(t * twoPi + index) * h * 0.02f),
                    h * 0.095f,
                    listOf(FlowerRose, FlowerAmber, FlowerSky)[index % 3],
                )
            }
            val butterflyX = playW * (0.20f + (sin(t * twoPi) * 0.5f + 0.5f) * 0.55f)
            val butterflyY = h * (0.20f + cos(t * twoPi * 2f) * 0.05f)
            drawButterfly(Offset(butterflyX, butterflyY), h * 0.035f, FlowerAmber)
        }
    }

    if (celebrating) {
        drawSparkle(Offset(playW * 0.90f, h * 0.22f), h * 0.03f, FlowerCenter)
    }
}

private fun DrawScope.drawGround(playW: Float, h: Float, accent: Color) {
    drawLine(
        color = accent.copy(alpha = 0.20f),
        start = Offset(playW * 0.05f, h * 0.86f),
        end = Offset(playW * 0.95f, h * 0.86f),
        strokeWidth = h * 0.012f,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawApple(center: Offset, radius: Float, color: Color) {
    drawCircle(color = color.copy(alpha = 0.35f), radius = radius, center = center + Offset(0f, radius * 0.06f))
    drawCircle(color = color, radius = radius, center = center)
    drawCircle(
        color = Color.White.copy(alpha = 0.18f),
        radius = radius * 0.42f,
        center = center - Offset(radius * 0.30f, radius * 0.32f),
    )
    drawLine(
        color = AppleStem,
        start = Offset(center.x + radius * 0.06f, center.y - radius * 0.92f),
        end = Offset(center.x + radius * 0.14f, center.y - radius * 1.24f),
        strokeWidth = radius * 0.15f,
        cap = StrokeCap.Round,
    )
    rotate(degrees = -28f, pivot = Offset(center.x + radius * 0.15f, center.y - radius * 1.15f)) {
        drawOval(
            color = AppleLeaf,
            topLeft = Offset(center.x + radius * 0.15f, center.y - radius * 1.34f),
            size = Size(radius * 0.78f, radius * 0.38f),
        )
    }
}

private fun DrawScope.drawFlower(center: Offset, radius: Float, petalColor: Color) {
    val directions = listOf(
        Offset(0f, -1.1f),
        Offset(1.02f, -0.35f),
        Offset(0.64f, 0.98f),
        Offset(-0.64f, 0.98f),
        Offset(-1.02f, -0.35f),
    )
    directions.forEach { direction ->
        drawCircle(
            color = petalColor,
            radius = radius * 0.58f,
            center = center + Offset(direction.x * radius * 0.62f, direction.y * radius * 0.62f),
        )
    }
    drawCircle(color = FlowerCenter, radius = radius * 0.34f, center = center)
}

private fun DrawScope.drawCard(
    center: Offset,
    width: Float,
    height: Float,
    fill: Color,
    stroke: Color,
) {
    drawRoundRect(
        color = fill,
        topLeft = Offset(center.x - width / 2f, center.y - height / 2f),
        size = Size(width, height),
        cornerRadius = CornerRadius(height * 0.14f),
    )
    drawRoundRect(
        color = stroke.copy(alpha = 0.55f),
        topLeft = Offset(center.x - width / 2f, center.y - height / 2f),
        size = Size(width, height),
        cornerRadius = CornerRadius(height * 0.14f),
        style = Stroke(width = height * 0.018f),
    )
}

private fun DrawScope.drawLeafTrail(t: Float, playW: Float, h: Float, accent: Color) {
    repeat(3) { index ->
        val x = playW * (0.10f + ((t + index * 0.33f) % 1f) * 0.75f)
        val y = h * (0.26f + sin((t + index * 0.2f) * 2 * PI).toFloat() * 0.06f)
        rotate(degrees = t * 360f + index * 60f, pivot = Offset(x, y)) {
            drawOval(
                color = AppleLeaf.copy(alpha = 0.45f),
                topLeft = Offset(x - h * 0.03f, y - h * 0.015f),
                size = Size(h * 0.06f, h * 0.03f),
            )
        }
    }
    drawSparkle(Offset(playW * 0.92f, h * 0.20f), h * 0.022f, accent)
}

private fun DrawScope.drawButterfly(center: Offset, radius: Float, color: Color) {
    drawOval(
        color = color.copy(alpha = 0.75f),
        topLeft = Offset(center.x - radius * 1.5f, center.y - radius),
        size = Size(radius * 1.2f, radius * 1.8f),
    )
    drawOval(
        color = color.copy(alpha = 0.75f),
        topLeft = Offset(center.x + radius * 0.3f, center.y - radius),
        size = Size(radius * 1.2f, radius * 1.8f),
    )
    drawLine(
        color = GardenGreen,
        start = Offset(center.x, center.y - radius * 0.9f),
        end = Offset(center.x, center.y + radius * 0.9f),
        strokeWidth = radius * 0.18f,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawSparkle(center: Offset, radius: Float, color: Color) {
    drawLine(
        color = color,
        start = Offset(center.x - radius, center.y),
        end = Offset(center.x + radius, center.y),
        strokeWidth = radius * 0.38f,
        cap = StrokeCap.Round,
    )
    drawLine(
        color = color,
        start = Offset(center.x, center.y - radius),
        end = Offset(center.x, center.y + radius),
        strokeWidth = radius * 0.38f,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawBurst(progress: Float, color: Color) {
    if (progress <= 0.01f) return
    val center = Offset(size.width * 0.78f, size.height * 0.52f)
    val radius = size.height * (0.10f + progress * 0.36f)
    repeat(8) { index ->
        val angle = index * (2 * Math.PI / 8).toFloat()
        drawCircle(
            color = color.copy(alpha = (1f - progress) * 0.55f),
            radius = size.height * 0.018f * (1f - progress * 0.5f),
            center = Offset(
                center.x + cos(angle) * radius,
                center.y + sin(angle) * radius,
            ),
        )
    }
}

private fun DrawScope.drawNumber(center: Offset, text: String, radius: Float, color: Color) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color.toArgb()
        textAlign = Paint.Align.CENTER
        textSize = radius * 1.30f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val baseline = center.y - (paint.descent() + paint.ascent()) / 2f
    drawContext.canvas.nativeCanvas.drawText(text, center.x, baseline, paint)
}

private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(),
    (red * 255).toInt(),
    (green * 255).toInt(),
    (blue * 255).toInt(),
)
