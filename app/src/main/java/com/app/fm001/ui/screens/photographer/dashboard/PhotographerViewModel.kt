package com.app.fm001.ui.screens.photographer.dashboard

import androidx.lifecycle.ViewModel
import com.app.fm001.model.JobProposal
import com.app.fm001.model.ProposalStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PhotographerViewModel : ViewModel() {
    private val _myBids = MutableStateFlow<List<JobProposal>>(emptyList())
    val myBids = _myBids.asStateFlow()

    fun applyForJob(proposal: JobProposal) {
        val updatedProposal = proposal.copy(status = ProposalStatus.IN_PROGRESS)
        val currentBids = _myBids.value.toMutableList()
        currentBids.add(updatedProposal)
        _myBids.value = currentBids
    }

    fun updateBidStatus(proposalId: String, newStatus: ProposalStatus) {
        val currentBids = _myBids.value.toMutableList()
        val index = currentBids.indexOfFirst { it.id == proposalId }
        if (index != -1) {
            currentBids[index] = currentBids[index].copy(status = newStatus)
            _myBids.value = currentBids
        }
    }
} 