package com.numbergarden.app.domain

import kotlin.random.Random

enum class BondPhase {
    ASK,
    CORRECT,
    TEACH,
}

/**
 * Number bonds: one flower sits at the top, two leaves below. Given one leaf,
 * the child chooses the partner that makes the target.
 */
data class BondRound(
    val target: Int,
    val left: Int,
    val answer: Int,
    val options: List<Int>,
)

data class BondState(
    val rounds: List<BondRound>,
    val index: Int = 0,
    val selected: Int? = null,
    val misses: Int = 0,
    val phase: BondPhase = BondPhase.ASK,
    val completed: Boolean = false,
) {
    val round: BondRound get() = rounds[index]

    val hintLevel: Int
        get() = when {
            phase == BondPhase.TEACH -> 2
            misses >= 1 -> 1
            else -> 0
        }
}

object BondSession {

    fun buildRounds(count: Int, random: Random): List<BondRound> {
        require(count > 0) { "Round count must be positive" }
        return (0 until count).map {
            val target = if (random.nextBoolean()) 5 else 10
            val left = random.nextInt(1, target)
            val answer = target - left
            val distractors = (1 until target)
                .filter { it != answer }
                .shuffled(random)
                .take(2)
            BondRound(
                target = target,
                left = left,
                answer = answer,
                options = (distractors + answer).sorted(),
            )
        }
    }

    fun select(state: BondState, value: Int): BondState {
        if (state.completed || state.phase != BondPhase.ASK) return state
        return if (value == state.round.answer) {
            state.copy(selected = value, phase = BondPhase.CORRECT)
        } else {
            val misses = state.misses + 1
            if (misses >= 2) {
                state.copy(
                    selected = state.round.answer,
                    misses = misses,
                    phase = BondPhase.TEACH,
                )
            } else {
                state.copy(misses = misses)
            }
        }
    }

    fun next(state: BondState): BondState {
        if (state.completed) return state
        return if (state.index >= state.rounds.lastIndex) {
            state.copy(completed = true)
        } else {
            state.copy(
                index = state.index + 1,
                selected = null,
                misses = 0,
                phase = BondPhase.ASK,
            )
        }
    }
}
