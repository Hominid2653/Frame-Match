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

    fun fetchJobs() {
        viewModelScope.launch {
            if (userId != null) {
                db.collection("jobs")
                    .whereEqualTo("clientId", userId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val jobs = documents.mapNotNull { doc ->
                            JobPost(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                description = doc.getString("description") ?: "",
                                budget = doc.getDouble("budget") ?: 0.0,
                                location = doc.getString("location") ?: "",
                                eventDate = doc.getDate("eventDate") ?: Date(),
                                eventType = EventType.valueOf(doc.getString("eventType") ?: "OTHER"),
                                requirements = doc.get("requirements") as? List<String> ?: emptyList(),
                                clientId = doc.getString("clientId") ?: "",
                                clientName = doc.getString("clientName") ?: "",
                                postedDate = doc.getDate("postedDate") ?: Date(),
                                postedBy = doc.getString("postedBy") ?: "Unknown", // Added field
                                status = JobPost.Status.valueOf(doc.getString("status") ?: "OPEN")
                            )
                        }
                        _myJobs.value = jobs
                    }
                    .addOnFailureListener {
                        // Handle error
                    }
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
                    "clientName" to "Client Name", // Replace with actual client name if available
                    "postedDate" to Date(),
                    "postedBy" to userEmail, // Added field
                    "status" to JobPost.Status.OPEN.name
                )

                db.collection("jobs")
                    .add(job)
                    .addOnSuccessListener {
                        fetchJobs() // Refresh the list after creating a new job
                    }
                    .addOnFailureListener {
                        // Handle error
                    }
            }
        }
    }
}