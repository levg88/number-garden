package com.numbergarden.app.domain

import com.numbergarden.app.model.CompareRound
import com.numbergarden.app.model.CompareTarget
import kotlin.random.Random

enum class GroupSide {
    LEFT,
    RIGHT,
}

enum class ComparePhase {
    ASK,
    CORRECT,
    TEACH,
}

data class CompareUiRound(
    val left: Int,
    val right: Int,
    val target: CompareTarget,
) {
    fun valueOf(side: GroupSide): Int = if (side == GroupSide.LEFT) left else right

    fun otherOf(side: GroupSide): Int = if (side == GroupSide.LEFT) right else left

    val correctSide: GroupSide
        get() = when (target) {
            CompareTarget.MORE -> if (left > right) GroupSide.LEFT else GroupSide.RIGHT
            CompareTarget.FEWER -> if (left < right) GroupSide.LEFT else GroupSide.RIGHT
        }

    fun isCorrect(side: GroupSide): Boolean = side == correctSide
}

data class CompareState(
    val rounds: List<CompareUiRound>,
    val index: Int = 0,
    val phase: ComparePhase = ComparePhase.ASK,
    val misses: Int = 0,
    val selectedSide: GroupSide? = null,
    val completed: Boolean = false,
) {
    val round: CompareUiRound get() = rounds[index]
    val hintLevel: Int
        get() = when {
            phase == ComparePhase.TEACH -> 2
            misses >= 1 -> 1
            else -> 0
        }
}

object CompareSession {

    fun buildRounds(rounds: List<CompareRound>, count: Int, random: Random): List<CompareUiRound> {
        require(rounds.isNotEmpty()) { "At least one compare round is required" }
        require(count > 0) { "Round count must be positive" }

        val shuffled = rounds.shuffled(random).map {
            CompareUiRound(left = it.left, right = it.right, target = it.target)
        }
        return (0 until count).map { index -> shuffled[index % shuffled.size] }
    }

    fun choose(state: CompareState, side: GroupSide): CompareState {
        if (state.completed || state.phase != ComparePhase.ASK) return state

        return if (state.round.isCorrect(side)) {
            state.copy(phase = ComparePhase.CORRECT, selectedSide = side)
        } else {
            val misses = state.misses + 1
            if (misses >= 2) {
                state.copy(
                    phase = ComparePhase.TEACH,
                    misses = misses,
                    selectedSide = state.round.correctSide,
                )
            } else {
                state.copy(misses = misses)
            }
        }
    }

    fun next(state: CompareState): CompareState {
        if (state.completed) return state
        return if (state.index >= state.rounds.lastIndex) {
            state.copy(completed = true)
        } else {
            state.copy(
                index = state.index + 1,
                phase = ComparePhase.ASK,
                misses = 0,
                selectedSide = null,
            )
        }
    }
}
