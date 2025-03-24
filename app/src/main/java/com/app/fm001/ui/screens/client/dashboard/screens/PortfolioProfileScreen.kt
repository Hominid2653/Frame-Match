package com.app.fm001.ui.screens.client.dashboard.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.app.fm001.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioProfileScreen(navController: NavController, email: String) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("No Name") }
    var bio by remember { mutableStateOf("No Bio") }
    var profileImage by remember { mutableStateOf<String?>(null) }
    var userPosts by remember { mutableStateOf(emptyList<Post>()) }
    var totalLikes by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(email) {
        isLoading = true
        println("Fetching profile for email: $email") // Debugging

        // Fetch photographer's profile
        db.collection("profiles")
            .whereEqualTo("email", email) // Filter by email
            .get()
            .addOnSuccessListener { documents ->
                isLoading = false
                println("Profile documents: ${documents.size()}") // Debugging
                if (!documents.isEmpty) {
                    val profile = documents.documents[0]
                    name = profile.getString("name") ?: "No Name"
                    bio = profile.getString("bio") ?: "No Bio"
                    profileImage = profile.getString("profileImage")
                    println("Profile fetched: $name, $bio, $profileImage") // Debugging
                } else {
                    println("No profile found for email: $email") // Debugging
                }
            }
            .addOnFailureListener {
                isLoading = false
                println("Failed to fetch profile: ${it.message}") // Debugging
                Toast.makeText(context, "Failed to fetch profile", Toast.LENGTH_SHORT).show()
            }

        // Fetch photographer's posts
        db.collection("posts")
            .whereEqualTo("email", email) // Filter posts by email
            .get()
            .addOnSuccessListener { documents ->
                isLoading = false
                println("Post documents: ${documents.size()}") // Debugging
                var likesSum = 0
                userPosts = documents.mapNotNull { doc ->
                    val id = doc.id
                    val imageBase64 = doc.getString("imageBase64") ?: ""
                    val description = doc.getString("description") ?: ""
                    val likes = doc.getLong("likes")?.toInt() ?: 0

                    likesSum += likes
                    Post(id, imageBase64, description, likes)
                }
                totalLikes = likesSum
                println("Posts fetched: ${userPosts.size}, Total likes: $totalLikes") // Debugging
            }
            .addOnFailureListener {
                isLoading = false
                println("Failed to fetch posts: ${it.message}") // Debugging
                Toast.makeText(context, "Failed to fetch posts", Toast.LENGTH_SHORT).show()
            }
    }

    // Display loading indicator
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // UI for PortfolioProfileScreen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Profile Header
            ProfileHeader(
                name = name,
                bio = bio,
                profileImage = profileImage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Section
            StatsSection(totalLikes = totalLikes, postCount = userPosts.size)

            Spacer(modifier = Modifier.height(16.dp))

            // Portfolio Grid
            Text(
                text = "Your Posts",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (userPosts.isEmpty()) {
                Text(
                    text = "No posts found for this photographer.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                PhotoGrid(posts = userPosts)
            }
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    bio: String,
    profileImage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!profileImage.isNullOrEmpty()) {
                val imageBitmap = remember { profileImage.toBitmap() }
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap.asImageBitmap(),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Default Profile Image",
                        modifier = Modifier.size(120.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Profile Image",
                    modifier = Modifier.size(120.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bio
        Text(
            text = bio,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun StatsSection(totalLikes: Int, postCount: Int) {
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
                value = postCount.toString(),
                label = "Posts"
            )
            StatItem(
                icon = Icons.Default.Star,
                value = totalLikes.toString(),
                label = "Likes"
            )
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String) {
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
fun PhotoGrid(posts: List<Post>) {
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
fun PhotoGridItem(post: Post) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
    ) {
        val imageBitmap = remember { post.imageBase64.toBitmap() }
        imageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun String.toBitmap(): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        Log.e("Base64Decode", "Failed to decode Base64", e)
        null
    }
}