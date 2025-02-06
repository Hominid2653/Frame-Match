package com.app.fm001.model

import java.util.Date

data class JobProposal(
    val id: String,
    val title: String,
    val description: String,
    val budget: Double,
    val location: String,
    val eventDate: Date,
    val eventType: EventType,
    val requirements: List<String>,
    val clientId: String,
    val clientName: String,
    val postedDate: Date,
    val status: ProposalStatus = ProposalStatus.OPEN
)

enum class ProposalStatus {
    OPEN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
} 