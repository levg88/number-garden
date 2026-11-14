package com.numbergarden.app.data

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * MediaPlayer can be unreliable with file:///android_asset paths on some devices,
 * so bundled videos are copied once to the app cache before playback.
 */
object AssetVideoCache {

    fun ensure(context: Context, assetPath: String): Uri {
        require(!assetPath.contains("..")) { "Unsafe asset path" }
        val fileName = assetPath.substringAfterLast('/')
        require(fileName.endsWith(".mp4")) { "Only mp4 assets are supported" }

        val directory = File(context.cacheDir, "videos")
        if (!directory.exists()) directory.mkdirs()
        val target = File(directory, fileName)

        if (!target.exists() || target.length() == 0L) {
            context.assets.open(assetPath).use { input ->
                target.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        return Uri.fromFile(target)
    }
}
