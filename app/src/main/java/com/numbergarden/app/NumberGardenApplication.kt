package com.numbergarden.app

import android.app.Application
import com.numbergarden.app.di.AppContainer

class NumberGardenApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
