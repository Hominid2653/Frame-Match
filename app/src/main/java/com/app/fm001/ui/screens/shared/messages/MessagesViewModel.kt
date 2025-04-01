// MessagesViewModel.kt
package com.app.fm001.ui.screens.shared.messages

import androidx.lifecycle.ViewModel
import com.app.fm001.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class MessagesViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText

    private val _unreadMessageCount = MutableStateFlow(0)
    val unreadMessageCount: StateFlow<Int> = _unreadMessageCount

    // Call this when fetching messages to update count
    private fun updateUnreadCount(messages: List<Message>) {
        _unreadMessageCount.value = messages.count { !it.isRead }
    }

    // Call this when messages are read
    fun markMessagesAsRead() {
        _unreadMessageCount.value = 0
    }

    private val _conversations = MutableStateFlow<List<Message>>(emptyList())
    val conversations: StateFlow<List<Message>> = _conversations

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var receiverListener: ListenerRegistration? = null
    private var senderListener: ListenerRegistration? = null

    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    fun fetchMessages(receiverId: String) {
        clearListeners()
        val senderId = auth.currentUser?.uid ?: return

        receiverListener = db.collection("messages")
            .whereIn("senderId", listOf(senderId, receiverId))
            .whereIn("receiverId", listOf(senderId, receiverId))
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                _messages.value = snapshot.documents.mapNotNull {
                    it.toObject(Message::class.java)
                }.sortedBy { it.timestamp }
            }
    }

    fun fetchAllConversations() {
        clearListeners()
        val userId = auth.currentUser?.uid ?: return

        receiverListener = db.collection("messages")
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { receiverSnapshot, e ->
                if (e != null) return@addSnapshotListener

                senderListener = db.collection("messages")
                    .whereEqualTo("senderId", userId)
                    .addSnapshotListener { senderSnapshot, e2 ->
                        if (e2 != null) return@addSnapshotListener

                        val receiverMessages = receiverSnapshot?.documents?.mapNotNull {
                            it.toObject(Message::class.java)
                        } ?: emptyList()

                        val senderMessages = senderSnapshot?.documents?.mapNotNull {
                            it.toObject(Message::class.java)
                        } ?: emptyList()

                        val allMessages = receiverMessages + senderMessages
                        _conversations.value = allMessages
                            .groupBy {
                                if (it.senderId == userId) it.receiverId else it.senderId
                            }
                            .map { (_, msgs) -> msgs.maxByOrNull { it.timestamp }!! }
                            .sortedByDescending { it.timestamp }
                    }
            }
    }

    fun sendMessage(receiverId: String, receiverEmail: String, senderEmail: String) {
        val senderId = auth.currentUser?.uid ?: return
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
                _messageText.value = ""
                fetchAllConversations()
            }
    }

    private fun clearListeners() {
        receiverListener?.remove()
        senderListener?.remove()
        receiverListener = null
        senderListener = null
    }

    override fun onCleared() {
        super.onCleared()
        clearListeners()
    }
}