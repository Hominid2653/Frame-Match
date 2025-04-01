package com.app.fm001.ui.screens.client.dashboard

import JobPost
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fm001.model.EventType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ClientJobsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _myJobs = MutableStateFlow<List<JobPost>>(emptyList())
    val myJobs: StateFlow<List<JobPost>> get() = _myJobs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            if (userId != null) {
                db.collection("jobs")
                    .whereEqualTo("clientId", userId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val jobs = documents.mapNotNull { doc ->
                            try {
                                JobPost(
                                    id = doc.id,
                                    title = doc.getString("title") ?: "",
                                    description = doc.getString("description") ?: "",
                                    budget = doc.getDouble("budget") ?: 0.0,
                                    location = doc.getString("location") ?: "",
                                    eventDate = doc.getDate("eventDate") ?: Date(),
                                    eventType = safeEventTypeParse(doc.getString("eventType")),
                                    requirements = doc.get("requirements") as? List<String> ?: emptyList(),
                                    clientId = doc.getString("clientId") ?: "",
                                    clientName = doc.getString("clientName") ?: "",
                                    postedDate = doc.getDate("postedDate") ?: Date(),
                                    postedBy = doc.getString("postedBy") ?: "Unknown",
                                    status = safeStatusParse(doc.getString("status"))
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        _myJobs.value = jobs
                        _isLoading.value = false
                    }
                    .addOnFailureListener { exception ->
                        _error.value = exception.message ?: "Failed to load jobs"
                        _isLoading.value = false
                    }
            } else {
                _error.value = "User not authenticated"
                _isLoading.value = false
            }
        }
    }

    fun createJob(
        title: String,
        description: String,
        budget: Double,
        location: String,
        eventDate: Date,
        eventType: EventType,
        requirements: List<String>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown"

            if (userId != null) {
                val job = hashMapOf(
                    "title" to title,
                    "description" to description,
                    "budget" to budget,
                    "location" to location,
                    "eventDate" to eventDate,
                    "eventType" to eventType.name,
                    "requirements" to requirements,
                    "clientId" to userId,
                    "clientName" to "Client Name",
                    "postedDate" to Date(),
                    "postedBy" to userEmail,
                    "status" to JobPost.Status.OPEN.name
                )

                db.collection("jobs")
                    .add(job)
                    .addOnSuccessListener {
                        fetchJobs()
                    }
                    .addOnFailureListener { exception ->
                        _error.value = exception.message ?: "Failed to create job"
                    }
                    .addOnCompleteListener {
                        _isLoading.value = false
                    }
            } else {
                _error.value = "User not authenticated"
                _isLoading.value = false
            }
        }
    }

    private fun safeStatusParse(status: String?): JobPost.Status {
        return try {
            JobPost.Status.valueOf(status ?: "OPEN")
        } catch (e: IllegalArgumentException) {
            JobPost.Status.OPEN
        }
    }

    private fun safeEventTypeParse(eventType: String?): EventType {
        return try {
            EventType.valueOf(eventType ?: "OTHER")
        } catch (e: IllegalArgumentException) {
            EventType.OTHER
        }
    }
}