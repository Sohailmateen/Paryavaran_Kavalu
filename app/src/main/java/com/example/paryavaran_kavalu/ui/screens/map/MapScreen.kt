package com.example.paryavaran_kavalu.ui.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.paryavaran_kavalu.utils.LocationHelper
import com.example.paryavaran_kavalu.viewmodel.ReportViewModel
import com.example.paryavaran_kavalu.viewmodel.UserRole
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MapScreen(
//    viewModel: ReportViewModel,
//    onNavigateToReport: (Double?, Double?) -> Unit = { _, _ -> },
//    onBack: () -> Unit = {},
//    onMarkerClick: (String) -> Unit = {}
//) {
//    val context = LocalContext.current
//    val reports by viewModel.allReports.collectAsState()
//    val userRole by viewModel.userRole.collectAsState()
//    val locationHelper = remember { LocationHelper(context) }
//
//    val bangalore = LatLng(12.9716, 77.5946)
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(bangalore, 11f)
//    }
//
//    var hasLocationPermission by remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//    }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        hasLocationPermission = isGranted
//    }
//
//    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
//
//    LaunchedEffect(hasLocationPermission) {
//        if (hasLocationPermission) {
//            locationHelper.getCurrentLocation { lat, lng ->
//                // Initial camera positioning
//                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 15f)
//            }
//        } else {
//            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }
//
//    val uiSettings = remember { MapUiSettings(zoomControlsEnabled = false) }
//    val properties = remember(hasLocationPermission) {
//        MapProperties(isMyLocationEnabled = hasLocationPermission)
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Waste Map", fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back",
//                            tint = MaterialTheme.colorScheme.onPrimary
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
//                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
//                )
//            )
//        },
//        containerColor = MaterialTheme.colorScheme.background,
//        floatingActionButton = {
//            if (userRole == UserRole.CITIZEN && selectedLocation != null) {
//                ExtendedFloatingActionButton(
//                    onClick = {
//                        onNavigateToReport(selectedLocation?.latitude, selectedLocation?.longitude)
//                    },
//                    icon = { Icon(Icons.Default.Add, null) },
//                    text = { Text("Report Here") },
//                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
//                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
//                )
//            }
//        }
//    ) { innerPadding ->
//        Box(modifier = Modifier.fillMaxSize()) {
//            GoogleMap(
//                modifier = Modifier.fillMaxSize(),
//                cameraPositionState = cameraPositionState,
//                uiSettings = uiSettings,
//                properties = properties,
//                contentPadding = innerPadding,
//                onMapClick = { latLng ->
//                    if (userRole == UserRole.CITIZEN) {
//                        selectedLocation = latLng
//                    }
//                }
//            ) {
//                reports.forEach { report ->
//                    Marker(
//                        state = rememberMarkerState(key = report.id, position = LatLng(report.latitude, report.longitude)),
//                        title = report.wasteType,
//                        snippet = report.status,
//                        icon = BitmapDescriptorFactory.defaultMarker(
//                            if (report.status == "Pending") BitmapDescriptorFactory.HUE_RED
//                            else BitmapDescriptorFactory.HUE_GREEN
//                        ),
//                        onClick = {
//                            onMarkerClick(report.id)
//                            false
//                        }
//                    )
//                }
//
//                selectedLocation?.let {
//                    Marker(
//                        state = rememberMarkerState(key = "selected", position = it),
//                        title = "New Report Spot",
//                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
//                        alpha = 0.8f
//                    )
//                }
//            }
//
//            if (userRole == UserRole.CITIZEN && selectedLocation == null) {
//                Surface(
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                        .padding(innerPadding)
//                        .padding(16.dp),
//                    shape = RoundedCornerShape(8.dp),
//                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
//                ) {
//                    Text(
//                        text = "Tap on map to mark a spot",
//                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
//        }
//    }
//}

// updated gemini code

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: ReportViewModel,
    onNavigateToReport: (Double?, Double?) -> Unit = { _, _ -> },
    onBack: () -> Unit = {},
    onMarkerClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val reports by viewModel.allReports.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val locationHelper = remember { LocationHelper(context) }

    val bangalore = LatLng(12.9716, 77.5946)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bangalore, 11f)
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            locationHelper.getCurrentLocation { lat, lng ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 15f)
            }
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val uiSettings = remember { MapUiSettings(zoomControlsEnabled = false) }
    val properties = remember(hasLocationPermission) {
        MapProperties(isMyLocationEnabled = hasLocationPermission)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Waste Map", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (userRole == UserRole.CITIZEN && selectedLocation != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        onNavigateToReport(selectedLocation?.latitude, selectedLocation?.longitude)
                    },
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Report Here") }
                )
            }
        }
    ) { innerPadding ->
        // Using the OLD layout structure (padding on the Box) to fix the white screen
        Box(modifier = Modifier.padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                properties = properties,
                onMapClick = { latLng ->
                    if (userRole == UserRole.CITIZEN) {
                        selectedLocation = latLng
                    }
                }
            ) {
                reports.forEach { report ->
                    Marker(
                        // Keep the NEW key-based state for better performance
                        state = rememberMarkerState(key = report.id.toString(), position = LatLng(report.latitude, report.longitude)),
                        title = report.wasteType,
                        snippet = report.status,
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (report.status == "Pending") BitmapDescriptorFactory.HUE_RED
                            else BitmapDescriptorFactory.HUE_GREEN
                        ),
                        onClick = {
                            onMarkerClick(report.id.toString())
                            false
                        }
                    )
                }

                selectedLocation?.let {
                    Marker(
                        state = rememberMarkerState(key = "selected", position = it),
                        title = "New Report Spot",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        alpha = 0.8f
                    )
                }
            }

            if (userRole == UserRole.CITIZEN && selectedLocation == null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = "Tap on map to mark a spot",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}