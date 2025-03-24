package com.app.fm001.ui.screens.shared.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.fm001.model.Conversation
import com.app.fm001.model.Message
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    loggedInUserId: String, // Logged-in user's ID
    senderId: String, // Sender's ID
    receiverId: String, // Receiver's ID
    onNavigateBack: () -> Unit, // Callback to navigate back
    onNavigateToConversation: (String, String) -> Unit, // Callback to navigate to the conversation screen
    viewModel: MessagesViewModel = viewModel()
) {
    // Fetch messages between the sender and receiver
    LaunchedEffect(senderId, receiverId) {
        viewModel.fetchMessages(senderId, receiverId)
    }

    // Observe messages from the ViewModel
    val messages by viewModel.messages.collectAsState()

    // Observe messageText from the ViewModel
    val messageText by viewModel.messageText.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (messages.isEmpty()) {
                Text(
                    text = "No messages yet.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(messages) { message ->
                        MessageBubble(
                            message = message,
                            isFromLoggedInUser = message.senderId == loggedInUserId,
                            onClick = {
                                // Navigate to the conversation screen with the sender's and receiver's IDs
                                onNavigateToConversation(message.senderId, message.receiverId)
                            }
                        )
                    }
                }
            }

            // Message input field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText, // ✅ Use the observed value
                    onValueChange = { viewModel.updateMessageText(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") }
                )

                IconButton(
                    onClick = {
                        viewModel.sendMessage(
                            senderId = loggedInUserId,
                            senderEmail = "loggedInUserEmail@example.com", // Replace with actual email
                            receiverId = receiverId,
                            receiverEmail = "receiverEmail@example.com" // Replace with actual email
                        )
                    }
                ) {
                    Icon(Icons.Default.Send, "Send")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isFromLoggedInUser: Boolean,
    onClick: () -> Unit // Callback for clicking on the email
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = if (isFromLoggedInUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isFromLoggedInUser) Color(0xFFDCF8C6) else Color(0xFFECECEC)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Display the sender's email
                Text(
                    text = message.senderEmail,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable(onClick = onClick) // Make the email clickable
                )
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
