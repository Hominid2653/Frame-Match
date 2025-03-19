package com.app.fm001.ui.screens.photographer.dashboard.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.fm001.model.JobProposal
import com.app.fm001.model.ProposalStatus
import com.app.fm001.ui.screens.photographer.dashboard.JobViewModel
import com.app.fm001.ui.screens.photographer.dashboard.components.JobProposalCard

@Composable
fun BidsScreen(
    viewModel: JobViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToMessages: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Active Bids", "Completed", "Cancelled")

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
                2 -> CancelledBids(proposals.filter { it.status == ProposalStatus.CANCELLED }, onNavigateToMessages)
            }
        }
    }
}

@Composable
private fun ActiveBids(
    bids: List<JobProposal>,
    viewModel: JobViewModel,
    onNavigateToMessages: (String) -> Unit
) {
    BidsList(bids = bids, viewModel = viewModel, onNavigateToMessages = onNavigateToMessages)
}

@Composable
private fun CompletedBids(
    bids: List<JobProposal>,
    onNavigateToMessages: (String) -> Unit
) {
    BidsList(bids = bids, viewModel = null, onNavigateToMessages = onNavigateToMessages)
}

@Composable
private fun CancelledBids(
    bids: List<JobProposal>,
    onNavigateToMessages: (String) -> Unit
) {
    BidsList(bids = bids, viewModel = null, onNavigateToMessages = onNavigateToMessages)
}

@Composable
private fun BidsList(
    bids: List<JobProposal>,
    viewModel: JobViewModel?,
    onNavigateToMessages: (String) -> Unit
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
                    onNavigateToMessages(clientId)
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
