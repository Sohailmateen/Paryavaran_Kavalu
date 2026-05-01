package com.example.paryavaran_kavalu.ui.screens.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

// ─────────────────────────────────────────────
// Design Tokens (shared with HomeScreen)
// ─────────────────────────────────────────────
private val ForestGreen   = Color(0xFF2E7D32)
private val LightGreen    = Color(0xFFA5D6A7)
private val DarkGreen     = Color(0xFF1B5E20)
private val OffWhite      = Color(0xFFF5F5F5)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextPrimary   = Color(0xFF1C1B1F)
private val TextSecondary = Color(0xFF6B7280)
private val BorderGray    = Color(0xFFE0E0E0)
private val AccentAmber   = Color(0xFFF59E0B)

// ─────────────────────────────────────────────
// Data
// ─────────────────────────────────────────────
private val wasteTypes = listOf(
    "🗑️  General Waste",
    "♻️  Plastic / Recyclable",
    "🌿  Bio-degradable",
    "⚡  E-Waste",
    "☣️  Hazardous / Chemical",
    "🏗️  Construction Debris",
    "🚽  Sewage / Drain Blockage"
)

// ─────────────────────────────────────────────
// Root Screen
// ─────────────────────────────────────────────
@Composable
fun NewReportScreen(
    onNavigateBack: () -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    var selectedWasteType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageAttached by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

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
                    enabled = selectedWasteType.isNotEmpty(),
                    onClick = onSubmit
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────
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
                        color = CardWhite
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
                    tint = CardWhite
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ForestGreen
        )
    )
}

// ─────────────────────────────────────────────
// Info Banner
// ─────────────────────────────────────────────
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

// ─────────────────────────────────────────────
// Section Label
// ─────────────────────────────────────────────
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

// ─────────────────────────────────────────────
// Waste Type Dropdown
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WasteTypeDropdown(
    selectedWasteType: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onWasteTypeSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = selectedWasteType,
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
            wasteTypes.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = type,
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

// ─────────────────────────────────────────────
// Description Field
// ─────────────────────────────────────────────
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

// ─────────────────────────────────────────────
// Image Upload Section
// ─────────────────────────────────────────────
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
                    tint = CardWhite,
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

// ─────────────────────────────────────────────
// Location Status Card
// ─────────────────────────────────────────────
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

// ─────────────────────────────────────────────
// Submit Button
// ─────────────────────────────────────────────
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
            contentColor = CardWhite,
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

// ─────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, name = "New Report Screen")
@Composable
fun NewReportScreenPreview() {
    MaterialTheme {
        NewReportScreen()
    }
}

//@Preview(showBackground = true, name = "Location Status Card")
//@Composable
//fun LocationStatusCardPreview() {
//    MaterialTheme {
//        Column(modifier = Modifier.padding(16.dp)) {
//            LocationStatusCard()
//        }
//    }
//}

//@Preview(showBackground = true, name = "Image Upload — Empty")
//@Composable
//fun ImageUploadEmptyPreview() {
//    MaterialTheme {
//        Column(modifier = Modifier.padding(16.dp)) {
//            ImageUploadSection(imageAttached = false, onUploadClick = {})
//        }
//    }
//}

//@Preview(showBackground = true, name = "Image Upload — Attached")
//@Composable
//fun ImageUploadAttachedPreview() {
//    MaterialTheme {
//        Column(modifier = Modifier.padding(16.dp)) {
//            ImageUploadSection(imageAttached = true, onUploadClick = {})
//        }
//    }
//}
