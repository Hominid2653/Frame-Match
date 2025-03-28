package com.app.fm001.ui.screens.client.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fm001.model.PhotographerProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MessagePhotographersViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _messagePhotographers = MutableStateFlow<List<PhotographerProfile>>(emptyList())
    val messagePhotographers: StateFlow<List<PhotographerProfile>> = _messagePhotographers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadMessagePhotographers(loggedInUserId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Get all messages where the logged-in user is the sender
                val messages = db.collection("messages")
                    .whereEqualTo("senderId", loggedInUserId)
                    .get()
                    .await()

                // Get unique receiver IDs from these messages
                val photographerIds = messages.documents
                    .mapNotNull { it.getString("receiverId") }
                    .toSet()

                Log.d("MessagePhotographers", "Found ${photographerIds.size} photographers messaged by user")

                if (photographerIds.isEmpty()) {
                    _messagePhotographers.value = emptyList()
                    return@launch
                }

                // Fetch profiles for these photographers
                val photographers = db.collection("profiles")
                    .whereIn("userId", photographerIds.toList())
                    .get()
                    .await()
                    .mapNotNull { document ->
                        PhotographerProfile(
                            id = document.id,
                            userId = document.getString("userId") ?: "",
                            name = document.getString("name") ?: "No Name",
                            bio = document.getString("bio") ?: "No Bio",
                            profileImage = document.getString("profileImage") ?: "",
                            rating = document.getDouble("rating")?.toFloat() ?: 0f,
                            reviewCount = document.getLong("reviewCount")?.toInt() ?: 0,
                            verified = document.getBoolean("verified") ?: false,
                            specialties = document.get("specialties") as? List<String> ?: emptyList(),
                            location = document.getString("location") ?: ""
                        )
                    }

                Log.d("MessagePhotographers", "Fetched ${photographers.size} photographer profiles")
                _messagePhotographers.value = photographers
            } catch (e: Exception) {
                Log.e("MessagePhotographers", "Error loading message photographers", e)
                _messagePhotographers.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}