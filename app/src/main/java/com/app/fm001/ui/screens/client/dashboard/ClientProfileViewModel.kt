package com.app.fm001.ui.screens.client.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fm001.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class ClientProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private val _profile = MutableStateFlow(getDummyProfile())
    val profile = _profile.asStateFlow()

    private val _selectedTags = MutableStateFlow<Set<EventType>>(_profile.value.preferredTags)
    val selectedTags = _selectedTags.asStateFlow()

    private val _settings = MutableStateFlow(_profile.value.settings)
    val settings = _settings.asStateFlow()

    init {
        fetchProfile()
    }

    // Fetch profile data from Firestore
    private fun fetchProfile() {
        viewModelScope.launch {
            currentUser?.uid?.let { uid ->
                db.collection("customer_profile").document(uid)
                    .addSnapshotListener { document, e ->
                        if (e != null) {
                            println("Error fetching profile: ${e.message}")
                            return@addSnapshotListener
                        }
                        if (document != null && document.exists()) {
                            val preferredTags = (document.get("preferredTags") as? List<String>)
                                ?.mapNotNull { EventType.valueOf(it) }
                                ?.toSet() ?: emptySet()

                            println("Fetched Preferred Tags: $preferredTags")

                            _profile.value = _profile.value.copy(
                                name = document.getString("name") ?: "John Doe",
                                bio = document.getString("bio") ?: "Photography enthusiast",
                                profileImage = document.getString("profileImage") ?: "",
                                preferredTags = preferredTags
                            )
                            _selectedTags.value = preferredTags
                        }
                    }
            }
        }
    }

    // Create a new customer profile in Firestore
    private suspend fun createCustomerProfile(uid: String) {
        val profile = getDummyProfile()
        db.collection("customer_profile").document(uid).set(
            mapOf(
                "name" to profile.name,
                "bio" to profile.bio,
                "profileImage" to profile.profileImage,
                "preferredTags" to profile.preferredTags.map { it.name },
                "settings" to profile.settings
            )
        ).await()
        _profile.value = profile
        _selectedTags.value = profile.preferredTags
    }

    // Save profile data to Firestore
    fun updateProfile(name: String, bio: String, profileImage: String? = null) {
        viewModelScope.launch {
            currentUser?.uid?.let { uid ->
                val updates = mutableMapOf<String, Any>(
                    "name" to name,
                    "bio" to bio
                )
                if (profileImage != null) {
                    updates["profileImage"] = profileImage
                }
                db.collection("customer_profile").document(uid).set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
                _profile.value = _profile.value.copy(
                    name = name,
                    bio = bio,
                    profileImage = profileImage ?: _profile.value.profileImage
                )
            }
        }
    }

    // Toggle selected tags and save to Firestore
    fun toggleTag(tag: EventType) {
        _selectedTags.value = _selectedTags.value.toMutableSet().apply {
            if (contains(tag)) remove(tag) else add(tag)
        }
        viewModelScope.launch {
            currentUser?.uid?.let { uid ->
                db.collection("customer_profile").document(uid).set(
                    mapOf("preferredTags" to _selectedTags.value.map { it.name }),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()
            }
        }
    }

    // Update notifications
    fun updateNotifications(enabled: Boolean) {
        _settings.value = _settings.value.copy(notificationsEnabled = enabled)
        updateProfileSettings()
    }

    // Logout
    fun logout() {
        auth.signOut()
        // Clear local state
        _profile.value = getDummyProfile()
        _selectedTags.value = emptySet()
    }

    // Helper function to create a dummy profile
    private fun getDummyProfile() = ClientProfile(
        id = "client_${UUID.randomUUID()}",
        name = "John Doe",
        bio = "Photography enthusiast",
        profileImage = "", // Empty Base64 string initially
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

    // Update profile settings in Firestore
    private fun updateProfileSettings() {
        viewModelScope.launch {
            currentUser?.uid?.let { uid ->
                db.collection("customer_profile").document(uid).set(
                    mapOf("settings" to _settings.value),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()
            }
        }
    }

    // Update privacy settings
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
    fun logout(onLogoutComplete: () -> Unit) {
        auth.signOut()

        // Clear local state
        _profile.value = getDummyProfile()
        _selectedTags.value = emptySet()
        _settings.value = _profile.value.settings

        // Trigger UI update (navigate to login)
        onLogoutComplete()
    }
}