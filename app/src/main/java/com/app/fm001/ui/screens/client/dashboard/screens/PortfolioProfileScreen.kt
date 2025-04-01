package com.app.fm001.ui.screens.client.dashboard.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioProfileScreen(navController: NavController, email: String) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf(email.substringBefore("@")) }
    var bio by remember { mutableStateOf("") }
    var userPosts by remember { mutableStateOf(emptyList<Post>()) }
    var isLoading by remember { mutableStateOf(true) }
    var totalLikes by remember { mutableStateOf(0) }

    LaunchedEffect(email) {
        try {
            isLoading = true

            // Fetch user profile
            val profileSnapshot = db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            if (!profileSnapshot.isEmpty) {
                val profile = profileSnapshot.documents[0]
                name = profile.getString("name") ?:
                        profile.getString("username") ?:
                        email.substringBefore("@")
                bio = profile.getString("bio") ?: ""
            }

            // Fetch all posts
            val querySnapshot = db.collection("posts")
                .whereEqualTo("email", email)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            userPosts = querySnapshot.documents.mapNotNull { doc ->
                Post(
                    id = doc.id,
                    imageBase64 = doc.getString("imageBase64") ?: "",
                    description = doc.getString("description") ?: "",
                    likes = doc.getLong("likes")?.toInt() ?: 0
                )
            }

            // Calculate total likes
            totalLikes = userPosts.sumOf { it.likes }

        } catch (e: Exception) {
            Toast.makeText(context, "Error loading portfolio", Toast.LENGTH_SHORT).show()
            Log.e("PortfolioProfile", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Profile Header
            ProfileHeaderSection(
                name = name,
                bio = bio,
                postCount = userPosts.size,
                totalLikes = totalLikes
            )

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> FullScreenLoader()
                    userPosts.isEmpty() -> EmptyState()
                    else -> PhotoGrid(posts = userPosts)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderSection(
    name: String,
    bio: String,
    postCount: Int,
    totalLikes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Name
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Bio
        if (bio.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Stats Row
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Posts Count
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = postCount.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Posts",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Likes Count
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Total likes",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = totalLikes.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Likes",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            thickness = 1.dp
        )
    }
}

@Composable
private fun FullScreenLoader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No posts available", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun PhotoGrid(posts: List<Post>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(1.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(posts) { post ->
            PhotoGridItem(post = post)
        }
    }
}

@Composable
private fun PhotoGridItem(post: Post) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        val imageBitmap = remember(post.imageBase64) { post.imageBase64.toBitmap() }
        imageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Post Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.BrokenImage, contentDescription = "Failed to load image")
        }
    }
}

private fun String.toBitmap(): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        Log.e("ImageDecode", "Failed to decode image", e)
        null
    }
}

data class Post(
    val id: String = "",
    val imageBase64: String = "",
    val description: String = "",
    val likes: Int = 0
)