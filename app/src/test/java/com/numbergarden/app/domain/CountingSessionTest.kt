package com.numbergarden.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class CountingSessionTest {

    private fun rounds(
        values: List<Int> = listOf(3, 1, 2, 5, 4),
        count: Int = 3,
        optionCount: Int = 3,
    ) = CountingSession.buildRounds(values, count, optionCount, Random(42))

    @Test
    fun `builds the requested number of rounds with valid options`() {
        val built = rounds(count = 5)

        assertEquals(5, built.size)
        built.forEach { round ->
            assertEquals(3, round.options.size)
            assertTrue(round.quantity in round.options)
            assertEquals(round.options.sorted(), round.options)
            assertEquals(round.options.distinct(), round.options)
            assertTrue(round.quantity in 1..5)
        }
    }

    @Test
    fun `same seed produces the same rounds`() {
        val first = CountingSession.buildRounds(listOf(1, 2, 3, 4, 5), 4, 3, Random(7))
        val second = CountingSession.buildRounds(listOf(1, 2, 3, 4, 5), 4, 3, Random(7))
        assertEquals(first, second)
    }

    @Test
    fun `a correct choice moves to the positive phase`() {
        val state = CountingState(rounds = rounds())
        val next = CountingSession.select(state, state.round.quantity)

        assertEquals(CountPhase.CORRECT, next.phase)
        assertEquals(state.round.quantity, next.selected)
        assertEquals(0, next.misses)
    }

    @Test
    fun `one wrong choice keeps the round open and adds a hint`() {
        val state = CountingState(rounds = rounds())
        val wrongOption = state.round.options.first { it != state.round.quantity }

        val afterWrong = CountingSession.select(state, wrongOption)

        assertEquals(CountPhase.ASK, afterWrong.phase)
        assertEquals(1, afterWrong.misses)
        assertEquals(1, afterWrong.hintLevel)
        assertFalse(afterWrong.completed)

        val afterCorrect = CountingSession.select(afterWrong, state.round.quantity)
        assertEquals(CountPhase.CORRECT, afterCorrect.phase)
    }

    @Test
    fun `two wrong choices reveal the answer without shaming`() {
        val state = CountingState(rounds = rounds())
        val wrongOption = state.round.options.first { it != state.round.quantity }

        val once = CountingSession.select(state, wrongOption)
        val twice = CountingSession.select(once, wrongOption)

        assertEquals(CountPhase.TEACH, twice.phase)
        assertEquals(2, twice.misses)
        assertEquals(state.round.quantity, twice.selected)
        assertEquals(2, twice.hintLevel)
        // Further taps cannot move the round.
        assertEquals(twice, CountingSession.select(twice, wrongOption))
    }

    @Test
    fun `next advances and completes after the final round`() {
        val state = CountingState(rounds = rounds(count = 2))
        val second = CountingSession.next(state)
        assertEquals(1, second.index)
        assertEquals(CountPhase.ASK, second.phase)

        val complete = CountingSession.next(second)
        assertTrue(complete.completed)
        assertEquals(complete, CountingSession.next(complete))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects an empty content list`() {
        CountingSession.buildRounds(emptyList(), 3, 3, Random(1))
    }

    @Test
    fun `rounds repeat content when a longer session is requested`() {
        val built = rounds(values = listOf(1, 2), count = 5)
        assertEquals(5, built.size)
        assertTrue(built.all { it.quantity in 1..2 })
    }
}
