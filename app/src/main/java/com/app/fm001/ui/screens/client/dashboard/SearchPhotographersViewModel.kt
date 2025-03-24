package com.app.fm001.ui.screens.client.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fm001.model.PhotographerProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchPhotographersViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // State for search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // State for selected categories
    private val _selectedCategories = MutableStateFlow(setOf<String>())
    val selectedCategories: StateFlow<Set<String>> = _selectedCategories.asStateFlow()

    // State for selected location
    private val _selectedLocation = MutableStateFlow<String?>(null)
    val selectedLocation: StateFlow<String?> = _selectedLocation.asStateFlow()

    // State for all photographers (original list)
    private val _allPhotographers = MutableStateFlow<List<PhotographerProfile>>(emptyList())

    // State for filtered photographers
    private val _photographers = MutableStateFlow<List<PhotographerProfile>>(emptyList())
    val photographers: StateFlow<List<PhotographerProfile>> = _photographers.asStateFlow()

    init {
        Log.d("SearchPhotographersViewModel", "Initializing ViewModel")
        fetchPhotographers()
    }

    // Fetch photographers from Firestore
    private fun fetchPhotographers() {
        Log.d("SearchPhotographersViewModel", "Fetching photographers from Firestore")
        viewModelScope.launch {
            db.collection("profiles")
                .get()
                .addOnSuccessListener { documents ->
                    val photographerList = documents.mapNotNull { document ->
                        PhotographerProfile(
                            id = document.id,
                            userId = document.getString("userId") ?: "",
                            name = document.getString("name") ?: "No Name",
                            bio = document.getString("bio") ?: "No Bio",
                            profileImage = document.getString("profileImage") ?: "",
                            rating = document.getDouble("rating")?.toFloat() ?: 0f,
                            reviewCount = document.getLong("reviewCount")?.toInt() ?: 0,
                            verified = document.getBoolean("verified") ?: false,
                            specialties = document.get("specialties") as? List<String> ?: emptyList(),
                            location = document.getString("location") ?: ""
                        )
                    }
                    Log.d("SearchPhotographersViewModel", "Fetched ${photographerList.size} photographers")
                    _allPhotographers.value = photographerList // Store the original list
                    filterPhotographers() // Apply initial filtering
                }
                .addOnFailureListener {
                    Log.e("SearchPhotographersViewModel", "Failed to fetch photographers: ${it.message}")
                }
        }
    }

    // Update search query
    fun onSearchQueryChange(query: String) {
        Log.d("SearchPhotographersViewModel", "Search query changed to: $query")
        _searchQuery.value = query
        filterPhotographers()
    }

    // Toggle category selection
    fun toggleCategory(category: String) {
        Log.d("SearchPhotographersViewModel", "Toggling category: $category")
        _selectedCategories.value = if (category in _selectedCategories.value) {
            _selectedCategories.value - category
        } else {
            _selectedCategories.value + category
        }
        filterPhotographers()
    }

    // Set selected location
    fun setLocation(location: String?) {
        Log.d("SearchPhotographersViewModel", "Selected location: $location")
        _selectedLocation.value = location
        filterPhotographers()
    }

    // Filter photographers based on search query, categories, and location
    private fun filterPhotographers() {
        Log.d("SearchPhotographersViewModel", "Filtering photographers")
        Log.d("SearchPhotographersViewModel", "Search query: ${_searchQuery.value}")
        Log.d("SearchPhotographersViewModel", "Selected categories: ${_selectedCategories.value}")
        Log.d("SearchPhotographersViewModel", "Selected location: ${_selectedLocation.value}")

        val filtered = if (_searchQuery.value.isEmpty() && _selectedCategories.value.isEmpty() && _selectedLocation.value == null) {
            // If no filters are applied, return the full list of photographers
            Log.d("SearchPhotographersViewModel", "No filters applied, showing all photographers")
            _allPhotographers.value
        } else {
            // Apply filters
            _allPhotographers.value.filter { photographer ->
                val matchesQuery = _searchQuery.value.isEmpty() ||
                        photographer.name.contains(_searchQuery.value, ignoreCase = true) ||
                        photographer.bio.contains(_searchQuery.value, ignoreCase = true)

                // Convert specialties and selected categories to lowercase for case-insensitive comparison
                val photographerSpecialties = photographer.specialties.map { it.lowercase() }
                val selectedCategoriesLowercase = _selectedCategories.value.map { it.lowercase() }

                val matchesCategories = _selectedCategories.value.isEmpty() ||
                        photographerSpecialties.any { it in selectedCategoriesLowercase }

                val matchesLocation = _selectedLocation.value == null ||
                        photographer.location.contains(_selectedLocation.value!!, ignoreCase = true)

                matchesQuery && matchesCategories && matchesLocation
            }
        }
        Log.d("SearchPhotographersViewModel", "Filtered photographers count: ${filtered.size}")
        _photographers.value = filtered
    }

    // Fetch a photographer by userId
    fun getPhotographerById(userId: String): Flow<PhotographerProfile?> = flow {
        try {
            Log.d("SearchPhotographersViewModel", "Fetching photographer by userId: $userId")
            val document = db.collection("profiles").document(userId).get().await()
            val photographer = document.toObject(PhotographerProfile::class.java)
            emit(photographer)
        } catch (e: Exception) {
            Log.e("SearchPhotographersViewModel", "Failed to fetch photographer: ${e.message}")
            emit(null)
        }
    }

    // Get available locations (example implementation)
    fun getAvailableLocations(): List<String> {
        return listOf("Nairobi", "Nakuru", "Eldoret", "Kisumu", "Kapsabet")
    }
}