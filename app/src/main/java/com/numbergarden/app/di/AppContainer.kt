package com.numbergarden.app.di

import android.content.Context
import com.numbergarden.app.data.ContentRepository
import com.numbergarden.app.data.PrefsProgressRepository
import com.numbergarden.app.data.PrefsSettingsRepository
import com.numbergarden.app.model.ProgressRepository
import com.numbergarden.app.model.SettingsRepository

/**
 * Manual dependency container. The app is deliberately small; a DI framework would
 * add build time and indirection without a payoff at this scale.
 */
class AppContainer(context: Context) {
    val contentRepository = ContentRepository(context.applicationContext)
    val progressRepository: ProgressRepository = PrefsProgressRepository(context.applicationContext)
    val settingsRepository: SettingsRepository = PrefsSettingsRepository(context.applicationContext)
}
