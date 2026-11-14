package com.numbergarden.app.ui.screens

import android.os.SystemClock
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.numbergarden.app.R
import com.numbergarden.app.ui.components.GardenButton
import com.numbergarden.app.ui.components.GardenScaffold
import com.numbergarden.app.ui.components.NumberButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

private data class GateQuestion(
    val a: Int,
    val b: Int,
    val answer: Int,
    val options: List<Int>,
)

private fun newQuestion(random: Random = Random.Default): GateQuestion {
    val pairs = listOf(6 to 3, 7 to 4, 8 to 4, 9 to 3)
    val (a, b) = pairs[random.nextInt(pairs.size)]
    val answer = a + b
    return GateQuestion(
        a = a,
        b = b,
        answer = answer,
        options = listOf(answer - 1, answer, answer + 1).sorted(),
    )
}

/**
 * A parental gate, not a security boundary. It adds friction for a child while
 * remaining usable for an adult, including one using a screen reader.
 */
@Composable
fun GrownUpsGateScreen(
    onPass: () -> Unit,
    onBack: () -> Unit,
) {
    var step by remember { mutableIntStateOf(0) }
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var holding by remember { mutableStateOf(false) }
    var holdJob by remember { mutableStateOf<Job?>(null) }
    var question by remember { mutableStateOf(newQuestion()) }
    var wrong by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val holdLabel = stringResource(R.string.grownups_hold)
    val holdingLabel = stringResource(R.string.grownups_holding)
    val useQuestionLabel = stringResource(R.string.grownups_use_question)
    // The hold control is a fixed circle; grow it so the label still fits at 200% text.
    val largeText = LocalDensity.current.fontScale >= 1.5f
    val holdDiameter = if (largeText) 288.dp else 216.dp
    val ringDiameter = holdDiameter - 28.dp
    val holdLabelPadding = if (largeText) 36.dp else 34.dp

    GardenScaffold(title = stringResource(R.string.grownups_title), onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        ) {
            if (step == 0) {
                Text(
                    text = stringResource(R.string.grownups_intro),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                val description = if (holding) holdingLabel else holdLabel
                Surface(
                    modifier = Modifier
                        .size(holdDiameter)
                        .semantics {
                            role = Role.Button
                            contentDescription = description
                            onClick(label = useQuestionLabel) {
                                step = 1
                                true
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    holding = true
                                    holdJob?.cancel()
                                    holdJob = scope.launch {
                                        val start = SystemClock.elapsedRealtime()
                                        while (isActive) {
                                            val elapsed = SystemClock.elapsedRealtime() - start
                                            holdProgress = (elapsed / HOLD_MS).coerceIn(0f, 1f)
                                            if (elapsed >= HOLD_MS) {
                                                step = 1
                                                break
                                            }
                                            delay(40)
                                        }
                                        holding = false
                                        holdProgress = 0f
                                    }
                                    tryAwaitRelease()
                                    holdJob?.cancel()
                                    holding = false
                                    holdProgress = 0f
                                },
                            )
                        },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { holdProgress },
                            modifier = Modifier.size(ringDiameter),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outlineVariant,
                            strokeWidth = 8.dp,
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = holdLabelPadding),
                        )
                    }
                }

                GardenButton(
                    text = stringResource(R.string.grownups_use_question),
                    onClick = { step = 1 },
                    primary = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Text(
                    text = stringResource(R.string.grownups_question, question.a, question.b),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    question.options.forEach { option ->
                        NumberButton(
                            value = option,
                            onClick = {
                                if (option == question.answer) {
                                    onPass()
                                } else {
                                    wrong = true
                                    question = newQuestion()
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                if (wrong) {
                    Text(
                        text = stringResource(R.string.grownups_wrong),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private const val HOLD_MS = 3000f
