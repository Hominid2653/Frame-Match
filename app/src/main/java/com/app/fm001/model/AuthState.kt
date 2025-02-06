package com.app.fm001.model

data class AuthState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "",
    val role: UserRole = UserRole.CLIENT,
    val isLoading: Boolean = false,
    val error: String? = null
) 