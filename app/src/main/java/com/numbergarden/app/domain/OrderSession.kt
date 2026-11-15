package com.numbergarden.app.domain

import kotlin.random.Random

enum class OrderPhase {
    ASK,
    CORRECT,
    TEACH,
}

/**
 * Put a small set of numbers in order by repeatedly choosing the smallest one.
 * The child never has to drag: every round is a sequence of calm taps.
 */
data class OrderRound(
    val values: List<Int>,
)

data class OrderState(
    val rounds: List<OrderRound>,
    val index: Int = 0,
    val placed: List<Int> = emptyList(),
    val misses: Int = 0,
    val phase: OrderPhase = OrderPhase.ASK,
    val completed: Boolean = false,
) {
    val round: OrderRound get() = rounds[index]

    val remaining: List<Int>
        get() = round.values.filterNot { it in placed }

    val smallest: Int? get() = remaining.minOrNull()

    val hintLevel: Int
        get() = when {
            phase == OrderPhase.TEACH -> 2
            misses >= 1 -> 1
            else -> 0
        }
}

object OrderSession {

    fun buildRounds(count: Int, random: Random): List<OrderRound> {
        require(count > 0) { "Round count must be positive" }
        return (0 until count).map {
            val size = random.nextInt(3, 6)
            val start = random.nextInt(1, 7 - size)
            val ascending = (start until start + size).toList()
            var values = ascending.shuffled(random)
            if (values == ascending) values = values.reversed()
            OrderRound(values = values)
        }
    }

    fun select(state: OrderState, value: Int): OrderState {
        if (state.completed || state.phase != OrderPhase.ASK) return state
        val target = state.smallest ?: return state
        if (value != target) {
            val misses = state.misses + 1
            return if (misses >= 2) {
                state.copy(
                    placed = state.round.values.sorted(),
                    misses = misses,
                    phase = OrderPhase.TEACH,
                )
            } else {
                state.copy(misses = misses)
            }
        }
        val placed = state.placed + value
        val done = placed.size == state.round.values.size
        return state.copy(
            placed = placed,
            misses = 0,
            phase = if (done) OrderPhase.CORRECT else OrderPhase.ASK,
        )
    }

    fun next(state: OrderState): OrderState {
        if (state.completed) return state
        return if (state.index >= state.rounds.lastIndex) {
            state.copy(completed = true)
        } else {
            state.copy(
                index = state.index + 1,
                placed = emptyList(),
                misses = 0,
                phase = OrderPhase.ASK,
            )
        }
    }
}
