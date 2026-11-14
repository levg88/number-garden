package com.numbergarden.app.domain

import kotlin.random.Random

enum class CountPhase {
    ASK,
    CORRECT,
    TEACH,
}

data class CountingRound(
    val quantity: Int,
    val options: List<Int>,
)

data class CountingState(
    val rounds: List<CountingRound>,
    val index: Int = 0,
    val selected: Int? = null,
    val misses: Int = 0,
    val phase: CountPhase = CountPhase.ASK,
    val completed: Boolean = false,
) {
    val round: CountingRound get() = rounds[index]

    val hintLevel: Int
        get() = when {
            phase == CountPhase.TEACH -> 2
            misses >= 1 -> 1
            else -> 0
        }
}

object CountingSession {

    fun buildRounds(
        values: List<Int>,
        count: Int,
        optionCount: Int,
        random: Random,
    ): List<CountingRound> {
        require(values.isNotEmpty()) { "At least one counting value is required" }
        require(count > 0) { "Round count must be positive" }
        require(optionCount in 2..4) { "Option count must be 2..4" }

        val shuffledValues = values.shuffled(random)
        val largestOption = maxOf(5, values.max())
        return (0 until count).map { index ->
            val quantity = shuffledValues[index % shuffledValues.size]
            val distractors = (1..largestOption)
                .filter { it != quantity }
                .shuffled(random)
                .take(optionCount - 1)
            CountingRound(
                quantity = quantity,
                options = (distractors + quantity).distinct().sorted(),
            )
        }
    }

    fun select(state: CountingState, value: Int): CountingState {
        if (state.completed || state.phase != CountPhase.ASK) return state

        return if (value == state.round.quantity) {
            state.copy(selected = value, phase = CountPhase.CORRECT)
        } else {
            val misses = state.misses + 1
            if (misses >= 2) {
                // Errorless scaffolding: after two attempts the answer is shown gently.
                state.copy(
                    selected = state.round.quantity,
                    misses = misses,
                    phase = CountPhase.TEACH,
                )
            } else {
                state.copy(misses = misses)
            }
        }
    }

    fun next(state: CountingState): CountingState {
        if (state.completed) return state
        return if (state.index >= state.rounds.lastIndex) {
            state.copy(completed = true)
        } else {
            state.copy(
                index = state.index + 1,
                selected = null,
                misses = 0,
                phase = CountPhase.ASK,
            )
        }
    }
}
