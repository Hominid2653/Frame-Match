package com.app.fm001.ui.screens.client.dashboard

import androidx.lifecycle.ViewModel
import com.app.fm001.model.JobPost
import com.app.fm001.model.EventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class ClientJobsViewModel : ViewModel() {
    private val _myJobs = MutableStateFlow<List<JobPost>>(getDummyJobs())
    val myJobs = _myJobs.asStateFlow()

    private fun getDummyJobs(): List<JobPost> {
        return listOf(
            JobPost(
                id = "1",
                title = "Wedding Photography",
                description = "Looking for a photographer for my beach wedding",
                budget = 1500.0,
                location = "Mombasa Beach Hotel",
                eventDate = Date(2024, 7, 15),
                eventType = EventType.WEDDING,
                requirements = listOf(
                    "5 years experience",
                    "Own equipment",
                    "Portfolio required"
                ),
                clientId = "current_user_id",
                postedDate = Date(),
                status = JobPost.Status.OPEN
            ),
            JobPost(
                id = "2",
                title = "Corporate Event",
                description = "Annual company meeting photography needed",
                budget = 800.0,
                location = "Nairobi Business Center",
                eventDate = Date(2024, 6, 20),
                eventType = EventType.CORPORATE,
                requirements = listOf(
                    "Professional equipment",
                    "Quick turnaround",
                    "Previous corporate event experience"
                ),
                clientId = "current_user_id",
                postedDate = Date(),
                status = JobPost.Status.IN_PROGRESS
            )
        )
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
        val newJob = JobPost(
            id = generateJobId(),
            title = title,
            description = description,
            budget = budget,
            location = location,
            eventDate = eventDate,
            eventType = eventType,
            requirements = requirements,
            clientId = "current_user_id", // TODO: Replace with actual user ID
            postedDate = Date(),
            status = JobPost.Status.OPEN
        )
        
        _myJobs.value = _myJobs.value + newJob
    }

    private fun generateJobId(): String {
        return "job_${System.currentTimeMillis()}"
    }
} 