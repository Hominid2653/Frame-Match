package com.app.fm001.model

data class PhotographerProfile(
    val id: String,
    val userId: String, // Required field
    val name: String,
    val bio: String,
    val profileImage: String, // Non-nullable String
    val rating: Float,
    val reviewCount: Int,
    val verified: Boolean,
    val specialties: List<String> = emptyList(),
    val location: String = "",
    val hourlyRate: Double? = null,
    val availability: Boolean = true,
    val portfolio: List<String> = emptyList()
)
data class PhotoPost(
    val id: String = "",
    val photographerId: String = "",
    val images: List<String> = emptyList(),
    val description: String = "",
    val hashtags: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val comments: List<com.app.fm001.model.Comment> = emptyList(),
    val isLiked: Boolean = false
)


data class Comment(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
data class Post(
    val id: String = "",
    val userId: String = "", // Make sure this exists
    val imageBase64: String = "",
    val description: String = "",
    val likes: Int = 0)
