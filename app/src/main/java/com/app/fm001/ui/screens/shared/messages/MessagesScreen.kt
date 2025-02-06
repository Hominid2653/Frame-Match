package com.app.fm001.ui.screens.shared.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.fm001.model.Message
import com.app.fm001.model.MessageType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel,
    onNavigateBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val conversations by viewModel.conversations.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

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
        },
        bottomBar = {
            BottomAppBar {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage("receiver_id", messageText)
                                    messageText = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, "Send")
                        }
                    }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            reverseLayout = true
        ) {
            items(conversations.sortedByDescending { it.timestamp }) { message ->
                MessageItem(
                    message = message,
                    dateFormatter = dateFormatter,
                    onAcceptBid = { viewModel.respondToBid(message.id, true) },
                    onRejectBid = { viewModel.respondToBid(message.id, false) }
                )
            }
        }
    }
}

@Composable
private fun MessageItem(
    message: Message,
    dateFormatter: SimpleDateFormat,
    onAcceptBid: () -> Unit,
    onRejectBid: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge
            )
            
            if (message.type == MessageType.BID) {
                message.bid?.let { bid ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Proposal: ${bid.proposal}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onRejectBid) {
                            Text("Reject")
                        }
                        TextButton(onClick = onAcceptBid) {
                            Text("Accept")
                        }
                    }
                }
            }
            
            Text(
                text = dateFormatter.format(message.timestamp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
} 