package com.app.fm001.ui.screens.client.dashboard

import androidx.lifecycle.ViewModel
import com.app.fm001.model.PhotographerProfile
import com.app.fm001.utils.random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class SearchPhotographersViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategories = _selectedCategories.asStateFlow()

    private val _selectedLocation = MutableStateFlow<String?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    private val categories = listOf(
        "Wedding", "Portrait", "Event", "Fashion", "Nature", "Architecture"
    )

    private val locations = listOf(
        "Nairobi", "Mombasa", "Kisumu", "Nakuru", "Eldoret", "Thika", "Malindi"
    )

    private val _photographers = MutableStateFlow(getDummyPhotographers())
    val photographers = _photographers.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterPhotographers()
    }

    fun toggleCategory(category: String) {
        _selectedCategories.value = _selectedCategories.value.toMutableSet().apply {
            if (contains(category)) remove(category) else add(category)
        }
        filterPhotographers()
    }

    fun setLocation(location: String?) {
        _selectedLocation.value = location
        filterPhotographers()
    }

    private fun filterPhotographers() {
        val query = _searchQuery.value.lowercase()
        val categories = _selectedCategories.value
        val location = _selectedLocation.value
        
        val filtered = getDummyPhotographers().filter { photographer ->
            val matchesQuery = query.isEmpty() || 
                photographer.name.lowercase().contains(query) ||
                photographer.bio.lowercase().contains(query)
            
            val matchesCategories = categories.isEmpty() || 
                photographer.specialties.any { it in categories }
            
            val matchesLocation = location == null || 
                photographer.location == location
            
            matchesQuery && matchesCategories && matchesLocation
        }
        
        _photographers.value = filtered
    }

    private fun getDummyPhotographers(): List<PhotographerProfile> {
        return List(20) { index ->
            val selectedCategories = categories.toList().shuffled().take(Random.nextInt(2, 4))
            PhotographerProfile(
                id = "photographer_$index",
                name = "Photographer ${index + 1}",
                bio = "Professional photographer specializing in ${selectedCategories.take(2).joinToString(" and ")}",
                profileImage = "https://i.pravatar.cc/150?img=$index",
                rating = (3.5f..5.0f).random(),
                reviewCount = Random.nextInt(10, 201),
                verified = index % 3 == 0,
                specialties = selectedCategories,
                location = locations[index % locations.size]
            )
        }
    }

    fun getAvailableLocations() = locations.toList()
    
    fun getAvailableCategories() = categories.toList()
} 