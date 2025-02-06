package com.app.fm001.ui.screens.photographer.dashboard.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.app.fm001.model.EventType
import com.app.fm001.model.JobProposal
import com.app.fm001.model.ProposalStatus
import com.app.fm001.ui.screens.photographer.dashboard.components.JobFeed
import java.util.Date

@Composable
fun HomeScreen(
    onNavigateToMessages: (String) -> Unit = {}
) {
    val proposals = remember { getDummyProposals() }
    
    Box(modifier = Modifier.fillMaxSize()) {
        JobFeed(
            proposals = proposals,
            onApplyClick = { /* Handle apply click */ },
            onMessageClick = onNavigateToMessages
        )
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