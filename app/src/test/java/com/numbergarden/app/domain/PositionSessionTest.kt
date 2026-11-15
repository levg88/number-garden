package com.numbergarden.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PositionSessionTest {

    @Test
    fun buildRoundsCreatesThreeItems() {
        val rounds = PositionSession.buildRounds(count = 5, random = Random(21))
        assertEquals(5, rounds.size)
        rounds.forEach { round ->
            assertEquals(listOf(0, 1, 2), round.items.sorted())
            assertTrue(round.target in 0..2)
        }
    }

    @Test
    fun tappingTargetMarksCorrect() {
        val rounds = PositionSession.buildRounds(count = 1, random = Random(22))
        val state = PositionState(rounds = rounds)
        val next = PositionSession.select(state, rounds.first().target)
        assertEquals(PositionPhase.CORRECT, next.phase)
        assertEquals(rounds.first().target, next.selected)
    }

    @Test
    fun twoMissesRevealTheTarget() {
        val rounds = PositionSession.buildRounds(count = 1, random = Random(23))
        val state = PositionState(rounds = rounds)
        val wrong = (0..2).first { it != rounds.first().target }
        val afterFirst = PositionSession.select(state, wrong)
        assertEquals(1, afterFirst.misses)
        val afterSecond = PositionSession.select(afterFirst, wrong)
        assertEquals(PositionPhase.TEACH, afterSecond.phase)
        assertEquals(rounds.first().target, afterSecond.selected)
    }

    @Test
    fun outOfRangeTapIsIgnored() {
        val rounds = PositionSession.buildRounds(count = 1, random = Random(24))
        val state = PositionState(rounds = rounds)
        assertEquals(state, PositionSession.select(state, 7))
    }

    @Test
    fun nextCompletesAfterLastRound() {
        val rounds = PositionSession.buildRounds(count = 2, random = Random(25))
        var state = PositionState(rounds = rounds)
        state = PositionSession.next(state)
        assertEquals(1, state.index)
        state = PositionSession.next(state)
        assertTrue(state.completed)
    }

    @Test
    fun ordinalKeyNamesPositions() {
        assertEquals("first", PositionSession.ordinalKey(0))
        assertEquals("second", PositionSession.ordinalKey(1))
        assertEquals("third", PositionSession.ordinalKey(2))
    }
}
