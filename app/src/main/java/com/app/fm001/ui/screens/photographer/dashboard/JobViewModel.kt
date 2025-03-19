package com.app.fm001.ui.screens.photographer.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fm001.model.EventType
import com.app.fm001.model.JobProposal
import com.app.fm001.model.ProposalStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class JobViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _jobs = MutableStateFlow<List<JobProposal>>(emptyList())
    val jobs: StateFlow<List<JobProposal>> get() = _jobs

    private val _proposals = MutableStateFlow<List<JobProposal>>(emptyList())
    val proposals: StateFlow<List<JobProposal>> get() = _proposals

    fun fetchJobs() {
        viewModelScope.launch {
            db.collection("jobs")
                .get()
                .addOnSuccessListener { documents ->
                    val jobs = documents.mapNotNull { doc ->
                        JobProposal(
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
                            status = ProposalStatus.valueOf(doc.getString("status") ?: "OPEN")
                        )
                    }
                    _jobs.value = jobs
                }
                .addOnFailureListener {
                    // Handle error
                }
        }
    }
    fun updateProposalStatus(proposal: JobProposal, newStatus: ProposalStatus) {
        viewModelScope.launch {
            val proposalRef = db.collection("proposals").document(proposal.id)

            proposalRef.update("status", newStatus.name)
                .addOnSuccessListener {
                    // Update local state
                    val updatedProposals = _proposals.value.map {
                        if (it.id == proposal.id) it.copy(status = newStatus) else it
                    }
                    _proposals.value = updatedProposals
                }
                .addOnFailureListener {
                    // Handle error
                }
        }
    }


    fun applyForJob(jobId: String) {
        viewModelScope.launch {
            if (userId != null) {
                // Update the job status to IN_PROGRESS
                db.collection("jobs")
                    .document(jobId)
                    .update("status", ProposalStatus.IN_PROGRESS.name)
                    .addOnSuccessListener {
                        // Create a proposal for the photographer
                        val proposal = hashMapOf(
                            "jobId" to jobId,
                            "photographerId" to userId,
                            "status" to ProposalStatus.IN_PROGRESS.name,
                            "proposalDate" to Date()
                        )

                        db.collection("proposals")
                            .add(proposal)
                            .addOnSuccessListener {
                                // Refresh the jobs list
                                fetchJobs()
                            }
                            .addOnFailureListener {
                                // Handle error
                            }
                    }
                    .addOnFailureListener {
                        // Handle error
                    }
            }
        }
    }

    fun fetchProposals() {
        viewModelScope.launch {
            if (userId != null) {
                db.collection("proposals")
                    .whereEqualTo("photographerId", userId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val proposals = documents.mapNotNull { doc ->
                            val jobId = doc.getString("jobId") ?: ""
                            val status = ProposalStatus.valueOf(doc.getString("status") ?: "PENDING")
                            val proposalDate = doc.getDate("proposalDate") ?: Date()

                            // Fetch the corresponding job details
                            db.collection("jobs")
                                .document(jobId)
                                .get()
                                .addOnSuccessListener { jobDoc ->
                                    if (jobDoc.exists()) {
                                        val jobProposal = JobProposal(
                                            id = jobId,
                                            title = jobDoc.getString("title") ?: "",
                                            description = jobDoc.getString("description") ?: "",
                                            budget = jobDoc.getDouble("budget") ?: 0.0,
                                            location = jobDoc.getString("location") ?: "",
                                            eventDate = jobDoc.getDate("eventDate") ?: Date(),
                                            eventType = EventType.valueOf(jobDoc.getString("eventType") ?: "OTHER"),
                                            requirements = jobDoc.get("requirements") as? List<String> ?: emptyList(),
                                            clientId = jobDoc.getString("clientId") ?: "",
                                            clientName = jobDoc.getString("clientName") ?: "",
                                            postedDate = jobDoc.getDate("postedDate") ?: Date(),
                                            status = status
                                        )
                                        _proposals.value = _proposals.value + jobProposal
                                    }
                                }
                                .addOnFailureListener {
                                    // Handle error
                                }
                        }
                    }
                    .addOnFailureListener {
                        // Handle error
                    }
            }
        }
    }
}