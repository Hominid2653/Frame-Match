package com.app.fm001.model

import java.util.Date

data class JobPost(
    val id: String,
    val title: String,
    val description: String,
    val budget: Double,
    val location: String,
    val eventDate: Date,
    val eventType: EventType,
    val requirements: List<String>,
    val clientId: String,
    val postedDate: Date,
    val status: Status = Status.OPEN
) {
    enum class Status {
        OPEN,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
} 