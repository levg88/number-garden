package com.numbergarden.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.Role
import com.numbergarden.app.R
import com.numbergarden.app.ui.theme.AppleLeaf
import com.numbergarden.app.ui.theme.AppleRed
import com.numbergarden.app.ui.theme.AppleRedDark
import com.numbergarden.app.ui.theme.AppleStem
import com.numbergarden.app.ui.theme.FlowerAmber
import com.numbergarden.app.ui.theme.FlowerCenter
import com.numbergarden.app.ui.theme.FlowerLeaf
import com.numbergarden.app.ui.theme.FlowerRose
import com.numbergarden.app.ui.theme.FlowerSky
import com.numbergarden.app.ui.theme.LocalHighContrast

@Composable
fun BigActionTile(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    art: @Composable () -> Unit,
) {
    val highContrast = LocalHighContrast.current
    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 164.dp)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(modifier = Modifier.height(62.dp), contentAlignment = Alignment.CenterStart) {
                art()
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
fun AppleGlyph(size: Dp, modifier: Modifier = Modifier) {
    AppleCanvas(size = size, modifier = modifier)
}

@Composable
fun AppleArt(
    size: Dp,
    countedOrdinal: Int?,
    contentDescription: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val highContrast = LocalHighContrast.current
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            onClickLabel = stringResource(R.string.count_tap_hint),
            role = Role.Button,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(clickModifier)
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription
                if (onClick != null) role = Role.Button
            },
        contentAlignment = Alignment.Center,
    ) {
        AppleCanvas(size = size)
        if (countedOrdinal != null) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.White.copy(alpha = if (highContrast) 1f else 0.92f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = countedOrdinal.toString(),
                    color = AppleRedDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics(mergeDescendants = true) {},
                )
            }
        }
    }
}

@Composable
fun AppleCanvas(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension * 0.36f
        val center = Offset(this.size.width / 2f, this.size.height * 0.56f)
        val stemTop = Offset(center.x + radius * 0.06f, center.y - radius * 0.95f)
        val stemBottom = Offset(center.x + radius * 0.14f, center.y - radius * 1.22f)

        drawCircle(
            color = AppleRedDark,
            radius = radius,
            center = center + Offset(0f, radius * 0.06f),
        )
        drawCircle(color = AppleRed, radius = radius, center = center)
        drawCircle(
            color = Color.White.copy(alpha = 0.14f),
            radius = radius * 0.46f,
            center = center - Offset(radius * 0.32f, radius * 0.34f),
        )
        drawLine(
            color = AppleStem,
            start = stemTop,
            end = stemBottom,
            strokeWidth = radius * 0.16f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
        )
        val leafPivot = Offset(stemBottom.x + radius * 0.1f, stemBottom.y + radius * 0.08f)
        rotate(degrees = -28f, pivot = leafPivot) {
            drawOval(
                color = AppleLeaf,
                topLeft = Offset(leafPivot.x, leafPivot.y - radius * 0.18f),
                size = Size(radius * 0.85f, radius * 0.42f),
            )
        }
    }
}

@Composable
fun WatchArt(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Canvas(modifier = modifier.size(width = 82.dp, height = 58.dp)) {
        drawRoundRect(
            color = scheme.primary,
            cornerRadius = CornerRadius(16f, 16f),
            size = Size(size.width, size.height),
        )
        val triangle = Path().apply {
            moveTo(size.width * 0.43f, size.height * 0.30f)
            lineTo(size.width * 0.43f, size.height * 0.70f)
            lineTo(size.width * 0.70f, size.height * 0.50f)
            close()
        }
        drawPath(triangle, color = scheme.background)
    }
}

@Composable
fun CountArt(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) {
            AppleGlyph(size = 30.dp)
        }
    }
}

@Composable
fun CompareArt(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) {
                Box(Modifier.size(13.dp).background(AppleRed, CircleShape))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(Modifier.size(13.dp).background(AppleRed, CircleShape))
        }
        Box(
            Modifier
                .width(60.dp)
                .height(2.dp)
                .background(scheme.outlineVariant),
        )
    }
}

@Composable
fun NumbersArt(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf("1", "2", "3").forEach { numeral ->
            Text(
                text = numeral,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics(mergeDescendants = true) {},
            )
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun AppleField(
    count: Int,
    modifier: Modifier = Modifier,
    appleSize: Dp = 56.dp,
    countedOrdinals: List<Int> = emptyList(),
    showSequenceHints: Boolean = false,
    onAppleClick: ((Int) -> Unit)? = null,
) {
    var tappedIndex by remember { mutableIntStateOf(-1) }
    val tapScale = remember { Animatable(1f) }
    LaunchedEffect(tappedIndex) {
        if (tappedIndex >= 0) {
            tapScale.snapTo(1f)
            tapScale.animateTo(1.18f, animationSpec = tween(120))
            tapScale.animateTo(1f, animationSpec = tween(120))
        }
    }
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 5,
    ) {
        repeat(count) { index ->
            val ordinal = countedOrdinals.indexOf(index).takeIf { it >= 0 }?.plus(1)
            val appear = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                appear.animateTo(1f, animationSpec = tween(420, delayMillis = index * 70))
            }
            val tap = if (index == tappedIndex) tapScale.value else 1f
            val scale = appear.value * tap
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppleArt(
                    size = appleSize,
                    countedOrdinal = ordinal,
                    contentDescription = if (ordinal != null) {
                        stringResource(R.string.cd_counted_apple, ordinal)
                    } else {
                        stringResource(R.string.cd_apple)
                    },
                    onClick = onAppleClick?.let { click ->
                        {
                            tappedIndex = index
                            click(index)
                        }
                    },
                    modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale),
                )
                if (showSequenceHints) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(appleSize)
                            .semantics { contentDescription = (index + 1).toString() },
                    )
                }
            }
        }
    }
}

@Composable
fun NumberButton(
    value: Int,
    onClick: () -> Unit,
    enabled: Boolean = true,
    emphasized: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val highContrast = LocalHighContrast.current
    val scheme = MaterialTheme.colorScheme
    val chooseLabel = stringResource(R.string.cd_choose_number, value)
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .heightIn(min = 88.dp)
            .semantics { role = Role.Button },
        shape = MaterialTheme.shapes.large,
        color = if (emphasized) scheme.primaryContainer else scheme.surface,
        border = BorderStroke(
            width = if (emphasized || highContrast) 3.dp else 1.dp,
            color = when {
                emphasized -> scheme.primary
                highContrast -> scheme.outline
                else -> scheme.outlineVariant
            },
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.displayMedium,
                color = scheme.onSurface,
                modifier = Modifier.semantics {
                    contentDescription = chooseLabel
                },
            )
        }
    }
}

@Composable
fun TenFrame(
    filled: Int,
    modifier: Modifier = Modifier,
    cellSize: Dp = 46.dp,
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(2) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(5) { column ->
                    val number = row * 5 + column + 1
                    val isFilled = number <= filled
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isFilled) {
                                    scheme.primaryContainer
                                } else {
                                    scheme.surface
                                },
                            )
                            .border(
                                width = 1.dp,
                                color = scheme.outlineVariant,
                                shape = RoundedCornerShape(10.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isFilled) {
                            AppleGlyph(size = cellSize * 0.62f)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlowerArt(
    bloomed: Boolean,
    size: Dp,
    modifier: Modifier = Modifier,
    petalColor: Color = FlowerRose,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stemX = w * 0.5f
        val bloomCenter = Offset(stemX, h * 0.40f)
        val petalRadius = w * 0.15f

        drawLine(
            color = FlowerLeaf,
            start = Offset(stemX, h * 0.98f),
            end = Offset(stemX, h * 0.46f),
            strokeWidth = w * 0.035f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
        )
        drawOval(
            color = FlowerLeaf.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.50f, h * 0.64f),
            size = Size(w * 0.24f, h * 0.12f),
        )
        drawOval(
            color = FlowerLeaf.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.27f, h * 0.74f),
            size = Size(w * 0.24f, h * 0.12f),
        )

        if (bloomed) {
            val petals = listOf(
                Offset(0f, -1.15f),
                Offset(1.05f, -0.36f),
                Offset(0.66f, 1.0f),
                Offset(-0.66f, 1.0f),
                Offset(-1.05f, -0.36f),
            )
            petals.forEach { direction ->
                drawCircle(
                    color = petalColor,
                    radius = petalRadius,
                    center = bloomCenter + Offset(
                        direction.x * petalRadius * 1.15f,
                        direction.y * petalRadius * 1.15f,
                    ),
                )
            }
            drawCircle(color = FlowerCenter, radius = petalRadius * 0.68f, center = bloomCenter)
        } else {
            drawOval(
                color = FlowerLeaf,
                topLeft = Offset(w * 0.35f, h * 0.40f),
                size = Size(w * 0.3f, h * 0.1f),
            )
        }
    }
}

fun flowerColor(index: Int): Color = when (index % 3) {
    0 -> FlowerRose
    1 -> FlowerAmber
    else -> FlowerSky
}

@Composable
fun GardenPlot(bloomed: Boolean, index: Int, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val motionAllowed = rememberMotionAllowed(false)
    val scale = remember(index) { Animatable(if (bloomed) 1f else 1f) }
    LaunchedEffect(bloomed) {
        if (bloomed && motionAllowed) {
            scale.snapTo(0.4f)
            scale.animateTo(1f, animationSpec = tween(600))
        } else {
            scale.snapTo(1f)
        }
    }
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .semantics {
                contentDescription = if (bloomed) {
                    "Flower ${index + 1}"
                } else {
                    "Empty plot ${index + 1}"
                }
            },
        shape = MaterialTheme.shapes.medium,
        color = scheme.surfaceContainerLow,
        border = BorderStroke(1.dp, scheme.outlineVariant),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            FlowerArt(
                bloomed = bloomed,
                size = 82.dp,
                petalColor = flowerColor(index),
                modifier = Modifier.graphicsLayer(scaleX = scale.value, scaleY = scale.value),
            )
        }
    }
}

@Composable
fun GardenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = true,
    enabled: Boolean = true,
) {
    if (primary) {
        androidx.compose.material3.Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.heightIn(min = 64.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    } else {
        androidx.compose.material3.OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.heightIn(min = 64.dp),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun LottieArt(
    @androidx.annotation.RawRes resId: Int,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    LottieAnimation(
        composition = composition,
        iterations = iterations,
        modifier = modifier,
    )
}
