package com.app.fm001.ui.screens.photographer.dashboard.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.fm001.model.PhotographerProfile
import com.app.fm001.ui.screens.photographer.dashboard.Comment
import com.app.fm001.ui.screens.photographer.dashboard.PhotoPost
import com.app.fm001.ui.screens.photographer.dashboard.PortfolioViewModel

enum class PortfolioViewType {
    GRID, FEED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = viewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val profile by viewModel.photographerProfile.collectAsState()
    var viewType by remember { mutableStateOf(PortfolioViewType.GRID) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(profile = profile)
        StatsSection()
        
        // Portfolio Header with View Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Portfolio",
                style = MaterialTheme.typography.titleLarge
            )
            
            IconToggleButton(
                checked = viewType == PortfolioViewType.FEED,
                onCheckedChange = { isFeed ->
                    viewType = if (isFeed) PortfolioViewType.FEED else PortfolioViewType.GRID
                }
            ) {
                Icon(
                    imageVector = if (viewType == PortfolioViewType.GRID) 
                        Icons.Default.ViewList else Icons.Default.GridView,
                    contentDescription = "Toggle view"
                )
            }
        }
        
        when (viewType) {
            PortfolioViewType.GRID -> PhotoGrid(posts = posts)
            PortfolioViewType.FEED -> PhotoFeed(posts = posts, viewModel = viewModel)
        }
    }
}

@Composable
private fun ProfileHeader(profile: PhotographerProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        AsyncImage(
            model = profile.profileImage,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Name and Verification Badge
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = profile.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            if (profile.verified) {
                Icon(
                    Icons.Default.Verified,
                    contentDescription = "Verified Photographer",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                )
            }
        }
        
        // Bio
        Text(
            text = profile.bio,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // Rating
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = if (index < profile.rating) MaterialTheme.colorScheme.primary 
                          else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "${profile.rating} (${profile.reviewCount} reviews)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Replace the single contact button with two buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Edit Profile Button
            OutlinedButton(
                onClick = { /* TODO: Implement edit profile */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile")
            }
            
            // Inbox Button
            Button(
                onClick = { /* TODO: Implement inbox navigation */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inbox")
            }
        }
    }
}

@Composable
private fun StatsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.PhotoLibrary,
                value = "156",
                label = "Photos"
            )
            StatItem(
                icon = Icons.Default.WorkHistory,
                value = "48",
                label = "Jobs"
            )
            StatItem(
                icon = Icons.Default.Star,
                value = "124",
                label = "Reviews"
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PhotoGrid(posts: List<PhotoPost>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(1.dp),
        modifier = Modifier.height(800.dp) // Make it scrollable within the Column
    ) {
        items(posts) { post ->
            PhotoGridItem(post = post)
        }
    }
}

@Composable
private fun PhotoGridItem(post: PhotoPost) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
    ) {
        AsyncImage(
            model = post.images.firstOrNull(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PhotoFeed(posts: List<PhotoPost>, viewModel: PortfolioViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        posts.forEach { post ->
            PhotoFeedItem(post = post, viewModel = viewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PhotoFeedItem(
    post: PhotoPost,
    viewModel: PortfolioViewModel
) {
    var showComments by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var showShareSheet by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column {
            // Image
            AsyncImage(
                model = post.images.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f/3f)
            )
            
            // Interaction buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Like button
                    IconButton(onClick = { viewModel.toggleLike(post.id) }) {
                        Icon(
                            if (post.isLiked) Icons.Filled.Favorite 
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) MaterialTheme.colorScheme.primary 
                                  else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Comment button
                    IconButton(onClick = { showComments = !showComments }) {
                        Icon(
                            Icons.Default.Comment,
                            contentDescription = "Comments"
                        )
                    }
                }
                
                // Share button
                IconButton(onClick = { showShareSheet = true }) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share"
                    )
                }
            }
            
            // Like count
            Text(
                text = "${post.likes} likes",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Post details
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Hashtags
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    post.hashtags.forEach { hashtag ->
                        AssistChip(
                            onClick = { },
                            label = { Text("#$hashtag") }
                        )
                    }
                }
                
                // Description
                if (post.description.isNotEmpty()) {
                    Text(
                        text = post.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // Timestamp
                Text(
                    text = formatTimestamp(post.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                
                // Comments section
                if (showComments) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        post.comments.forEach { comment ->
                            CommentItem(comment)
                        }
                        
                        // Comment input
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                placeholder = { Text("Add a comment...") },
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            IconButton(
                                onClick = {
                                    if (commentText.isNotEmpty()) {
                                        viewModel.addComment(post.id, commentText)
                                        commentText = ""
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Post comment")
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showShareSheet) {
        ShareSheet(
            onDismiss = { showShareSheet = false },
            onShare = { /* TODO: Implement sharing */ }
        )
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = comment.username,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareSheet(
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Share via",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            ListItem(
                headlineContent = { Text("Copy Link") },
                leadingContent = { Icon(Icons.Default.Link, contentDescription = null) },
                modifier = Modifier.clickable { /* TODO: Implement copy link */ }
            )
            ListItem(
                headlineContent = { Text("Share to Instagram") },
                leadingContent = { Icon(Icons.Default.Image, contentDescription = null) },
                modifier = Modifier.clickable { /* TODO: Implement Instagram sharing */ }
            )
            ListItem(
                headlineContent = { Text("More Options") },
                leadingContent = { Icon(Icons.Default.Share, contentDescription = null) },
                modifier = Modifier.clickable { onShare() }
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        else -> "${diff / 86400_000}d ago"
    }
} 