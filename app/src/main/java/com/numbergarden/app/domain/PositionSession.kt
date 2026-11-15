package com.numbergarden.app.domain

import kotlin.random.Random

enum class PositionPhase {
    ASK,
    CORRECT,
    TEACH,
}

/**
 * Ordinal language: first, second, third. Three garden friends stand in a row
 * and the child taps the friend in the requested position.
 */
data class PositionRound(
    val items: List<Int>,
    val target: Int,
)

data class PositionState(
    val rounds: List<PositionRound>,
    val index: Int = 0,
    val selected: Int? = null,
    val misses: Int = 0,
    val phase: PositionPhase = PositionPhase.ASK,
    val completed: Boolean = false,
) {
    val round: PositionRound get() = rounds[index]

    val hintLevel: Int
        get() = when {
            phase == PositionPhase.TEACH -> 2
            misses >= 1 -> 1
            else -> 0
        }
}

object PositionSession {

    fun buildRounds(count: Int, random: Random): List<PositionRound> {
        require(count > 0) { "Round count must be positive" }
        return (0 until count).map {
            PositionRound(
                items = listOf(0, 1, 2).shuffled(random),
                target = random.nextInt(3),
            )
        }
    }

    fun select(state: PositionState, position: Int): PositionState {
        if (state.completed || state.phase != PositionPhase.ASK) return state
        if (position !in state.round.items.indices) return state
        return if (position == state.round.target) {
            state.copy(selected = position, phase = PositionPhase.CORRECT)
        } else {
            val misses = state.misses + 1
            if (misses >= 2) {
                state.copy(
                    selected = state.round.target,
                    misses = misses,
                    phase = PositionPhase.TEACH,
                )
            } else {
                state.copy(misses = misses)
            }
        }
    }

    fun next(state: PositionState): PositionState {
        if (state.completed) return state
        return if (state.index >= state.rounds.lastIndex) {
            state.copy(completed = true)
        } else {
            state.copy(
                index = state.index + 1,
                selected = null,
                misses = 0,
                phase = PositionPhase.ASK,
            )
        }
    }

    fun ordinalKey(position: Int): String = when (position) {
        0 -> "first"
        1 -> "second"
        else -> "third"
    }
}
