package com.app.fm001.ui.screens.client.dashboard.screens

import JobPost
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.fm001.model.EventType
import com.app.fm001.ui.screens.client.dashboard.ClientJobsViewModel
import com.app.fm001.ui.screens.client.dashboard.components.CreateJobDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientJobsScreen(
    viewModel: ClientJobsViewModel = viewModel()
) {
    var showCreateJobDialog by remember { mutableStateOf(false) }
    val myJobs by viewModel.myJobs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchJobs()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateJobDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Job")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Jobs",
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                myJobs.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No jobs found")
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(top = 16.dp)
                    ) {
                        items(myJobs) { job ->
                            JobCard(job = job)
                        }
                    }
                }
            }
        }
    }

    if (showCreateJobDialog) {
        CreateJobDialog(
            onDismiss = { showCreateJobDialog = false },
            onJobCreated = { jobDetails ->
                viewModel.createJob(
                    title = jobDetails.title,
                    description = jobDetails.description,
                    budget = jobDetails.budget,
                    location = jobDetails.location,
                    eventDate = jobDetails.eventDate,
                    eventType = EventType.valueOf(jobDetails.eventType.toString()),
                    requirements = jobDetails.requirements
                )
                showCreateJobDialog = false
            }
        )
    }
}

@Composable
private fun JobCard(job: JobPost) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.titleLarge
                )
                AssistChip(
                    onClick = { },
                    label = { Text(job.status.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (job.status) {
                            JobPost.Status.OPEN -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            JobPost.Status.IN_PROGRESS -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            JobPost.Status.COMPLETED -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                            JobPost.Status.CANCELLED -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            JobPost.Status.PENDING -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                        }
                    )
                )
            }

            Text(
                text = job.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Budget: $${job.budget}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Location: ${job.location}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Event Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(job.eventDate)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Event Type: ${job.eventType.name}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Posted By: ${job.postedBy}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (job.requirements.isNotEmpty()) {
                Text(
                    text = "Requirements:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                job.requirements.forEach { requirement ->
                    Text(
                        text = "• $requirement",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}