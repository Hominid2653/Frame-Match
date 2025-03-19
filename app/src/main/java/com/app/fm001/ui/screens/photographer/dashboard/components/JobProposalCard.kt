package com.app.fm001.ui.screens.photographer.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.fm001.model.EventType
import com.app.fm001.model.JobProposal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JobProposalCard(
    proposal: JobProposal,
    onApplyClick: (JobProposal) -> Unit, // Accepts JobProposal as a parameter
    onMessageClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = proposal.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = proposal.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "Budget: $${proposal.budget}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Location: ${proposal.location}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Event Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(proposal.eventDate)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Event Type: ${proposal.eventType.name}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            if (proposal.requirements.isNotEmpty()) {
                Text(
                    text = "Requirements:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                proposal.requirements.forEach { requirement ->
                    Text(
                        text = "• $requirement",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { onApplyClick(proposal) }) {
                    Text("Apply")
                }
                Button(onClick = { onMessageClick(proposal.clientId) }) {
                    Text("Message")
                }
            }
        }
    }
}