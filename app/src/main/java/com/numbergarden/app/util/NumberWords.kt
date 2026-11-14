package com.numbergarden.app.util

private val words = listOf(
    "zero",
    "one",
    "two",
    "three",
    "four",
    "five",
    "six",
    "seven",
    "eight",
    "nine",
    "ten",
)

fun numberWord(value: Int, capitalized: Boolean = false): String {
    val word = words.getOrElse(value) { value.toString() }
    return if (capitalized) word.replaceFirstChar { it.uppercaseChar() } else word
}
