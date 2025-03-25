package com.app.fm001.model

import java.util.Date
data class Message(
    val id: String = "",  // Default value to avoid "No value passed for parameter 'id'"
    val content: String = "",
    val senderId: String = "",
    val senderEmail: String = "",
    val receiverId: String = "",
    val receiverEmail: String = "",
    val timestamp: Date = Date()  // Ensure timestamp is a Date object
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