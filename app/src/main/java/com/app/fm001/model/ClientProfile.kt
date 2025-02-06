package com.app.fm001.model

data class ClientProfile(
    val id: String,
    val name: String,
    val bio: String,
    val profileImage: String?,
    val email: String,
    val preferredTags: Set<EventType> = emptySet(),
    val settings: ClientSettings = ClientSettings()
)

data class ClientSettings(
    val notificationsEnabled: Boolean = true,
    val language: String = "English",
    val privacySettings: PrivacySettings = PrivacySettings()
)

data class PrivacySettings(
    val profileVisibility: ProfileVisibility = ProfileVisibility.PUBLIC,
    val showEmail: Boolean = false,
    val allowMessages: Boolean = true
)

enum class ProfileVisibility {
    PUBLIC,
    PRIVATE,
    PHOTOGRAPHERS_ONLY
} 