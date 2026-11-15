package com.numbergarden.app

import android.app.Application
import com.numbergarden.app.di.AppContainer
import com.numbergarden.app.rive.RiveRuntime

class NumberGardenApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        RiveRuntime.initialize(this)
        com.numbergarden.app.audio.SoundEffects.init(this)
    }
}
