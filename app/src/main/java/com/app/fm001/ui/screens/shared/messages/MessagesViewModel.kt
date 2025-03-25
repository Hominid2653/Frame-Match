package com.app.fm001.ui.screens.shared.messages

import androidx.lifecycle.ViewModel
import com.app.fm001.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class MessagesViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    fun fetchMessages(receiverId: String) {
        val senderId = auth.currentUser?.uid ?: return

        db.collection("messages")
            .whereIn("senderId", listOf(senderId, receiverId))
            .whereIn("receiverId", listOf(senderId, receiverId))
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                val messages = snapshot.documents.mapNotNull { it.toObject(Message::class.java) }
                _messages.value = messages.sortedBy { it.timestamp }
            }
    }

    fun sendMessage(receiverId: String, value: String, value1: String) {
        val senderId = auth.currentUser?.uid ?: return

        // Fetch sender and receiver emails from Firestore
        db.collection("users").document(senderId).get().addOnSuccessListener { senderDoc ->
            val senderEmail = senderDoc.getString("email") ?: "Unknown Email"

            db.collection("users").document(receiverId).get().addOnSuccessListener { receiverDoc ->
                val receiverEmail = receiverDoc.getString("email") ?: "Unknown Email"

                val message = Message(
                    id = UUID.randomUUID().toString(),
                    content = _messageText.value,
                    senderId = senderId,
                    senderEmail = senderEmail,
                    receiverId = receiverId,
                    receiverEmail = receiverEmail,
                    timestamp = Date()
                )

                db.collection("messages").add(message)
                    .addOnSuccessListener {
                        _messageText.value = "" // Clear input after sending
                    }
            }
        }
    }
}
