package com.app.fm001.ui.screens.photographer.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PhotoPost(
    val id: String,
    val photographerId: String,
    val images: List<String>,
    val description: String,
    val hashtags: List<String>,
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val comments: List<Comment> = emptyList(),
    val isLiked: Boolean = false
)

data class Comment(
    val id: String,
    val userId: String,
    val username: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

class PostViewModel : ViewModel() {
    private val _isPosting = MutableStateFlow(false)
    val isPosting = _isPosting.asStateFlow()

    private val _posts = MutableStateFlow<List<PhotoPost>>(emptyList())
    val posts = _posts.asStateFlow()

    suspend fun createPost(images: List<String>, description: String, hashtags: List<String>) {
        _isPosting.value = true
        try {
            // TODO: Implement actual API call to save post
            val newPost = PhotoPost(
                id = System.currentTimeMillis().toString(),
                photographerId = "current_user_id", // TODO: Get from auth
                images = images,
                description = description,
                hashtags = hashtags
            )
            
            _posts.value = _posts.value + newPost
        } finally {
            _isPosting.value = false
        }
    }
} 