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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.fm001.ui.screens.client.dashboard.ClientHomeViewModel
import com.app.fm001.ui.screens.client.dashboard.FeedPost

@Composable
fun ClientHomeScreen(
    viewModel: ClientHomeViewModel = viewModel(),
    onNavigateToPhotographer: (String) -> Unit = {}
) {
    val feedPosts by viewModel.feedPosts.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(feedPosts) { feedPost ->
            FeedPostCard(
                feedPost = feedPost,
                onLike = { viewModel.likePost(feedPost.post.id) },
                onPhotographerClick = { onNavigateToPhotographer(feedPost.photographer.id) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedPostCard(
    feedPost: FeedPost,
    onLike: () -> Unit,
    onPhotographerClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column {
            // Photographer header
            ListItem(
                headlineContent = { 
                    Text(
                        text = feedPost.photographer.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                supportingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${feedPost.photographer.rating}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        if (feedPost.photographer.verified) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                leadingContent = {
                    AsyncImage(
                        model = feedPost.photographer.profileImage,
                        contentDescription = "Photographer profile picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onPhotographerClick),
                        contentScale = ContentScale.Crop
                    )
                },
                modifier = Modifier.clickable(onClick = onPhotographerClick)
            )

            // Post image
            AsyncImage(
                model = feedPost.post.images.firstOrNull(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f/3f),
                contentScale = ContentScale.Crop
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
                    IconButton(onClick = onLike) {
                        Icon(
                            if (feedPost.post.isLiked) Icons.Filled.Favorite 
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like"
                        )
                    }
                }
            }

            // Like count
            Text(
                text = "${feedPost.post.likes} likes",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Description and hashtags
            if (feedPost.post.description.isNotEmpty()) {
                Text(
                    text = feedPost.post.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Hashtags
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                feedPost.post.hashtags.forEach { hashtag ->
                    AssistChip(
                        onClick = { },
                        label = { Text("#$hashtag") }
                    )
                }
            }
        }
    }
} 