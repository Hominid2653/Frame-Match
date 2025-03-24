package com.app.fm001.ui.screens.client.dashboard.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.app.fm001.model.ClientSettings
import com.app.fm001.model.EventType
import com.app.fm001.ui.screens.client.dashboard.ClientProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(
    viewModel: ClientProfileViewModel,
    onNavigateToPrivacySettings: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ProfileHeader(
            name = profile.name,
            bio = profile.bio,
            profileImage = profile.profileImage.orEmpty(),
            onEditClick = { showEditProfileDialog = true }
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = "Photography Preferences",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PreferredTagsSection(
            selectedTags = selectedTags,
            onTagSelected = viewModel::toggleTag
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = "Account Settings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AccountSettingsSection(
            settings = settings,
            onNavigateToPrivacySettings = onNavigateToPrivacySettings,
            onToggleNotifications = viewModel::updateNotifications,
            onLogout = viewModel::logout
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            name = profile.name,
            bio = profile.bio,
            profileImage = profile.profileImage.orEmpty(),
            onDismiss = { showEditProfileDialog = false },
            onSave = viewModel::updateProfile
        )
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    bio: String,
    profileImage: String,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.padding(bottom = 16.dp)) {
            profileImage.takeIf { it.isNotEmpty() }?.let {
                decodeBase64ToBitmapProfile(it)?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Profile picture",
                        modifier = Modifier.size(120.dp).clip(CircleShape)
                    )
                }
            } ?: run {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default profile picture",
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                )
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Edit, "Edit profile")
            }
        }
        Text(text = name, style = MaterialTheme.typography.headlineMedium)
        Text(text = bio, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    settings: ClientSettings,
    onNavigateToPrivacySettings: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ListItem(
            headlineContent = { Text("Notifications") },
            leadingContent = { Icon(Icons.Default.Notifications, null) },
            trailingContent = {
                Switch(
                    checked = settings.notificationsEnabled,
                    onCheckedChange = onToggleNotifications
                )
            }
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
            trailingContent = { Text(settings.language) }
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
            ),
            modifier = Modifier.clickable(onClick = onLogout)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    name: String,
    bio: String,
    profileImage: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String?) -> Unit
) {
    var updatedName by remember { mutableStateOf(name) }
    var updatedBio by remember { mutableStateOf(bio) }
    var updatedProfileImage by remember { mutableStateOf(profileImage) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                profileImage.takeIf { it.isNotEmpty() }?.let {
                    decodeBase64ToBitmapProfile(it)?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Profile picture",
                            modifier = Modifier.size(120.dp).clip(CircleShape)
                        )
                    }
                } ?: run {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Default profile picture",
                        modifier = Modifier.size(120.dp).clip(CircleShape)
                    )
                }
                OutlinedTextField(
                    value = updatedName,
                    onValueChange = { updatedName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = updatedBio,
                    onValueChange = { updatedBio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(updatedName, updatedBio, updatedProfileImage) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun decodeBase64ToBitmapProfile(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}