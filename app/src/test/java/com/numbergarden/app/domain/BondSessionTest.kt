package com.numbergarden.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class BondSessionTest {

    @Test
    fun buildRoundsCreatesValidBonds() {
        val rounds = BondSession.buildRounds(count = 8, random = Random(11))
        assertEquals(8, rounds.size)
        rounds.forEach { round ->
            assertTrue(round.target == 5 || round.target == 10)
            assertEquals(round.target, round.left + round.answer)
            assertEquals(3, round.options.size)
            assertEquals(3, round.options.distinct().size)
            assertTrue(round.answer in round.options)
            assertTrue(round.options.all { it in 1 until round.target })
        }
    }

    @Test
    fun correctAnswerMarksCorrect() {
        val rounds = BondSession.buildRounds(count = 1, random = Random(12))
        val state = BondState(rounds = rounds)
        val next = BondSession.select(state, rounds.first().answer)
        assertEquals(BondPhase.CORRECT, next.phase)
        assertEquals(rounds.first().answer, next.selected)
    }

    @Test
    fun twoMissesRevealTheAnswer() {
        val rounds = BondSession.buildRounds(count = 1, random = Random(13))
        val state = BondState(rounds = rounds)
        val wrong = rounds.first().options.first { it != rounds.first().answer }
        val afterFirst = BondSession.select(state, wrong)
        assertEquals(1, afterFirst.misses)
        val afterSecond = BondSession.select(afterFirst, wrong)
        assertEquals(BondPhase.TEACH, afterSecond.phase)
        assertEquals(rounds.first().answer, afterSecond.selected)
    }

    @Test
    fun nextCompletesAfterLastRound() {
        val rounds = BondSession.buildRounds(count = 2, random = Random(14))
        var state = BondState(rounds = rounds)
        state = BondSession.next(state)
        assertEquals(1, state.index)
        assertEquals(BondPhase.ASK, state.phase)
        state = BondSession.next(state)
        assertTrue(state.completed)
    }
}
