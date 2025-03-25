package com.app.fm001.ui.screens.shared.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.fm001.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    senderId: String,  // ✅ Added senderId

    receiverId: String,
    onNavigateBack: () -> Unit, // Corrected: Now it's a function
    viewModel: MessagesViewModel = viewModel()
) {
    val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val senderEmail = remember { mutableStateOf<String?>(null) }
    val receiverEmail = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch sender and receiver emails when the screen loads
    LaunchedEffect(loggedInUserId, receiverId) {
        coroutineScope.launch {
            senderEmail.value = fetchUserEmail(loggedInUserId)
            receiverEmail.value = fetchUserEmail(receiverId)
        }
    }

    LaunchedEffect(receiverId) {
        viewModel.fetchMessages(receiverId)
    }

    val messages by viewModel.messages.collectAsState()
    val messageText by viewModel.messageText.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { // Fix: Function used correctly
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(messages) { message ->
                        MessageBubble(
                            message = message,
                            isFromLoggedInUser = message.senderId == loggedInUserId
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f)) // Ensures input field is visible

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .imePadding() // Push up when keyboard appears
                    .navigationBarsPadding(), // Avoid overlap with system navigation bar
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { viewModel.updateMessageText(it) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Type a message...") }
                )

                IconButton(
                    onClick = {
                        if (senderEmail.value != null && receiverEmail.value != null) {
                            viewModel.sendMessage(
                                receiverId,
                                receiverEmail.value!!,
                                senderEmail.value!!
                            )
                        }
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
    isFromLoggedInUser: Boolean
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
                Text(text = message.senderEmail, style = MaterialTheme.typography.bodySmall)
                Text(text = message.content, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

/**
 * Fetches the email of a user given their user ID.
 */
suspend fun fetchUserEmail(userId: String): String? {
    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .await()

        snapshot.getString("email") // Get the email field from Firestore
    } catch (e: Exception) {
        null
    }
}
