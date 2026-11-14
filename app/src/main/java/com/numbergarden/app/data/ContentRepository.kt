package com.numbergarden.app.data

import android.content.Context
import com.numbergarden.app.model.ContentPack
import com.numbergarden.app.model.ContentValidator
import kotlinx.serialization.json.Json

class ContentLoadException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Loads and validates the bundled content pack. Validation is strict so a broken
 * content update fails fast instead of showing a child an empty or wrong screen.
 */
class ContentRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = false
        isLenient = false
    }

    @Volatile
    private var cached: ContentPack? = null

    fun load(): ContentPack {
        cached?.let { return it }
        return synchronized(this) {
            cached ?: readAndValidate().also { cached = it }
        }
    }

    fun reload(): ContentPack {
        synchronized(this) { cached = null }
        return load()
    }

    private fun readAndValidate(): ContentPack {
        val pack = try {
            context.assets.open(CONTENT_FILE).bufferedReader().use { reader ->
                json.decodeFromString(ContentPack.serializer(), reader.readText())
            }
        } catch (e: Exception) {
            throw ContentLoadException("Could not read $CONTENT_FILE", e)
        }

        val errors = ContentValidator.validate(pack)
        if (errors.isNotEmpty()) {
            throw ContentLoadException("Content pack is invalid:\n" + errors.joinToString("\n"))
        }

        pack.lessons.forEach { lesson ->
            try {
                context.assets.open(lesson.video).close()
            } catch (e: Exception) {
                throw ContentLoadException("Missing video asset: ${lesson.video}", e)
            }
        }
        return pack
    }

    private companion object {
        const val CONTENT_FILE = "content.json"
    }
}
