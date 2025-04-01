package com.app.fm001.ui.screens.photographer.dashboard.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.fm001.model.JobProposal
import com.app.fm001.model.Message
import com.app.fm001.model.ProposalStatus
import com.app.fm001.ui.screens.photographer.dashboard.JobViewModel
import com.app.fm001.ui.screens.photographer.dashboard.components.JobProposalCard
import com.app.fm001.ui.screens.shared.messages.MessagesViewModel
import java.util.Locale

@Composable
fun BidsScreen(
    loggedInUserId: String,
    loggedInUserEmail: String,
    jobViewModel: JobViewModel = viewModel(),
    messagesViewModel: MessagesViewModel = viewModel(),
    onNavigateToMessages: (String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Active Bids", "Completed", "Messages")

    LaunchedEffect(Unit) {
        jobViewModel.fetchProposals()
        messagesViewModel.fetchAllConversations()
    }

    val proposals by jobViewModel.proposals.collectAsState()
    val conversations by messagesViewModel.conversations.collectAsState()

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
                0 -> ActiveBids(
                    bids = proposals.filter { it.status == ProposalStatus.IN_PROGRESS },
                    jobViewModel = jobViewModel,
                    onNavigateToMessages = onNavigateToMessages
                )
                1 -> CompletedBids(
                    bids = proposals.filter { it.status == ProposalStatus.COMPLETED },
                    onNavigateToMessages = onNavigateToMessages
                )
                2 -> EmailListScreen(
                    conversations = conversations,
                    loggedInUserId = loggedInUserId,
                    onEmailClick = { receiverId, receiverEmail ->
                        onNavigateToMessages(receiverId, receiverEmail)
                    }
                )
            }
        }
    }
}

@Composable
private fun EmailListScreen(
    conversations: List<Message>,
    loggedInUserId: String,
    onEmailClick: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Messages",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (conversations.isEmpty()) {
            Text(
                text = "No messages yet",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(conversations.distinctBy {
                    if (it.senderId == loggedInUserId) it.receiverId else it.senderId
                }) { message ->
                    val (otherUserId, otherUserEmail) = if (message.senderId == loggedInUserId) {
                        message.receiverId to message.receiverEmail
                    } else {
                        message.senderId to message.senderEmail
                    }

                    EmailListItem(
                        email = otherUserEmail,
                        onClick = { onEmailClick(otherUserId, otherUserEmail) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmailListItem(
    email: String,
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
                    text = email,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View conversation"
            )
        }
    }
}

@Composable
private fun ActiveBids(
    bids: List<JobProposal>,
    jobViewModel: JobViewModel,
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
                jobViewModel.updateProposalStatus(selectedBid!!, ProposalStatus.PENDING)
                showDialog = false
            }
        )
    }
}

@Composable
private fun CompletedBids(
    bids: List<JobProposal>,
    onNavigateToMessages: (String, String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(bids) { bid ->
            JobProposalCard(
                proposal = bid,
                onApplyClick = {},
                onMessageClick = { clientId ->
                    onNavigateToMessages(bid.photographerId, clientId)
                }
            )
        }
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
        title = { Text("Confirm Bid Action") },
        text = { Text("Are you sure you want to proceed with this bid?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}