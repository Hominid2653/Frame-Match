package com.app.fm001.ui.screens.client.dashboard.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.fm001.model.PhotographerProfile
import com.app.fm001.ui.components.PhotographerCard
import com.app.fm001.ui.screens.client.dashboard.MessagePhotographersViewModel

@Composable
fun MessagePhotographersScreen(
    loggedInUserId: String,
    loggedInUserEmail: String,
    onNavigateToMessages: (String, String) -> Unit,
    viewModel: MessagePhotographersViewModel = viewModel()
) {
    val photographers by viewModel.messagePhotographers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(loggedInUserId, loggedInUserEmail) {
        viewModel.loadMessagePhotographers(loggedInUserId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Photographers You've Messaged With",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (photographers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You haven't messaged any photographers yet")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(photographers) { photographer ->
                        PhotographerCard(
                            photographer = photographer,
                            onClick = {
                                onNavigateToMessages(loggedInUserId, photographer.userId)
                            }
                        )
                    }
                }
            }
        }
    }
}

// PhotographerCard remains the same as previous implementation