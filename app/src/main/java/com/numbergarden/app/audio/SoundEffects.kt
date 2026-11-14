package com.numbergarden.app.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import com.numbergarden.app.di.AppContainer

/**
 * Tiny, optional sound cues. These are deliberately soft and short so they stay inside the
 * app's low-sensory budget, and they never block a task if the device cannot play them.
 */
object SoundEffects {

    private var toneGenerator: ToneGenerator? = null

    fun init(context: Context) {
        if (toneGenerator == null) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 30)
            } catch (_: Throwable) {
                toneGenerator = null
            }
        }
    }

    fun correct() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
        } catch (_: Throwable) {
        }
    }

    fun finish() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 180)
        } catch (_: Throwable) {
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
        } catch (_: Throwable) {
        }
        toneGenerator = null
    }
}