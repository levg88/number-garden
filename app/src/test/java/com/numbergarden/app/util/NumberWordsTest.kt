package com.numbergarden.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberWordsTest {

    @Test
    fun `formats zero through ten`() {
        assertEquals("zero", numberWord(0))
        assertEquals("three", numberWord(3))
        assertEquals("ten", numberWord(10))
    }

    @Test
    fun `capitalizes when asked`() {
        assertEquals("Three", numberWord(3, capitalized = true))
    }

    @Test
    fun `falls back to digits outside the word list`() {
        assertEquals("12", numberWord(12))
    }
}
