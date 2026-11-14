package com.numbergarden.app.domain

import kotlin.random.Random

enum class PatternPhase {
    ASK,
    CORRECT,
    TEACH,
}

/**
 * A pattern round shows a short sequence of two shapes and asks for the next one.
 * Shapes are encoded as 0 = apple and 1 = flower so the engine stays testable
 * without any Compose dependency.
 */
data class PatternRound(
    val sequence: List<Int>,
    val options: List<Int>,
    val correct: Int,
)

data class PatternState(
    val rounds: List<PatternRound>,
    val index: Int = 0,
    val selected: Int? = null,
    val misses: Int = 0,
    val phase: PatternPhase = PatternPhase.ASK,
    val completed: Boolean = false,
) {
    val round: PatternRound get() = rounds[index]

    val hintLevel: Int
        get() = when {
            phase == PatternPhase.TEACH -> 2
            misses >= 1 -> 1
            else -> 0
        }
}

object PatternSession {

    fun buildRounds(count: Int, random: Random): List<PatternRound> {
        require(count > 0) { "Round count must be positive" }
        return (0 until count).map { index ->
            val patternType = random.nextInt(2)
            val (sequence, correct) = when (patternType) {
                0 -> {
                    // Apple, flower, apple, flower -> apple
                    listOf(0, 1, 0, 1) to 0
                }
                else -> {
                    // Apple, apple, flower, apple, apple -> flower
                    listOf(0, 0, 1, 0, 0) to 1
                }
            }
            PatternRound(
                sequence = sequence,
                options = listOf(0, 1),
                correct = correct,
            )
        }
    }

    fun select(state: PatternState, value: Int): PatternState {
        if (state.completed || state.phase != PatternPhase.ASK) return state
        return if (value == state.round.correct) {
            state.copy(selected = value, phase = PatternPhase.CORRECT)
        } else {
            val misses = state.misses + 1
            if (misses >= 2) {
                state.copy(
                    selected = state.round.correct,
                    misses = misses,
                    phase = PatternPhase.TEACH,
                )
            } else {
                state.copy(misses = misses)
            }
        }
    }

    fun next(state: PatternState): PatternState {
        if (state.completed) return state
        return if (state.index >= state.rounds.lastIndex) {
            state.copy(completed = true)
        } else {
            state.copy(
                index = state.index + 1,
                selected = null,
                misses = 0,
                phase = PatternPhase.ASK,
            )
        }
    }
}