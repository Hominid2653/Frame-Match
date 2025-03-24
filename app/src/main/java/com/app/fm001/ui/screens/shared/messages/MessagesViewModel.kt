package com.app.fm001.ui.screens.shared.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fm001.model.Conversation
import com.app.fm001.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class MessagesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // State for messages
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages

    // State for conversations
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> get() = _conversations

    // State for the message input field
    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> get() = _messageText

    // Fetch messages between two users
    fun fetchMessages(senderId: String, receiverId: String) {
        viewModelScope.launch {
            try {
                val messages = db.collection("messages")
                    .whereIn("senderId", listOf(senderId, receiverId))
                    .whereIn("receiverId", listOf(senderId, receiverId))
                    .get()
                    .await()

                _messages.value = messages.documents
                    .mapNotNull { document ->
                        document.toObject(Message::class.java) // Explicitly specify the type
                    }
                    .sortedBy { it.timestamp }
            } catch (e: Exception) {
                println("Error fetching messages: ${e.message}")
            }
        }
    }

    // Fetch conversations for the logged-in user
    fun fetchConversations(userId: String) {
        viewModelScope.launch {
            try {
                // Fetch messages where the logged-in user is the sender or receiver
                val senderMessages = db.collection("messages")
                    .whereEqualTo("senderId", userId)
                    .get()
                    .await()

                val receiverMessages = db.collection("messages")
                    .whereEqualTo("receiverId", userId)
                    .get()
                    .await()

                // Combine the results
                val allMessages = senderMessages.documents + receiverMessages.documents

                // Group messages by the other user's ID
                val groupedMessages = allMessages
                    .mapNotNull { document ->
                        document.toObject(Message::class.java) // Explicitly specify the type
                    }
                    .groupBy { message ->
                        if (message.senderId == userId) message.receiverId else message.senderId
                    }

                // Create conversations from grouped messages
                val conversations = groupedMessages.map { (otherUserId, messages) ->
                    val lastMessage = messages.maxByOrNull { it.timestamp }
                    Conversation(
                        otherUserId = otherUserId,
                        lastMessage = lastMessage?.content ?: "",
                        timestamp = lastMessage?.timestamp ?: Date()
                    )
                }

                _conversations.value = conversations
            } catch (e: Exception) {
                println("Error fetching conversations: ${e.message}")
            }
        }
    }

    // Send a message
    // Send a message
    fun sendMessage(senderId: String, senderEmail: String, receiverId: String, receiverEmail: String) {
        viewModelScope.launch {
            try {
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = senderId,
                    senderEmail = senderEmail,
                    receiverId = receiverId,
                    receiverEmail = receiverEmail,
                    content = _messageText.value,
                    timestamp = Date()
                )

                db.collection("messages")
                    .document(message.id)
                    .set(message)
                    .await()

                // Clear the input field
                _messageText.value = ""

                // Refresh messages
                fetchMessages(senderId, receiverId)
            } catch (e: Exception) {
                println("Error sending message: ${e.message}")
            }
        }
    }

    // Update the message input field
    fun updateMessageText(text: String) {
        _messageText.value = text
    }
}