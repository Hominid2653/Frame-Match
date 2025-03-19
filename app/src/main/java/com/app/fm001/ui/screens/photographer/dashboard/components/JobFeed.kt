import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.fm001.model.JobProposal
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.app.fm001.model.EventType
import com.app.fm001.ui.screens.photographer.dashboard.components.JobProposalCard

@Composable
fun JobFeed(
    proposals: List<JobProposal>,
    onApplyClick: (JobProposal) -> Unit,
    onMessageClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<EventType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search jobs...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            singleLine = true
        )

        // Filter chips
        ScrollableFilterChips(selectedFilter) { selectedFilter = it }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtered job list
        val filteredProposals = proposals.filter {
            (selectedFilter == null || it.eventType == selectedFilter) &&
                    it.title.contains(searchQuery, ignoreCase = true)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(filteredProposals) { proposal ->
                JobProposalCard(
                    proposal = proposal,
                    onApplyClick = onApplyClick,
                    onMessageClick = onMessageClick
                )
            }
        }
    }
}

@Composable
private fun ScrollableFilterChips(selectedFilter: EventType?, onFilterSelected: (EventType?) -> Unit) {
    val filters = EventType.values().toList()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { onFilterSelected(null) },
                label = { Text("All") },
                leadingIcon = if (selectedFilter == null) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null
            )
        }
        items(filters) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.name) },
                leadingIcon = if (selectedFilter == filter) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null
            )
        }
    }
}
