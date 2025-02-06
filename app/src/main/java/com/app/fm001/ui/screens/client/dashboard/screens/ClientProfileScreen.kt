package com.app.fm001.ui.screens.client.dashboard.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.fm001.model.EventType
import com.app.fm001.ui.screens.client.dashboard.ClientProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(
    viewModel: ClientProfileViewModel,
    onNavigateToPrivacySettings: () -> Unit
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile Header
        ProfileHeader(
            onEditClick = { showEditProfileDialog = true }
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Preferred Tags Section
        Text(
            text = "Photography Preferences",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PreferredTagsSection(
            selectedTags = viewModel.selectedTags.collectAsState().value,
            onTagSelected = viewModel::toggleTag
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Account Settings
        Text(
            text = "Account Settings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AccountSettingsSection(onNavigateToPrivacySettings)
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, bio ->
                // TODO: Implement save profile logic
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
private fun ProfileHeader(
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            AsyncImage(
                model = "https://i.pravatar.cc/150",
                contentDescription = "Profile picture",
                modifier = Modifier.size(120.dp)
            )
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Edit, "Edit profile")
            }
        }
        
        Text(
            text = "John Doe",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Photography enthusiast",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun PreferredTagsSection(
    selectedTags: Set<EventType>,
    onTagSelected: (EventType) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EventType.values().forEach { tag ->
            FilterChip(
                selected = tag in selectedTags,
                onClick = { onTagSelected(tag) },
                label = { Text(tag.name) },
                leadingIcon = if (tag in selectedTags) {
                    { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                } else null
            )
        }
    }
}

@Composable
private fun AccountSettingsSection(
    onNavigateToPrivacySettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ListItem(
            headlineContent = { Text("Notifications") },
            leadingContent = { Icon(Icons.Default.Notifications, null) },
            trailingContent = { Switch(checked = true, onCheckedChange = {}) }
        )
        
        ListItem(
            headlineContent = { Text("Privacy Settings") },
            leadingContent = { Icon(Icons.Default.Lock, null) },
            trailingContent = { Icon(Icons.Default.ChevronRight, null) },
            modifier = Modifier.clickable(onClick = onNavigateToPrivacySettings)
        )
        
        ListItem(
            headlineContent = { Text("Language") },
            leadingContent = { Icon(Icons.Default.Language, null) },
            trailingContent = { Text("English") }
        )
        
        ListItem(
            headlineContent = { Text("Help & Support") },
            leadingContent = { Icon(Icons.Default.Help, null) }
        )
        
        ListItem(
            headlineContent = { Text("Logout") },
            leadingContent = { Icon(Icons.Default.Logout, null) },
            colors = ListItemDefaults.colors(
                headlineColor = MaterialTheme.colorScheme.error,
                leadingIconColor = MaterialTheme.colorScheme.error
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, bio: String) -> Unit
) {
    var name by remember { mutableStateOf("John Doe") }
    var bio by remember { mutableStateOf("Photography enthusiast") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, bio) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 