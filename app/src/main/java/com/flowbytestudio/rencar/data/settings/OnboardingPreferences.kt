package com.flowbytestudio.rencar.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.onboardingDataStore by preferencesDataStore(name = "onboarding_prefs")

/**
 * Tracks whether the user has completed the onboarding tour at least once, persisted via
 * DataStore. Mirrors the [ThemeController] singleton pattern used elsewhere in the app instead
 * of pulling in a DI framework.
 */
object OnboardingPreferences {

    private val HAS_SEEN_ONBOARDING_KEY = booleanPreferencesKey("has_seen_onboarding")

    private val _hasSeenOnboarding = MutableStateFlow(false)
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    private var appContext: Context? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun init(context: Context) {
        if (appContext != null) return
        val ctx = context.applicationContext
        appContext = ctx
        scope.launch {
            val saved = ctx.onboardingDataStore.data.first()[HAS_SEEN_ONBOARDING_KEY]
            _hasSeenOnboarding.value = saved ?: false
        }
    }

    fun markOnboardingSeen() {
        _hasSeenOnboarding.value = true
        val ctx = appContext ?: return
        scope.launch {
            ctx.onboardingDataStore.edit { prefs -> prefs[HAS_SEEN_ONBOARDING_KEY] = true }
        }
    }
}
