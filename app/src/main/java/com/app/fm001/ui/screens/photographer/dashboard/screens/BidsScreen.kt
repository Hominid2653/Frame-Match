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
import com.app.fm001.model.EventType
import com.app.fm001.ui.screens.photographer.dashboard.components.JobProposalCard
import java.util.Date

@Composable
fun BidsScreen(
    onNavigateToMessages: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Active Bids", "Completed", "Cancelled")

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab row for filtering bids by status
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

        // Content based on selected tab
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> ActiveBids(onNavigateToMessages)
                1 -> CompletedBids(onNavigateToMessages)
                2 -> CancelledBids(onNavigateToMessages)
            }
        }
    }
}

@Composable
private fun ActiveBids(
    onNavigateToMessages: (String) -> Unit
) {
    val activeBids = remember { getDummyBids().filter { it.status == ProposalStatus.IN_PROGRESS } }
    BidsList(bids = activeBids, onNavigateToMessages = onNavigateToMessages)
}

@Composable
private fun CompletedBids(
    onNavigateToMessages: (String) -> Unit
) {
    val completedBids = remember { getDummyBids().filter { it.status == ProposalStatus.COMPLETED } }
    BidsList(bids = completedBids, onNavigateToMessages = onNavigateToMessages)
}

@Composable
private fun CancelledBids(
    onNavigateToMessages: (String) -> Unit
) {
    val cancelledBids = remember { getDummyBids().filter { it.status == ProposalStatus.CANCELLED } }
    BidsList(bids = cancelledBids, onNavigateToMessages = onNavigateToMessages)
}

@Composable
private fun BidsList(
    bids: List<JobProposal>,
    onNavigateToMessages: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(bids) { bid ->
            JobProposalCard(
                proposal = bid,
                onApplyClick = { /* Handle bid actions */ },
                onMessageClick = { clientId -> 
                    onNavigateToMessages(clientId)
                }
            )
        }
    }
}

private fun getDummyBids(): List<JobProposal> {
    return listOf(
        JobProposal(
            id = "1",
            title = "Wedding Photography Needed",
            description = "Looking for an experienced photographer for a beach wedding",
            budget = 1500.0,
            location = "Mombasa Beach Hotel",
            eventDate = Date(),
            eventType = EventType.WEDDING,
            requirements = listOf(
                "5 years experience",
                "Own equipment",
                "Portfolio required",
                "Full day coverage"
            ),
            clientId = "client_123",
            clientName = "John Doe",
            postedDate = Date(),
            status = ProposalStatus.IN_PROGRESS
        ),
        JobProposal(
            id = "2",
            title = "Corporate Event Coverage",
            description = "Annual company meeting and award ceremony",
            budget = 800.0,
            location = "Nairobi Business Center",
            eventDate = Date(),
            eventType = EventType.CORPORATE,
            requirements = listOf(
                "Professional equipment",
                "Quick turnaround",
                "Previous corporate experience"
            ),
            clientId = "client_456",
            clientName = "Tech Corp Ltd",
            postedDate = Date(),
            status = ProposalStatus.COMPLETED
        ),
        JobProposal(
            id = "3",
            title = "Birthday Party Photography",
            description = "Sweet 16 birthday party photography needed",
            budget = 500.0,
            location = "Karen Country Club",
            eventDate = Date(),
            eventType = EventType.BIRTHDAY,
            requirements = listOf(
                "Experience with party photography",
                "Good with kids",
                "4 hours coverage"
            ),
            clientId = "client_789",
            clientName = "John Smith",
            postedDate = Date(),
            status = ProposalStatus.CANCELLED
        )
    )
} 