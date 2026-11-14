package com.numbergarden.app.model

/**
 * Pure, framework-free validation so the content pipeline can be tested on the JVM.
 */
object ContentValidator {

    fun validate(pack: ContentPack): List<String> = buildList {
        if (pack.schemaVersion != 1) {
            add("schemaVersion must be 1, was ${pack.schemaVersion}")
        }
        if (pack.lessons.isEmpty()) add("At least one lesson is required")
        if (pack.lessons.map { it.id }.distinct().size != pack.lessons.size) {
            add("Lesson ids must be unique")
        }

        val videoPattern = Regex("^videos/[a-z0-9_]+\\.mp4$")
        pack.lessons.forEach { lesson ->
            val label = "lesson '${lesson.id}'"
            if (lesson.id.isBlank()) add("A lesson id is blank")
            if (lesson.title.isBlank()) add("$label has no title")
            if (lesson.summary.isBlank()) add("$label has no summary")
            if (!videoPattern.matches(lesson.video)) {
                add("$label video must match 'videos/name.mp4'")
            }
            if (lesson.durationSec !in 5..300) add("$label duration must be 5..300 seconds")
            if (lesson.captions.isEmpty()) add("$label needs at least one caption")
            if (lesson.captions.map { it.atMs } != lesson.captions.map { it.atMs }.sorted()) {
                add("$label captions must be in time order")
            }
            lesson.captions.forEach { caption ->
                if (caption.text.isBlank()) add("$label has a blank caption")
                if (caption.atMs < 0 || caption.atMs >= lesson.durationSec * 1000) {
                    add("$label caption at ${caption.atMs}ms is outside the video")
                }
            }
        }

        if (pack.numberCards.size != 10) add("Exactly 10 number cards are required")
        if (pack.numberCards.map { it.value }.sorted() != (1..10).toList()) {
            add("Number cards must cover 1 through 10 exactly")
        }
        pack.numberCards.forEach { card ->
            if (card.word.isBlank()) add("Number card ${card.value} has no word")
            if (card.narration.isBlank()) add("Number card ${card.value} has no narration")
        }

        if (pack.countingValues.isEmpty()) add("countingValues cannot be empty")
        if (pack.countingValues.any { it !in 1..10 }) {
            add("countingValues must be between 1 and 10")
        }
        if (pack.maxSessionRounds !in 1..100) add("maxSessionRounds must be 1..100")
        if (pack.optionCount !in 2..4) add("optionCount must be 2..4")

        if (pack.compareRounds.isEmpty()) add("compareRounds cannot be empty")
        pack.compareRounds.forEachIndexed { index, round ->
            if (round.left !in 1..10 || round.right !in 1..10) {
                add("Compare round $index must use 1..10 apples")
            }
            if (round.left == round.right) {
                add("Compare round $index must not have equal groups")
            }
        }

        val largestGroup = maxOf(
            pack.countingValues.maxOrNull() ?: 5,
            pack.compareRounds.maxOfOrNull { maxOf(it.left, it.right) } ?: 5,
            5,
        )
        if (pack.optionCount - 1 > largestGroup - 1) {
            add("optionCount needs more distinct numbers than the content provides")
        }
    }
}
