package com.app.fm001.ui.screens.client.dashboard.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.fm001.ui.screens.client.dashboard.ClientProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay

@Composable
fun ClientHomeScreen(
    navController: NavController,
    viewModel: ClientProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val posts = remember { mutableStateListOf<DocumentSnapshot>() }
    var userProfileImage by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf<String?>(null) }
    val preferredTags by viewModel.selectedTags.collectAsState()
    val userNamesCache = remember { mutableStateMapOf<String, String>() }

    fun fetchPosts() {
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                posts.clear()
                snapshots?.forEach { document ->
                    val postHashtags = document.get("hashtags") as? List<String> ?: emptyList()
                    val normalizedPostHashtags = postHashtags.map { it.uppercase() }
                    val normalizedPreferredTags = preferredTags.map { it.name.uppercase() }
                    if (preferredTags.isEmpty() || normalizedPostHashtags.any { it in normalizedPreferredTags }) {
                        posts.add(document)
                    }
                }
            }
    }

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { document ->
                userProfileImage = document.getString("profileImage")
                userName = document.getString("name") ?: currentUser?.email?.substringBefore("@")
            }
            fetchPosts()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = userName ?: currentUser?.email?.substringBefore("@") ?: "Guest",
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
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        items(posts) { document ->
            val post = document.data
            val id = document.id
            val description = post?.get("description") as? String ?: ""
            val hashtags = post?.get("hashtags") as? List<String> ?: emptyList()
            val imageBase64 = post?.get("imageBase64") as? String ?: ""
            val posterEmail = post?.get("email") as? String ?: "Unknown"
            val posterName = post?.get("userName") as? String ?: posterEmail
            val likes = (post?.get("likes") as? Long)?.toInt() ?: 0
            val likedBy = post?.get("likedBy") as? List<String> ?: emptyList()
            val hasLiked = currentUser?.uid in likedBy

            FeedPostCard(
                id = id,
                description = description,
                hashtags = hashtags,
                imageBase64 = imageBase64,
                posterName = posterName,
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
                },
                onClick = {
                    navController.navigate("portfolio/$posterEmail")
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
    hashtags: List<String>,
    imageBase64: String,
    posterName: String,
    likes: Int,
    hasLiked: Boolean,
    onLike: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Poster name at the top
            Text(
                text = posterName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Post Image
            imageBase64.takeIf { it.isNotEmpty() }?.let {
                val bitmap = decodeBase64ToBitmap(it)
                bitmap?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Post Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(4f / 3f)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Description
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Hashtags
            if (hashtags.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .wrapContentWidth()
                ) {
                    hashtags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            // Like button and count below hashtags
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = onLike,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (hasLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (hasLiked) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = likes.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}