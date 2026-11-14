package com.numbergarden.app.ui.screens

import com.numbergarden.app.model.CompareTarget
import org.junit.Assert.assertEquals
import org.junit.Test

class ComparisonWordPairTest {

    @Test
    fun `more puts the greater quantity first and capitalizes it`() {
        assertEquals("Three" to "one", comparisonWordPair(3, 1, CompareTarget.MORE))
        assertEquals("Four" to "two", comparisonWordPair(2, 4, CompareTarget.MORE))
    }

    @Test
    fun `fewer puts the smaller quantity first and capitalizes it`() {
        assertEquals("One" to "three", comparisonWordPair(3, 1, CompareTarget.FEWER))
        assertEquals("Two" to "four", comparisonWordPair(4, 2, CompareTarget.FEWER))
    }

    @Test
    fun `operand order does not change the sentence`() {
        assertEquals(
            comparisonWordPair(1, 5, CompareTarget.FEWER),
            comparisonWordPair(5, 1, CompareTarget.FEWER),
        )
        assertEquals(
            comparisonWordPair(1, 5, CompareTarget.MORE),
            comparisonWordPair(5, 1, CompareTarget.MORE),
        )
    }
}
