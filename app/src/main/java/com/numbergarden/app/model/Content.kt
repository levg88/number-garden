package com.numbergarden.app.model

import kotlinx.serialization.Serializable

/**
 * A content pack is the single source of learning content.
 * It is bundled in assets/content.json, versioned, validated at load time,
 * and contains no user data. New packs can be added without code changes.
 */
@Serializable
data class ContentPack(
    val schemaVersion: Int,
    val lessons: List<Lesson>,
    val numberCards: List<NumberCard>,
    val countingValues: List<Int>,
    val compareRounds: List<CompareRound>,
    val maxSessionRounds: Int = 5,
    val optionCount: Int = 3,
)

@Serializable
data class Lesson(
    val id: String,
    val title: String,
    val skill: String,
    val video: String,
    val summary: String,
    val durationSec: Int,
    val captions: List<Caption>,
)

@Serializable
data class Caption(
    val atMs: Int,
    val text: String,
)

@Serializable
data class NumberCard(
    val value: Int,
    val word: String,
    val narration: String,
)

@Serializable
data class CompareRound(
    val left: Int,
    val right: Int,
    val target: CompareTarget,
)

@Serializable
enum class CompareTarget {
    MORE,
    FEWER,
}
