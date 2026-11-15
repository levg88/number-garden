package com.numbergarden.app.rive

import android.content.Context
import app.rive.runtime.kotlin.core.Rive

/**
 * Loads the Rive native runtime once, before any [app.rive.runtime.kotlin.RiveAnimationView]
 * is constructed.
 *
 * The Rive AAR deliberately removes its own AndroidX Startup entry, so the host app must
 * initialise the runtime. Doing it here lets the app fail soft: if the native library is
 * unavailable for the device ABI, [available] stays false and the mascot falls back to its
 * hand-drawn face instead of crashing the game.
 */
object RiveRuntime {

    @Volatile
    var available: Boolean = false
        private set

    fun initialize(context: Context) {
        if (available) return
        available = runCatching {
            Rive.init(context.applicationContext)
        }.isSuccess
    }
}
