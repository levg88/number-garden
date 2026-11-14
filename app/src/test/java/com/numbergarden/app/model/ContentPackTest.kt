package com.numbergarden.app.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ContentPackTest {

    private val contentFile = File("src/main/assets/content.json")

    private fun loadPack(): ContentPack {
        assertTrue("Bundled content.json is missing", contentFile.exists())
        return Json.decodeFromString(
            ContentPack.serializer(),
            contentFile.readText(),
        )
    }

    @Test
    fun `bundled content is valid`() {
        val errors = ContentValidator.validate(loadPack())
        assertEquals(emptyList<String>(), errors)
    }

    @Test
    fun `every lesson video exists and has content`() {
        val pack = loadPack()
        assertTrue(pack.lessons.isNotEmpty())
        pack.lessons.forEach { lesson ->
            val file = File("src/main/assets/${lesson.video}")
            assertTrue("Missing ${lesson.video}", file.exists())
            assertTrue("${lesson.video} is suspiciously small", file.length() > 10_000)
        }
    }

    @Test
    fun `number cards cover one through ten in order`() {
        val pack = loadPack()
        assertEquals((1..10).toList(), pack.numberCards.map { it.value })
        assertEquals("ten", pack.numberCards.last().word)
    }

    @Test
    fun `lesson captions stay inside the video duration`() {
        loadPack().lessons.forEach { lesson ->
            lesson.captions.forEach { caption ->
                assertTrue(caption.atMs >= 0)
                assertTrue(caption.atMs < lesson.durationSec * 1000)
            }
        }
    }

    @Test
    fun `validator rejects duplicate lesson ids`() {
        val pack = loadPack()
        val duplicate = pack.copy(lessons = listOf(pack.lessons.first(), pack.lessons.first()))
        assertTrue(ContentValidator.validate(duplicate).any { it.contains("unique") })
    }

    @Test
    fun `validator rejects equal compare groups`() {
        val pack = loadPack()
        val invalid = pack.copy(
            compareRounds = listOf(CompareRound(2, 2, CompareTarget.MORE)),
        )
        assertTrue(ContentValidator.validate(invalid).any { it.contains("equal groups") })
    }

    @Test
    fun `validator rejects incomplete number cards`() {
        val pack = loadPack()
        val invalid = pack.copy(numberCards = pack.numberCards.dropLast(1))
        assertTrue(ContentValidator.validate(invalid).isNotEmpty())
    }

    @Test
    fun `progress blooms are capped and never negative`() {
        assertEquals(0, Progress().blooms)
        assertEquals(5, Progress(countingSessions = 3, compareSessions = 2).blooms)
        assertEquals(Progress.MAX_BLOOMS, Progress(countingSessions = 50).blooms)
    }
}
