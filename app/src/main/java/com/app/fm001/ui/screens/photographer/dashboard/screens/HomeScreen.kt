package com.app.fm001.ui.screens.photographer.dashboard.screens

import JobFeed
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.app.fm001.ui.screens.photographer.dashboard.JobViewModel

@Composable
fun HomeScreen(
    viewModel: JobViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToMessages: (String) -> Unit = {}
) {
    // Fetch jobs when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.fetchJobs()
    }

    // Collect the list of jobs from the ViewModel
    val jobs by viewModel.jobs.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        JobFeed(
            proposals = jobs.reversed(), // Reverse the list to show latest first
            onApplyClick = { job ->
                // Handle apply click (e.g., submit a proposal)
                viewModel.applyForJob(job.id)
            },
            onMessageClick = onNavigateToMessages
        )
    }
}