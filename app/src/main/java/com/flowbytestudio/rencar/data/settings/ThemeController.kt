package com.flowbytestudio.rencar.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

/**
 * Holds the user's theme preference (Light / Dark / System) in memory and mirrors it to
 * disk via DataStore. Mirrors the [com.flowbytestudio.rencar.data.auth.AuthSession] singleton
 * pattern used elsewhere in the app instead of pulling in a DI framework.
 */
object ThemeController {

    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private var appContext: Context? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun init(context: Context) {
        if (appContext != null) return
        val ctx = context.applicationContext
        appContext = ctx
        scope.launch {
            val saved = ctx.themeDataStore.data.first()[THEME_MODE_KEY]
            val mode = saved?.let { name -> runCatching { ThemeMode.valueOf(name) }.getOrNull() }
            if (mode != null) {
                _themeMode.value = mode
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        val ctx = appContext ?: return
        scope.launch {
            ctx.themeDataStore.edit { prefs -> prefs[THEME_MODE_KEY] = mode.name }
        }
    }
}
