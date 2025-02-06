package com.app.fm001.ui.screens.client.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.fm001.model.EventType
import java.text.SimpleDateFormat
import java.util.*

data class JobDetails(
    val title: String,
    val description: String,
    val budget: Double,
    val location: String,
    val eventDate: Date,
    val eventType: EventType,
    val requirements: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobDialog(
    onDismiss: () -> Unit,
    onJobCreated: (JobDetails) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf(Date()) }
    var eventType by remember { mutableStateOf(EventType.WEDDING) }
    var currentRequirement by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf(listOf<String>()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showEventTypeMenu by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Job") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Budget (KES)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Event Date
                OutlinedTextField(
                    value = dateFormatter.format(eventDate),
                    onValueChange = { },
                    label = { Text("Event Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, "Select date")
                        }
                    }
                )

                // Event Type
                ExposedDropdownMenuBox(
                    expanded = showEventTypeMenu,
                    onExpandedChange = { showEventTypeMenu = it }
                ) {
                    OutlinedTextField(
                        value = eventType.name,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Event Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showEventTypeMenu) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showEventTypeMenu,
                        onDismissRequest = { showEventTypeMenu = false }
                    ) {
                        EventType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    eventType = type
                                    showEventTypeMenu = false
                                }
                            )
                        }
                    }
                }

                // Requirements
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = currentRequirement,
                            onValueChange = { currentRequirement = it },
                            label = { Text("Add Requirement") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (currentRequirement.isNotBlank()) {
                                    requirements = requirements + currentRequirement
                                    currentRequirement = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, "Add requirement")
                        }
                    }
                    requirements.forEachIndexed { index, requirement ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• $requirement")
                            IconButton(
                                onClick = {
                                    requirements = requirements.filterIndexed { i, _ -> i != index }
                                }
                            ) {
                                Icon(Icons.Default.Clear, "Remove requirement")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && budget.isNotBlank()) {
                        onJobCreated(
                            JobDetails(
                                title = title,
                                description = description,
                                budget = budget.toDoubleOrNull() ?: 0.0,
                                location = location,
                                eventDate = eventDate,
                                eventType = eventType,
                                requirements = requirements
                            )
                        )
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { 
                eventDate = it
                showDatePicker = false
            },
            initialDate = eventDate
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Date) -> Unit,
    initialDate: Date
) {
    val calendar = Calendar.getInstance()
    calendar.time = initialDate
    
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(calendar.time)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = rememberDatePickerState(
                initialSelectedDateMillis = initialDate.time
            )
        )
    }
} 