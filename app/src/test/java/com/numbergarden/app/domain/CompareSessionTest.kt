package com.numbergarden.app.domain

import com.numbergarden.app.model.CompareRound
import com.numbergarden.app.model.CompareTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class CompareSessionTest {

    private val content = listOf(
        CompareRound(left = 3, right = 1, target = CompareTarget.MORE),
        CompareRound(left = 1, right = 3, target = CompareTarget.FEWER),
        CompareRound(left = 4, right = 2, target = CompareTarget.MORE),
    )

    @Test
    fun `builds the requested number of compare rounds`() {
        val rounds = CompareSession.buildRounds(content, 5, Random(3))
        assertEquals(5, rounds.size)
        rounds.forEach { round ->
            assertTrue(round.left != round.right)
            assertTrue(round.left in 1..10)
            assertTrue(round.right in 1..10)
        }
    }

    @Test
    fun `more rounds point at the larger group`() {
        val round = CompareUiRound(3, 1, CompareTarget.MORE)
        assertEquals(GroupSide.LEFT, round.correctSide)
        assertTrue(round.isCorrect(GroupSide.LEFT))
        assertFalse(round.isCorrect(GroupSide.RIGHT))
    }

    @Test
    fun `fewer rounds point at the smaller group`() {
        val round = CompareUiRound(3, 1, CompareTarget.FEWER)
        assertEquals(GroupSide.RIGHT, round.correctSide)
        assertTrue(round.isCorrect(GroupSide.RIGHT))
    }

    @Test
    fun `a correct choice is celebrated`() {
        val state = CompareState(rounds = listOf(CompareUiRound(2, 4, CompareTarget.MORE)))
        val chosen = CompareSession.choose(state, GroupSide.RIGHT)

        assertEquals(ComparePhase.CORRECT, chosen.phase)
        assertEquals(GroupSide.RIGHT, chosen.selectedSide)
        assertEquals(0, chosen.misses)
    }

    @Test
    fun `two mistakes reveal the correct group`() {
        val state = CompareState(rounds = listOf(CompareUiRound(5, 2, CompareTarget.MORE)))
        val once = CompareSession.choose(state, GroupSide.RIGHT)
        assertEquals(ComparePhase.ASK, once.phase)
        assertEquals(1, once.misses)

        val twice = CompareSession.choose(once, GroupSide.RIGHT)
        assertEquals(ComparePhase.TEACH, twice.phase)
        assertEquals(GroupSide.LEFT, twice.selectedSide)
    }

    @Test
    fun `next completes after the final round`() {
        val state = CompareState(rounds = listOf(CompareUiRound(2, 1, CompareTarget.MORE)))
        val complete = CompareSession.next(state)
        assertTrue(complete.completed)
        assertEquals(complete, CompareSession.next(complete))
    }
}
