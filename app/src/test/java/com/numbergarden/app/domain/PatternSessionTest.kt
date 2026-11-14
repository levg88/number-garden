package com.numbergarden.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PatternSessionTest {

    @Test
    fun buildRoundsCreatesRequestedCount() {
        val rounds = PatternSession.buildRounds(count = 5, random = Random(1))
        assertEquals(5, rounds.size)
        rounds.forEach { round ->
            assertEquals(2, round.options.size)
            assertTrue(round.correct in round.options)
        }
    }

    @Test
    fun correctSelectionMarksCorrect() {
        val rounds = PatternSession.buildRounds(count = 1, random = Random(1))
        val state = PatternState(rounds = rounds)
        val next = PatternSession.select(state, rounds.first().correct)
        assertEquals(PatternPhase.CORRECT, next.phase)
    }

    @Test
    fun twoMissesTeachAnswer() {
        val rounds = PatternSession.buildRounds(count = 1, random = Random(1))
        val state = PatternState(rounds = rounds)
        val wrong = rounds.first().options.first { it != rounds.first().correct }
        val afterFirst = PatternSession.select(state, wrong)
        val afterSecond = PatternSession.select(afterFirst, wrong)
        assertEquals(PatternPhase.TEACH, afterSecond.phase)
        assertEquals(rounds.first().correct, afterSecond.selected)
    }

    @Test
    fun nextCompletesAfterLast() {
        val rounds = PatternSession.buildRounds(count = 2, random = Random(1))
        var state = PatternState(rounds = rounds)
        state = PatternSession.select(state, rounds[0].correct)
        state = PatternSession.next(state)
        state = PatternSession.select(state, rounds[1].correct)
        state = PatternSession.next(state)
        assertTrue(state.completed)
    }
}