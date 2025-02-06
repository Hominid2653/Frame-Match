package com.app.fm001.model

import java.util.Date

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Date,
    val jobId: String? = null,
    val bid: Bid? = null,
    val type: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT,
    BID,
    ACCEPTED_BID,
    REJECTED_BID
}

data class Bid(
    val amount: Double,
    val proposal: String,
    val availability: Boolean = true,
    val status: BidStatus = BidStatus.PENDING
)

enum class BidStatus {
    PENDING,
    ACCEPTED,
    REJECTED
} 