package com.app.fm001.ui.screens.photographer.dashboard.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    modifier: Modifier = Modifier
) {
    var description by remember { mutableStateOf("") }
    var selectedHashtags by remember { mutableStateOf(setOf<String>()) }
    var selectedImages by remember { mutableStateOf(listOf<String>()) }

    // Temporary dummy data - will be replaced with database data
    val availableHashtags = remember {
        listOf(
            "Wedding", "Portrait", "Nature", "Street", "Fashion",
            "Event", "Sports", "Architecture", "Food", "Travel"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Image upload section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImages.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = { /* TODO: Implement image picker */ }) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add photos",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Text("Add Photos", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        // Selected images preview
        if (selectedImages.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                items(selectedImages) { imageUri ->
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = {
                                selectedImages = selectedImages.filter { it != imageUri }
                            },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove image",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description field
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hashtags section
        Text(
            "Select Hashtags",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(200.dp)
        ) {
            items(availableHashtags) { hashtag ->
                FilterChip(
                    selected = selectedHashtags.contains(hashtag),
                    onClick = {
                        selectedHashtags = if (selectedHashtags.contains(hashtag)) {
                            selectedHashtags - hashtag
                        } else {
                            selectedHashtags + hashtag
                        }
                    },
                    label = { Text("#$hashtag") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Post button
        Button(
            onClick = { /* TODO: Implement post functionality */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Post")
        }
    }
} 