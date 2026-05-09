package com.example.paryavaran_kavalu.ui.screens.report

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.paryavaran_kavalu.utils.LocationHelper
import com.example.paryavaran_kavalu.ui.theme.*
import com.example.paryavaran_kavalu.viewmodel.ReportViewModel
import com.example.paryavaran_kavalu.viewmodel.WasteType
import com.example.paryavaran_kavalu.utils.ImageHelper
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(
    viewModel: ReportViewModel,
    lat: Double? = null,
    lng: Double? = null,
    onNavigateBack: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val userRole by viewModel.userRole.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    // Using rememberSaveable to survive process death/recreation
    var selectedWasteTypeName by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedWasteType = selectedWasteTypeName?.let { name -> WasteType.valueOf(name) }
    
    var description by rememberSaveable { mutableStateOf("") }
    var imageUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val imageUri = imageUriString?.let { Uri.parse(it) }
    
    var dropdownExpanded by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    var currentLat by rememberSaveable { mutableStateOf(lat) }
    var currentLng by rememberSaveable { mutableStateOf(lng) }
    var isFetchingLocation by rememberSaveable { mutableStateOf(lat == null) }
    var locationError by rememberSaveable { mutableStateOf<String?>(null) }

    var showImageSourceDialog by rememberSaveable { mutableStateOf(false) }
    var tempCameraUri by rememberSaveable { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Save to internal storage immediately so it's visible in details later
            val savedPath = ImageHelper.saveImageToInternalStorage(context, it)
            imageUriString = savedPath
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            // Save to internal storage permanently
            val savedPath = ImageHelper.saveImageToInternalStorage(context, Uri.parse(tempCameraUri!!))
            imageUriString = savedPath
        }
    }

    // Fetch location if not provided
    LaunchedEffect(Unit) {
        if (currentLat == null) {
            try {
                locationHelper.getCurrentLocation { l, ln ->
                    currentLat = l
                    currentLng = ln
                    isFetchingLocation = false
                }
                delay(15000)
                if (currentLat == null) {
                    isFetchingLocation = false
                    locationError = "Location timeout. Please try again."
                }
            } catch (e: Exception) {
                isFetchingLocation = false
                locationError = "Failed to access GPS."
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Select Image Source") },
            text = { Text("Choose how you want to upload the photo.") },
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

    if (uiState is com.example.paryavaran_kavalu.viewmodel.ReportUiState.Success) {
        ReportSuccessDialog(onDismiss = {
            viewModel.resetUiState()
            onNavigateBack()
        })
    }

    Scaffold(
        topBar = {
            ReportTopBar(onNavigateBack = onNavigateBack)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            StepIndicator(currentStep = if (selectedWasteType == null) 1 else if (description.isEmpty()) 2 else 3)

            InfoBanner()

            SectionLabel(text = "Waste Type", required = true)
            WasteTypeDropdown(
                selectedWasteType = selectedWasteType,
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it },
                onWasteTypeSelected = {
                    selectedWasteTypeName = it.name
                    dropdownExpanded = false
                }
            )

            SectionLabel(text = "Description", required = false)
            DescriptionField(
                value = description,
                onValueChange = { description = it }
            )

            SectionLabel(text = "Photo Evidence", required = false)
            ImageUploadSection(
                imageUri = imageUri,
                onUploadClick = { 
                    if (imageUri == null) showImageSourceDialog = true
                    else imageUriString = null
                }
            )

            SectionLabel(text = "Location", required = true)
            LocationStatusCard(
                latitude = currentLat,
                longitude = currentLng,
                isFetching = isFetchingLocation,
                error = locationError
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Submit Button
            if (uiState is com.example.paryavaran_kavalu.viewmodel.ReportUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                SubmitButton(
                    enabled = selectedWasteType != null && currentLat != null,
                    onClick = {
                        selectedWasteType?.let { type ->
                            viewModel.addReport(
                                wasteType = type.label,
                                description = description.ifBlank { "No description provided." },
                                imageUri = imageUri,
                                latitude = currentLat ?: 0.0,
                                longitude = currentLng ?: 0.0,
                                currentUserRole = userRole.name
                            )
                        }
                    }
                )
            }

            if (uiState is com.example.paryavaran_kavalu.viewmodel.ReportUiState.Error) {
                Text(
                    text = (uiState as com.example.paryavaran_kavalu.viewmodel.ReportUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
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
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Text(
                    text = "Help keep your community clean",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun InfoBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reports are reviewed and dispatched to volunteer cleanup teams.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer
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
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        if (required) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "*",
                color = MaterialTheme.colorScheme.error,
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
            placeholder = { Text("Select waste category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            WasteType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.label) },
                    onClick = { onWasteTypeSelected(type) }
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
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
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}

@Composable
private fun ImageUploadSection(
    imageUri: Uri?,
    onUploadClick: () -> Unit
) {
    if (imageUri == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(MaterialTheme.colorScheme.surface)
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
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Upload Image",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Tap to upload photo",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "JPG, PNG up to 10MB",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = CleanedGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Photo attached",
                style = MaterialTheme.typography.labelSmall.copy(color = CleanedGreen)
            )
        }
    }
}

@Composable
private fun LocationStatusCard(
    latitude: Double? = null,
    longitude: Double? = null,
    isFetching: Boolean = false,
    error: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (error != null) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.tertiaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (error != null) Icons.Default.Error else Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = if (error != null) MaterialTheme.colorScheme.error 
                          else MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (error != null) "Location Error" 
                           else if (isFetching) "Fetching location..." 
                           else if (latitude != null) "Location Tagged" 
                           else "Location access needed",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = if (error != null) error
                           else if (latitude != null && longitude != null) 
                                "%.4f, %.4f".format(latitude, longitude)
                           else "GPS coordinates will be auto-tagged",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            if (isFetching) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            } else if (latitude != null && error == null) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Ready",
                    tint = CleanedGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
            text = "Please select a waste type and ensure location is tagged.",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (step < currentStep) {
                            Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                        } else {
                            Text(
                                text = "$step", 
                                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, 
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
                Text(
                    text = label, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = if (isActive) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (index < 2) {
                HorizontalDivider(
                    modifier = Modifier.width(40.dp), 
                    color = if (step < currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun ReportSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = CleanedGreen, modifier = Modifier.size(48.dp)) },
        title = { Text("Report Submitted!", fontWeight = FontWeight.Bold) },
        text = { Text("Your waste report has been submitted successfully. Thank you for keeping your community clean! 🌿") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    )
}
