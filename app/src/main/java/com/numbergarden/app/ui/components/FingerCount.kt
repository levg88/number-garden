package com.numbergarden.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * A calm, animated hand showing [count] raised fingers (0..10). Fingers rise one at a time.
 */
@Composable
fun FingerCount(count: Int, modifier: Modifier = Modifier, color: Color = Color(0xFFE8A07A)) {
    val safe = count.coerceIn(0, 10)
    val anim = remember(safe) { Animatable(0f) }
    LaunchedEffect(safe) {
        anim.animateTo(safe.toFloat(), animationSpec = tween(600))
    }
    val raised = anim.value
    Canvas(modifier = modifier.size(120.dp, 80.dp)) {
        val w = size.width
        val h = size.height
        // palm
        drawRoundRect(
            color = color.copy(alpha = 0.35f),
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.45f),
            size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
        )
        // 5 fingers
        for (i in 0 until 5) {
            val fingerRaised = (raised - i).coerceIn(0f, 1f)
            val x = w * (0.28f + i * 0.11f)
            val fingerHeight = h * (0.15f + 0.35f * fingerRaised)
            drawRoundRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(x, h * 0.45f - fingerHeight),
                size = androidx.compose.ui.geometry.Size(w * 0.08f, fingerHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
            )
        }
        // second hand for 6-10
        if (safe > 5) {
            val extra = (raised - 5).coerceIn(0f, 5f)
            for (i in 0 until 5) {
                val fingerRaised = (extra - i).coerceIn(0f, 1f)
                val x = w * (0.28f + i * 0.11f)
                val fingerHeight = h * (0.15f + 0.35f * fingerRaised)
                drawRoundRect(
                    color = color.copy(alpha = 0.85f),
                    topLeft = androidx.compose.ui.geometry.Offset(x, h * 0.05f - fingerHeight * 0.5f + h * 0.0f),
                    size = androidx.compose.ui.geometry.Size(w * 0.08f, fingerHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                )
            }
        }
    }
}