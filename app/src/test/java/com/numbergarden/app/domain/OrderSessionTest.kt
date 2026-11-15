package com.numbergarden.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class OrderSessionTest {

    @Test
    fun buildRoundsCreatesRequestedCount() {
        val rounds = OrderSession.buildRounds(count = 6, random = Random(3))
        assertEquals(6, rounds.size)
        rounds.forEach { round ->
            assertTrue(round.values.size in 3..5)
            assertEquals(round.values.size, round.values.distinct().size)
            assertNotEquals(round.values.sorted(), round.values)
        }
    }

    @Test
    fun choosingSmallestPlacesIt() {
        val rounds = OrderSession.buildRounds(count = 1, random = Random(4))
        val state = OrderState(rounds = rounds)
        val smallest = state.smallest ?: error("round has no values")
        val next = OrderSession.select(state, smallest)
        assertEquals(listOf(smallest), next.placed)
        assertEquals(OrderPhase.ASK, next.phase)
    }

    @Test
    fun placingEveryValueCompletesTheRound() {
        val rounds = OrderSession.buildRounds(count = 1, random = Random(5))
        var state = OrderState(rounds = rounds)
        repeat(rounds.first().values.size) {
            state = OrderSession.select(state, state.smallest ?: error("no remaining value"))
        }
        assertEquals(rounds.first().values.sorted(), state.placed)
        assertEquals(OrderPhase.CORRECT, state.phase)
    }

    @Test
    fun twoMissesTeachTheWholeOrder() {
        val rounds = OrderSession.buildRounds(count = 1, random = Random(6))
        val state = OrderState(rounds = rounds)
        val wrong = state.remaining.first { it != state.smallest }
        val afterFirst = OrderSession.select(state, wrong)
        assertEquals(1, afterFirst.misses)
        val afterSecond = OrderSession.select(afterFirst, wrong)
        assertEquals(OrderPhase.TEACH, afterSecond.phase)
        assertEquals(rounds.first().values.sorted(), afterSecond.placed)
    }

    @Test
    fun correctSelectionResetsMisses() {
        val rounds = OrderSession.buildRounds(count = 1, random = Random(8))
        var state = OrderState(rounds = rounds)
        val wrong = state.remaining.first { it != state.smallest }
        state = OrderSession.select(state, wrong)
        assertEquals(1, state.misses)
        state = OrderSession.select(state, state.smallest ?: error("no remaining value"))
        assertEquals(0, state.misses)
    }

    @Test
    fun nextCompletesAfterLastRound() {
        val rounds = OrderSession.buildRounds(count = 2, random = Random(7))
        var state = OrderState(rounds = rounds)
        state = OrderSession.next(state)
        assertEquals(1, state.index)
        state = OrderSession.next(state)
        assertTrue(state.completed)
    }
}
