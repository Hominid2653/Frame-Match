package com.app.fm001.ui.screens.client.dashboard.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.fm001.model.PhotographerProfile
import com.app.fm001.ui.screens.client.dashboard.SearchPhotographersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPhotographersScreen(
    viewModel: SearchPhotographersViewModel = viewModel(),
    onNavigateToPhotographer: (String) -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategories by viewModel.selectedCategories.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val photographers by viewModel.photographers.collectAsState()
    var showLocationDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            onSearch = { },
            active = false,
            onActiveChange = { },
            placeholder = { Text("Search photographers...") },
            modifier = Modifier.fillMaxWidth()
        ) { }

        // Location and Categories Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Location Filter
            FilterChip(
                selected = selectedLocation != null,
                onClick = { showLocationDialog = true },
                label = { 
                    Text(selectedLocation ?: "All Locations")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            )
        }

        // Categories
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = category in selectedCategories,
                    onClick = { viewModel.toggleCategory(category) },
                    label = { Text(category) },
                    leadingIcon = if (category in selectedCategories) {
                        {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
                )
            }
        }

        // Photographers List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(photographers) { photographer ->
                PhotographerCard(
                    photographer = photographer,
                    onClick = { onNavigateToPhotographer(photographer.id) }
                )
            }
        }
    }

    if (showLocationDialog) {
        LocationSelectionDialog(
            currentLocation = selectedLocation,
            locations = viewModel.getAvailableLocations(),
            onLocationSelected = { 
                viewModel.setLocation(it)
                showLocationDialog = false
            },
            onDismiss = { showLocationDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotographerCard(
    photographer: PhotographerProfile,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = photographer.profileImage,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = photographer.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (photographer.verified) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Text(
                    text = photographer.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${photographer.rating} (${photographer.reviewCount} reviews)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationSelectionDialog(
    currentLocation: String?,
    locations: List<String>,
    onLocationSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Location") },
        text = {
            LazyColumn {
                item {
                    ListItem(
                        headlineContent = { Text("All Locations") },
                        modifier = Modifier.clickable { onLocationSelected(null) }
                    )
                }
                items(locations) { location ->
                    ListItem(
                        headlineContent = { Text(location) },
                        leadingContent = if (location == currentLocation) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else null,
                        modifier = Modifier.clickable { onLocationSelected(location) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

private val categories = listOf(
    "Wedding", "Portrait", "Event", "Fashion", "Nature", "Architecture"
) 