package com.app.fm001.ui.screens.client.dashboard.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.fm001.model.ClientSettings
import com.app.fm001.model.EventType
import com.app.fm001.model.Message
import com.app.fm001.navigation.Screen
import com.app.fm001.ui.screens.client.dashboard.ClientProfileViewModel
import com.app.fm001.ui.screens.shared.messages.MessagesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(
    navController: NavController,
    viewModel: ClientProfileViewModel = viewModel(),
    messagesViewModel: MessagesViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val conversations by messagesViewModel.conversations.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showConversationsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        messagesViewModel.fetchAllConversations()
    }

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
            onEditClick = { showEditProfileDialog = true },
            onInboxClick = { showConversationsDialog = true }
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
            onNavigateToPrivacySettings = { navController.navigate("privacy_settings") },
            onToggleNotifications = viewModel::updateNotifications,
            onNavigateToMessages = { showConversationsDialog = true },
            onNavigateToLogin = {
                viewModel.logout {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            name = profile.name,
            bio = profile.bio,
            profileImage = profile.profileImage.orEmpty(),
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, bio, image ->
                viewModel.updateProfile(name, bio, image)
                showEditProfileDialog = false
            }
        )
    }

    if (showConversationsDialog) {
        ConversationsDialog(
            conversations = conversations,
            currentUserId = viewModel.currentUserId,
            onConversationClick = { receiverId, receiverEmail ->
                navController.navigate("client_messages/${viewModel.currentUserId}/$receiverId")
                showConversationsDialog = false
            },
            onDismiss = { showConversationsDialog = false }
        )
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    bio: String,
    profileImage: String,
    onEditClick: () -> Unit,
    onInboxClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.padding(bottom = 16.dp)) {
            if (profileImage.isNotEmpty()) {
                decodeBase64ToBitmapProfile(profileImage)?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Profile picture",
                        modifier = Modifier.size(120.dp).clip(CircleShape)
                    )
                } ?: Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default profile picture",
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default profile picture",
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Edit, "Edit profile")
                }
            }
        }

        Text(text = name, style = MaterialTheme.typography.headlineMedium)

        IconButton(
            onClick = onInboxClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Messages",
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = bio,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
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
    settings: ClientSettings,
    onNavigateToPrivacySettings: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToLogin: () -> Unit
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
            headlineContent = { Text("Messages") },
            leadingContent = { Icon(Icons.Default.Email, null) },
            trailingContent = { Icon(Icons.Default.ChevronRight, null) },
            modifier = Modifier.clickable(onClick = onNavigateToMessages)
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
            modifier = Modifier.clickable(onClick = onNavigateToLogin)
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
                if (profileImage.isNotEmpty()) {
                    decodeBase64ToBitmapProfile(profileImage)?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Profile picture",
                            modifier = Modifier.size(120.dp).clip(CircleShape)
                        )
                    } ?: Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Default profile picture",
                        modifier = Modifier.size(120.dp).clip(CircleShape)
                    )
                } else {
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
            TextButton(onClick = {
                onSave(updatedName, updatedBio, updatedProfileImage)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ConversationsDialog(
    conversations: List<Message>,
    currentUserId: String,
    onConversationClick: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your Conversations") },
        text = {
            if (conversations.isEmpty()) {
                Text("No conversations yet", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn {
                    items(conversations.distinctBy {
                        if (it.senderId == currentUserId) it.receiverId else it.senderId
                    }) { message ->
                        val (otherUserId, otherUserEmail) = if (message.senderId == currentUserId) {
                            message.receiverId to message.receiverEmail
                        } else {
                            message.senderId to message.senderEmail
                        }

                        ListItem(
                            headlineContent = { Text(otherUserEmail) },
                            supportingContent = {
                                Text(
                                    message.content,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.clickable {
                                onConversationClick(otherUserId, otherUserEmail)
                            }
                        )
                        Divider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

private fun decodeBase64ToBitmapProfile(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}