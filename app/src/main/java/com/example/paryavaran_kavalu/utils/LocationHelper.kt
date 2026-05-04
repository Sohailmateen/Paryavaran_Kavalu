package com.example.paryavaran_kavalu.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHelper(context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /** 
     * Get current GPS coordinates. 
     * Requires Manifest.permission.ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationResult: (latitude: Double, longitude: Double) -> Unit) {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationResult(it.latitude, it.longitude)
            }
        }
    }
}
