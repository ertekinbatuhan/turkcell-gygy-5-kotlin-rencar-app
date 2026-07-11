package com.flowbytestudio.rencar.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthSession {

    var accessToken: String? = null
        private set
    var refreshToken: String? = null
        private set

    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser.asStateFlow()

    val isLoggedIn: StateFlow<Boolean>
        get() = _isLoggedIn.asStateFlow()
    private val _isLoggedIn = MutableStateFlow(false)

    var justRegistered: Boolean = false
        private set

    fun onAuthenticated(response: AuthResponse, isNewRegistration: Boolean = false) {
        accessToken = response.accessToken
        refreshToken = response.refreshToken
        _currentUser.value = response.user
        _isLoggedIn.value = true
        justRegistered = isNewRegistration
    }

    fun consumeJustRegistered() {
        justRegistered = false
    }

    fun clear() {
        accessToken = null
        refreshToken = null
        _currentUser.value = null
        _isLoggedIn.value = false
        justRegistered = false
    }
}
