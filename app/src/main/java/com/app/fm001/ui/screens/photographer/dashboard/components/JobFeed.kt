package com.app.fm001.ui.screens.photographer.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.fm001.model.JobProposal
import com.app.fm001.model.EventType
import java.util.Date
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.app.fm001.model.ProposalStatus

@Composable
fun JobFeed(
    proposals: List<JobProposal>,
    onApplyClick: (JobProposal) -> Unit,
    onMessageClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search jobs...") },
            leadingIcon = { 
                Icon(Icons.Default.Search, contentDescription = null)
            },
            singleLine = true
        )

        // Filter chips
        ScrollableFilterChips()

        Spacer(modifier = Modifier.height(16.dp))

        // Job list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(proposals) { proposal ->
                JobProposalCard(
                    proposal = proposal,
                    onApplyClick = onApplyClick,
                    onMessageClick = onMessageClick
                )
            }
        }
    }
}

@Composable
private fun ScrollableFilterChips() {
    val filters = remember {
        listOf(
            "All",
            "Wedding",
            "Birthday",
            "Corporate",
            "Graduation",
            "Other"
        )
    }
    var selectedFilter by remember { mutableStateOf("All") }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { selectedFilter = filter },
                label = { Text(filter) },
                leadingIcon = if (selectedFilter == filter) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null
            )
        }
    }
}

private fun getDummyProposals(): List<JobProposal> {
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
            clientName = "Sarah Johnson",
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
            status = ProposalStatus.IN_PROGRESS
        )
    )
} 