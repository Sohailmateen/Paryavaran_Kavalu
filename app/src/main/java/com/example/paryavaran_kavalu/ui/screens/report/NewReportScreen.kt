package com.example.paryavaran_kavalu.ui.screens.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paryavaran_kavalu.ui.theme.*
import com.example.paryavaran_kavalu.viewmodel.ReportViewModel
import com.example.paryavaran_kavalu.viewmodel.WasteReport
import com.example.paryavaran_kavalu.viewmodel.WasteType
import java.util.UUID

@Composable
fun NewReportScreen(
    viewModel: ReportViewModel,
    onNavigateBack: () -> Unit = {}
) {
    var selectedWasteType by remember { mutableStateOf<WasteType?>(null) }
    var description by remember { mutableStateOf("") }
    var imageAttached by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    if (submitted) {
        ReportSuccessDialog(onDismiss = onNavigateBack)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OffWhite
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            ReportTopBar(onNavigateBack = onNavigateBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Step Indicator (from new code)
                StepIndicator(currentStep = if (selectedWasteType == null) 1 else if (description.isEmpty()) 2 else 3)

                // Info Banner
                InfoBanner()

                // Section: Waste Type
                SectionLabel(text = "Waste Type", required = true)
                WasteTypeDropdown(
                    selectedWasteType = selectedWasteType,
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it },
                    onWasteTypeSelected = {
                        selectedWasteType = it
                        dropdownExpanded = false
                    }
                )

                // Section: Description
                SectionLabel(text = "Description", required = false)
                DescriptionField(
                    value = description,
                    onValueChange = { description = it }
                )

                // Section: Photo Evidence
                SectionLabel(text = "Photo Evidence", required = false)
                ImageUploadSection(
                    imageAttached = imageAttached,
                    onUploadClick = { imageAttached = !imageAttached }
                )

                // Section: Location
                SectionLabel(text = "Location", required = true)
                LocationStatusCard()

                Spacer(modifier = Modifier.height(8.dp))

                // Submit Button
                SubmitButton(
                    enabled = selectedWasteType != null,
                    onClick = {
                        selectedWasteType?.let { type ->
                            viewModel.addReport(
                                WasteReport(
                                    id = UUID.randomUUID().toString(),
                                    wasteType = type,
                                    description = description.ifBlank { "No description provided." },
                                    latitude = 12.9716, // Placeholder
                                    longitude = 77.5946 // Placeholder
                                )
                            )
                            submitted = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "New Report",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Help keep your community clean",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LightGreen
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ForestGreen
        )
    )
}

@Composable
private fun InfoBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE8F5E9)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reports are reviewed and dispatched to volunteer cleanup teams.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DarkGreen
                )
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String, required: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        )
        if (required) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "*",
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WasteTypeDropdown(
    selectedWasteType: WasteType?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onWasteTypeSelected: (WasteType) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = selectedWasteType?.label ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    text = "Select waste category",
                    color = TextSecondary
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = BorderGray,
                focusedLabelColor = ForestGreen,
                unfocusedContainerColor = CardWhite,
                focusedContainerColor = CardWhite
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            WasteType.entries.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = type.label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (type == selectedWasteType) ForestGreen else TextPrimary,
                                fontWeight = if (type == selectedWasteType)
                                    FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    },
                    onClick = { onWasteTypeSelected(type) },
                    trailingIcon = {
                        if (type == selectedWasteType) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DescriptionField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Describe the waste situation — amount, severity, any hazards...",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = BorderGray,
            unfocusedContainerColor = CardWhite,
            focusedContainerColor = CardWhite
        ),
        maxLines = 5
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "${value.length} / 300",
            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
        )
    }
}

@Composable
private fun ImageUploadSection(
    imageAttached: Boolean,
    onUploadClick: () -> Unit
) {
    if (!imageAttached) {
        // Upload Prompt
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 2.dp,
                    brush = SolidColor(BorderGray),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(CardWhite)
                .clickable(onClick = onUploadClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Upload Image",
                        tint = ForestGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Tap to upload photo",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = ForestGreen
                    )
                )
                Text(
                    text = "JPG, PNG up to 10MB",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }
        }
    } else {
        // Image "Attached" Placeholder State
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEEEEEE)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Preview",
                    tint = TextSecondary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "waste_photo_001.jpg",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
            // Remove button top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xCC000000))
                    .clickable(onClick = onUploadClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Success badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Photo attached",
                style = MaterialTheme.typography.labelSmall.copy(color = ForestGreen)
            )
        }
    }
}

@Composable
private fun LocationStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = AccentAmber,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Fetching location...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "GPS coordinates will be auto-tagged",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary
                    )
                )
            }
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = AccentAmber
            )
        }
    }
}

@Composable
private fun SubmitButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ForestGreen,
            contentColor = Color.White,
            disabledContainerColor = BorderGray,
            disabledContentColor = TextSecondary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (enabled) 4.dp else 0.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Send,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Submit Report",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
    }

    if (!enabled) {
        Text(
            text = "Please select a waste type to submit",
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Type", "Describe", "Submit").forEachIndexed { index, label ->
            val step = index + 1
            val isActive = step <= currentStep
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = CircleShape,
                    color = if (isActive) ForestGreen else BorderGray,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (step < currentStep) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Text("$step", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                Text(label, style = MaterialTheme.typography.labelSmall, color = if (isActive) TextPrimary else TextSecondary)
            }
            if (index < 2) {
                HorizontalDivider(modifier = Modifier.width(40.dp), color = if (step < currentStep) ForestGreen else BorderGray)
            }
        }
    }
}

@Composable
private fun ReportSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(48.dp)) },
        title = { Text("Report Submitted!", fontWeight = FontWeight.Bold) },
        text = { Text("Your waste report has been submitted successfully. Thank you for keeping your community clean! 🌿") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = ForestGreen, fontWeight = FontWeight.Bold)
            }
        }
    )
}
