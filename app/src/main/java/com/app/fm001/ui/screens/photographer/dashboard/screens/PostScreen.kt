package com.app.fm001.ui.screens.photographer.dashboard.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.fm001.utils.encodeImageToBase64
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen() {
    var description by remember { mutableStateOf("") }
    var selectedHashtags by remember { mutableStateOf(setOf<String>()) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }
    var isPosting by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Loading...") }

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid
    val userEmail = user?.email

    LaunchedEffect(userId) {
        userId?.let { fetchUserName(it) { name -> userName = name } }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImage = uri
        base64Image = uri?.let { encodeImageToBase64(it, context) }.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Posting as: $userName", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { launcher.launch("image/*") },
            shape = MaterialTheme.shapes.medium
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImage == null) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add photos", modifier = Modifier.size(48.dp))
                    Text("Add Photo", style = MaterialTheme.typography.bodyLarge)
                } else {
                    AsyncImage(model = selectedImage, contentDescription = "Selected Image", modifier = Modifier.fillMaxSize())
                    IconButton(
                        onClick = { selectedImage = null; base64Image = null },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove Image", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Hashtags", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        val availableHashtags = listOf("Wedding", "Portrait", "Nature", "Street", "Fashion", "Event", "Sports", "Architecture", "Food", "Travel")
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(200.dp)
        ) {
            items(availableHashtags.size) { index ->
                val hashtag = availableHashtags[index]
                FilterChip(
                    selected = selectedHashtags.contains(hashtag),
                    onClick = {
                        selectedHashtags = if (selectedHashtags.contains(hashtag)) selectedHashtags - hashtag else selectedHashtags + hashtag
                    },
                    label = { Text("#$hashtag") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (userId == null || userEmail == null) {
                    Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (selectedImage == null) {
                    Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isPosting = true
                val post = hashMapOf(
                    "userId" to userId,
                    "email" to userEmail,
                    "userName" to userName,
                    "imageBase64" to base64Image,
                    "description" to description,
                    "hashtags" to selectedHashtags.toList(),
                    "likes" to 0,
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("posts").add(post)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Post uploaded!", Toast.LENGTH_SHORT).show()
                        description = ""
                        selectedHashtags = setOf()
                        selectedImage = null
                        base64Image = null
                        isPosting = false
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to upload: ${e.message}", Toast.LENGTH_LONG).show()
                        isPosting = false
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isPosting
        ) {
            if (isPosting) CircularProgressIndicator(modifier = Modifier.size(18.dp))
            else {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Post")
            }
        }
    }
}

fun fetchUserName(userId: String, onResult: (String) -> Unit) {
    FirebaseFirestore.getInstance().collection("profiles")
        .whereEqualTo("userId", userId)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val name = documents.documents[0].getString("name") ?: "Unknown"
                onResult(name)
            } else {
                onResult("Unknown")
            }
        }
        .addOnFailureListener {
            onResult("Unknown")
        }
}