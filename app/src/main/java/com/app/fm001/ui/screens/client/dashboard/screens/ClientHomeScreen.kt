package com.app.fm001.ui.screens.client.dashboard.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ClientHomeScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val posts = remember { mutableStateListOf<DocumentSnapshot>() }
    var userProfileImage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { document ->
                userProfileImage = document.getString("profileImage")
            }
        }
        db.collection("posts").orderBy("timestamp").addSnapshotListener { snapshots, e ->
            if (e != null) return@addSnapshotListener
            posts.clear()
            snapshots?.forEach { document -> posts.add(document) }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = currentUser?.email ?: "Guest",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                leadingContent = {
                    AsyncImage(
                        model = userProfileImage,
                        contentDescription = "User Profile Image",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                },
                modifier = Modifier.padding(16.dp)
            )
        }

        items(posts) { document ->
            val post = document.data
            val id = document.id
            val description = post?.get("description") as? String ?: ""
            val hashtags = post?.get("hashtags") as? List<String> ?: emptyList() // Corrected retrieval
            val imageBase64 = post?.get("imageBase64") as? String ?: ""
            val posterEmail = post?.get("email") as? String ?: "Unknown"
            val likes = (post?.get("likes") as? Long)?.toInt() ?: 0
            val likedBy = post?.get("likedBy") as? List<String> ?: emptyList()
            val hasLiked = currentUser?.uid in likedBy

            FeedPostCard(
                id = id,
                description = description,
                hashtags = hashtags,  // Corrected to pass List<String>
                imageBase64 = imageBase64,
                posterEmail = posterEmail,
                likes = likes,
                hasLiked = hasLiked,
                onLike = {
                    if (!hasLiked && currentUser?.uid != null) {
                        val updatedLikes = likes + 1
                        val updatedLikedBy = likedBy + currentUser.uid
                        db.collection("posts").document(id)
                            .update(mapOf(
                                "likes" to updatedLikes,
                                "likedBy" to updatedLikedBy
                            ))
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FeedPostCard(
    id: String,
    description: String,
    hashtags: List<String>, // Corrected type
    imageBase64: String,
    posterEmail: String,
    likes: Int,
    hasLiked: Boolean,
    onLike: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            // Poster Email
            Text(
                text = "Posted by: $posterEmail",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Post Image
            imageBase64.takeIf { it.isNotEmpty() }?.let {
                val bitmap = decodeBase64ToBitmap(it)
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Post Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Likes Count
            Text(
                text = "$likes likes",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Description
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Hashtags - Corrected to display properly
            if (hashtags.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    hashtags.forEach { hashtag ->
                        AssistChip(
                            onClick = {},
                            label = { Text("#$hashtag") } // Fixed to properly show hashtags
                        )
                    }
                }
            }

            // Like Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onLike) {
                    Icon(
                        if (hasLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like"
                    )
                }
            }
        }
    }
}

/**
 * Decodes a Base64 string to a Bitmap.
 */
fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}
