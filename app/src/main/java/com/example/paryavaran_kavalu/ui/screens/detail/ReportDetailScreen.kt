package com.example.paryavaran_kavalu.ui.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.paryavaran_kavalu.data.model.Report
import com.example.paryavaran_kavalu.ui.theme.*
import com.example.paryavaran_kavalu.viewmodel.ReportViewModel
import com.example.paryavaran_kavalu.viewmodel.UserRole
import com.example.paryavaran_kavalu.utils.ImageHelper

//import android.content.Intent
//import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    reportId: String,
    viewModel: ReportViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val reports by viewModel.allReports.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val report = reports.find { it.id == reportId }

    var cleanedImageUriString by remember { mutableStateOf<String?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<String?>(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = ImageHelper.saveImageToInternalStorage(context, it)
            cleanedImageUriString = savedPath
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            val savedPath = ImageHelper.saveImageToInternalStorage(context, Uri.parse(tempCameraUri!!))
            cleanedImageUriString = savedPath
        }
    }

    if (report == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Report not found", color = MaterialTheme.colorScheme.onBackground)
        }
        return
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Select Image Source") },
            text = { Text("Take a new photo or choose from your gallery.") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    photoPickerLauncher.launch("image/*")
                }) { Text("Gallery") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    val uri = ImageHelper.getTempImageUri(context)
                    tempCameraUri = uri.toString()
                    cameraLauncher.launch(uri)
                }) { Text("Camera") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                if (report.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = report.imageUrl,
                        contentDescription = "Waste Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    ReportImagePlaceholder()
                }

                if (report.status == "Cleaned" && report.cleanedImageUrl.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "Cleaned View Below",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = report.wasteType,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    StatusBadge(status = report.status)
                }

                if (report.status == "Cleaned" && report.cleanedImageUrl.isNotEmpty()) {
                    DetailCard(icon = Icons.Filled.CheckCircle, title = "Cleaned Evidence") {
                        AsyncImage(
                            model = report.cleanedImageUrl,
                            contentDescription = "Cleaned Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                DetailCard(icon = Icons.Filled.Description, title = "Description") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = report.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reported by ${report.reporterName.ifBlank { "Unknown User" }}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                DetailCard(icon = Icons.Filled.LocationOn, title = "Location") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = userRole == UserRole.VOLUNTEER) {
                                val gmmIntentUri = Uri.parse("google.navigation:q=${report.latitude},${report.longitude}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            },
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CoordinateRow(label = "Latitude", value = "%.6f".format(report.latitude))
                        CoordinateRow(label = "Longitude", value = "%.6f".format(report.longitude))
                        
                        if (userRole == UserRole.VOLUNTEER) {
                            Text(
                                text = "Tap to navigate in Google Maps",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                if (userRole == UserRole.VOLUNTEER) {
                    if (report.status == "Pending") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Image Picker for Volunteers
                            if (cleanedImageUriString == null) {
                                OutlinedButton(
                                    onClick = { showImageSourceDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(Icons.Filled.AddAPhoto, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add Cleaning Photo (Optional)")
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                                    AsyncImage(
                                        model = cleanedImageUriString,
                                        contentDescription = "New Cleaned Image",
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { cleanedImageUriString = null },
                                        modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(0.4f), CircleShape)
                                    ) {
                                        Icon(Icons.Filled.Close, null, tint = Color.White)
                                    }
                                }
                            }

                            if (uiState is com.example.paryavaran_kavalu.viewmodel.ReportUiState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else {
                                Button(
                                    onClick = { 
                                        viewModel.markAsCleaned(
                                            report.id, 
                                            cleanedImageUriString?.let { Uri.parse(it) }
                                        ) 
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Mark as Cleaned",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            if (uiState is com.example.paryavaran_kavalu.viewmodel.ReportUiState.Error) {
                                Text(
                                    text = (uiState as com.example.paryavaran_kavalu.viewmodel.ReportUiState.Error).message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    } else {
                        // Already cleaned state
                        OutlinedButton(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                disabledContentColor = CleanedGreen
                            )
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = CleanedGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Already Cleaned ✓",
                                color = CleanedGreen,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Text(
                                text = if (report.status == "Pending") 
                                    "Volunteers have been notified to clean this spot." 
                                else "Thank you for reporting! This spot has been cleaned.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Image,
                contentDescription = "Image",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Report Image",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (label, bgColor, textColor) = when (status) {
        "Pending" -> Triple("● Pending", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
        "Cleaned" -> Triple("✓ Cleaned", CleanedGreen.copy(alpha = 0.15f), CleanedGreen)
        else -> Triple("Unknown", Color.Gray.copy(alpha = 0.15f), Color.Gray)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DetailCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            content()
        }
    }
}

@Composable
private fun CoordinateRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
    }
}
