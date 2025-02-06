package com.app.fm001.model

data class PhotographerProfile(
    val id: String,
    val name: String,
    val bio: String,
    val profileImage: String,
    val rating: Float,
    val reviewCount: Int,
    val verified: Boolean,
    val specialties: List<String> = emptyList(),
    val location: String = "",
    val hourlyRate: Double? = null,
    val availability: Boolean = true
) 