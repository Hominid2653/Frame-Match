package com.app.fm001.ui.screens.client.dashboard.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.fm001.model.ProfileVisibility
import com.app.fm001.ui.screens.client.dashboard.ClientProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    viewModel: ClientProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    var showVisibilityDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ListItem(
                headlineContent = { Text("Profile Visibility") },
                supportingContent = { Text(settings.privacySettings.profileVisibility.name) },
                modifier = Modifier.clickable { showVisibilityDialog = true }
            )

            ListItem(
                headlineContent = { Text("Show Email") },
                trailingContent = {
                    Switch(
                        checked = settings.privacySettings.showEmail,
                        onCheckedChange = { viewModel.updatePrivacySettings(showEmail = it) }
                    )
                }
            )

            ListItem(
                headlineContent = { Text("Allow Messages") },
                trailingContent = {
                    Switch(
                        checked = settings.privacySettings.allowMessages,
                        onCheckedChange = { viewModel.updatePrivacySettings(allowMessages = it) }
                    )
                }
            )
        }
    }

    if (showVisibilityDialog) {
        AlertDialog(
            onDismissRequest = { showVisibilityDialog = false },
            title = { Text("Profile Visibility") },
            text = {
                Column {
                    ProfileVisibility.values().forEach { visibility ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updatePrivacySettings(visibility = visibility)
                                    showVisibilityDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.privacySettings.profileVisibility == visibility,
                                onClick = {
                                    viewModel.updatePrivacySettings(visibility = visibility)
                                    showVisibilityDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(visibility.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVisibilityDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 