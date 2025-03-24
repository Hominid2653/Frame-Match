package com.app.fm001.model

import java.util.Date
data class Message(
    val id: String,
    val senderId: String,
    val senderEmail: String, // Add sender's email
    val receiverId: String,
    val receiverEmail: String, // Add receiver's email
    val content: String,
    val timestamp: Date
)

enum class BidStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

data class Conversation(
    val otherUserId: String, // ID of the other user in the conversation
    val lastMessage: String, // Last message in the conversation
    val timestamp: Date // Timestamp of the last message
)