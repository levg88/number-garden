package com.numbergarden.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.numbergarden.app.audio.LocalSpeech
import com.numbergarden.app.audio.Narrator
import com.numbergarden.app.ui.nav.NumberGardenNavHost
import com.numbergarden.app.ui.theme.NumberGardenTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )

        val container = (application as NumberGardenApplication).container

        setContent {
            val settings by container.settingsRepository.settings.collectAsStateWithLifecycle()
            NumberGardenTheme(highContrast = settings.highContrast) {
                val context = LocalContext.current
                val narrator = remember(context) {
                    Narrator(context.applicationContext, container.settingsRepository)
                }
                DisposableEffect(narrator) {
                    onDispose { narrator.release() }
                }
                CompositionLocalProvider(LocalSpeech provides narrator) {
                    NumberGardenNavHost(container)
                }
            }
        }
    }
}
