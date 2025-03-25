package com.app.fm001.ui.screens.photographer.dashboard.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.fm001.model.Conversation
import com.app.fm001.model.JobProposal
import com.app.fm001.model.ProposalStatus
import com.app.fm001.ui.screens.photographer.dashboard.JobViewModel
import com.app.fm001.ui.screens.photographer.dashboard.components.JobProposalCard
import com.app.fm001.ui.screens.shared.messages.MessagesViewModel
import java.util.Locale

@Composable
fun BidsScreen(
    loggedInUserId: String, // Logged-in photographer's ID
    loggedInUserEmail: String, // Logged-in photographer's Email
    viewModel: JobViewModel = viewModel(),
    onNavigateToMessages: (String, String) -> Unit // Callback to navigate to the message screen
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Active Bids", "Completed", "Messages") // Renamed "Cancelled" to "Messages"

    LaunchedEffect(Unit) {
        viewModel.fetchProposals()
    }

    val proposals by viewModel.proposals.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> ActiveBids(proposals.filter { it.status == ProposalStatus.IN_PROGRESS }, viewModel, onNavigateToMessages)
                1 -> CompletedBids(proposals.filter { it.status == ProposalStatus.COMPLETED }, onNavigateToMessages)
                2 -> Messages(loggedInUserId, loggedInUserEmail, onNavigateToMessages) // Pass dynamic email here
            }
        }
    }
}

@Composable
private fun ActiveBids(
    bids: List<JobProposal>,
    viewModel: JobViewModel,
    onNavigateToMessages: (String, String) -> Unit
) {
    BidsList(bids = bids, viewModel = viewModel, onNavigateToMessages = onNavigateToMessages)
}

@Composable
private fun CompletedBids(
    bids: List<JobProposal>,
    onNavigateToMessages: (String, String) -> Unit
) {
    BidsList(bids = bids, viewModel = null, onNavigateToMessages = onNavigateToMessages)
}

@Composable
private fun Messages(
    loggedInUserId: String, // Logged-in photographer's ID
    loggedInUserEmail: String, // Logged-in photographer's Email
    onNavigateToMessages: (String, String) -> Unit // Callback to navigate to the message screen
) {
    val viewModel: MessagesViewModel = viewModel()
    val messages by viewModel.messages.collectAsState()

    val filteredMessages = messages.filter { it.receiverId == loggedInUserId }

    Column {
        Text(
            text = "Messages",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(filteredMessages) { message ->
                MessageItem(
                    senderEmail = message.senderEmail,
                    lastMessage = message.content,
                    timestamp = message.timestamp.toString(),
                    onClick = {
                        onNavigateToMessages(loggedInUserEmail, message.senderEmail)
                    }
                )
            }
        }
    }
}

@Composable
private fun MessageItem(
    senderEmail: String,
    lastMessage: String,
    timestamp: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = senderEmail,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = timestamp,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun BidsList(
    bids: List<JobProposal>,
    viewModel: JobViewModel?,
    onNavigateToMessages: (String, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedBid by remember { mutableStateOf<JobProposal?>(null) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(bids) { bid ->
            JobProposalCard(
                proposal = bid,
                onApplyClick = {
                    selectedBid = bid
                    showDialog = true
                },
                onMessageClick = { clientId ->
                    onNavigateToMessages(bid.photographerId, clientId)
                }
            )
        }
    }

    if (showDialog && selectedBid != null) {
        ConfirmBidActionDialog(
            bid = selectedBid!!,
            onDismiss = { showDialog = false },
            onConfirm = {
                viewModel?.updateProposalStatus(selectedBid!!, ProposalStatus.PENDING)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ConfirmBidActionDialog(
    bid: JobProposal,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Action") },
        text = { Text("Do you want to continue with the bid or cancel it?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Continue (Pending)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ClientMessageItem(
    clientName: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = clientName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate to messages"
            )
        }
    }
}
