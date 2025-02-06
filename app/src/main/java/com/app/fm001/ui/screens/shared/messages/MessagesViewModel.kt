package com.app.fm001.ui.screens.shared.messages

import androidx.lifecycle.ViewModel
import com.app.fm001.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class MessagesViewModel : ViewModel() {
    private val _conversations = MutableStateFlow<List<Message>>(emptyList())
    val conversations = _conversations.asStateFlow()

    fun sendMessage(receiverId: String, content: String, jobId: String? = null) {
        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = "current_user_id", // TODO: Replace with actual user ID
            receiverId = receiverId,
            content = content,
            timestamp = Date(),
            jobId = jobId
        )
        _conversations.value = _conversations.value + message
    }

    fun sendBid(receiverId: String, jobId: String, amount: Double, proposal: String) {
        val bid = Bid(amount = amount, proposal = proposal)
        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = "current_user_id", // TODO: Replace with actual user ID
            receiverId = receiverId,
            content = "New bid: $$amount",
            timestamp = Date(),
            jobId = jobId,
            bid = bid,
            type = MessageType.BID
        )
        _conversations.value = _conversations.value + message
    }

    fun respondToBid(messageId: String, accepted: Boolean) {
        val message = _conversations.value.find { it.id == messageId } ?: return
        val updatedBid = message.bid?.copy(
            status = if (accepted) BidStatus.ACCEPTED else BidStatus.REJECTED
        )
        val responseMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = "current_user_id",
            receiverId = message.senderId,
            content = if (accepted) "Bid accepted" else "Bid rejected",
            timestamp = Date(),
            jobId = message.jobId,
            bid = updatedBid,
            type = if (accepted) MessageType.ACCEPTED_BID else MessageType.REJECTED_BID
        )
        _conversations.value = _conversations.value + responseMessage
    }
} 