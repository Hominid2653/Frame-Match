package com.app.fm001.ui.screens.client.dashboard

import androidx.lifecycle.ViewModel
import com.app.fm001.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class ClientProfileViewModel : ViewModel() {
    private val _profile = MutableStateFlow(getDummyProfile())
    val profile = _profile.asStateFlow()

    private val _selectedTags = MutableStateFlow<Set<EventType>>(_profile.value.preferredTags)
    val selectedTags = _selectedTags.asStateFlow()

    private val _settings = MutableStateFlow(_profile.value.settings)
    val settings = _settings.asStateFlow()

    fun updateProfile(name: String, bio: String) {
        _profile.value = _profile.value.copy(
            name = name,
            bio = bio
        )
    }

    fun toggleTag(tag: EventType) {
        _selectedTags.value = _selectedTags.value.toMutableSet().apply {
            if (contains(tag)) remove(tag) else add(tag)
        }
        // Update profile with new tags
        _profile.value = _profile.value.copy(preferredTags = _selectedTags.value)
    }

    fun updateNotifications(enabled: Boolean) {
        _settings.value = _settings.value.copy(notificationsEnabled = enabled)
        updateProfileSettings()
    }

    fun updateLanguage(language: String) {
        _settings.value = _settings.value.copy(language = language)
        updateProfileSettings()
    }

    fun updatePrivacySettings(
        visibility: ProfileVisibility? = null,
        showEmail: Boolean? = null,
        allowMessages: Boolean? = null
    ) {
        _settings.value = _settings.value.copy(
            privacySettings = _settings.value.privacySettings.copy(
                profileVisibility = visibility ?: _settings.value.privacySettings.profileVisibility,
                showEmail = showEmail ?: _settings.value.privacySettings.showEmail,
                allowMessages = allowMessages ?: _settings.value.privacySettings.allowMessages
            )
        )
        updateProfileSettings()
    }

    private fun updateProfileSettings() {
        _profile.value = _profile.value.copy(settings = _settings.value)
    }

    fun logout() {
        // TODO: Implement actual logout logic
    }

    private fun getDummyProfile() = ClientProfile(
        id = "client_${UUID.randomUUID()}",
        name = "John Doe",
        bio = "Photography enthusiast",
        profileImage = "https://i.pravatar.cc/150",
        email = "john.doe@example.com",
        preferredTags = setOf(EventType.WEDDING, EventType.PORTRAIT),
        settings = ClientSettings(
            notificationsEnabled = true,
            language = "English",
            privacySettings = PrivacySettings(
                profileVisibility = ProfileVisibility.PUBLIC,
                showEmail = false,
                allowMessages = true
            )
        )
    )
} 