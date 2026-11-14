package com.numbergarden.app.domain

import kotlin.random.Random

enum class MathPhase { ASK, CORRECT, TEACH }

data class MathRound(
    val left: Int,
    val right: Int,
    val operation: Int, // 0 = add, 1 = subtract
    val answer: Int,
    val options: List<Int>,
)

data class MathState(
    val rounds: List<MathRound>,
    val index: Int = 0,
    val selected: Int? = null,
    val misses: Int = 0,
    val phase: MathPhase = MathPhase.ASK,
    val completed: Boolean = false,
) {
    val round: MathRound get() = rounds[index]
    val hintLevel: Int
        get() = when {
            phase == MathPhase.TEACH -> 2
            misses >= 1 -> 1
            else -> 0
        }
}

object MathSession {

    fun buildRounds(count: Int, random: Random): List<MathRound> {
        require(count > 0)
        return (0 until count).map {
            val op = random.nextInt(2)
            val (left, right, answer) = if (op == 0) {
                val l = random.nextInt(1, 6)
                val r = random.nextInt(1, 6)
                val a = (l + r).coerceAtMost(10)
                Triple(l, r, a)
            } else {
                val a = random.nextInt(1, 10)
                val r = random.nextInt(1, a)
                val l = a
                Triple(l, r, a - r)
            }
            val distractors = (1..10).filter { it != answer }.shuffled(random).take(2)
            MathRound(
                left = left,
                right = right,
                operation = op,
                answer = answer,
                options = (distractors + answer).sorted(),
            )
        }
    }

    fun select(state: MathState, value: Int): MathState {
        if (state.completed || state.phase != MathPhase.ASK) return state
        return if (value == state.round.answer) {
            state.copy(selected = value, phase = MathPhase.CORRECT)
        } else {
            val misses = state.misses + 1
            if (misses >= 2) {
                state.copy(selected = state.round.answer, misses = misses, phase = MathPhase.TEACH)
            } else {
                state.copy(misses = misses)
            }
        }
    }

    fun next(state: MathState): MathState {
        if (state.completed) return state
        return if (state.index >= state.rounds.lastIndex) {
            state.copy(completed = true)
        } else {
            state.copy(index = state.index + 1, selected = null, misses = 0, phase = MathPhase.ASK)
        }
    }
}