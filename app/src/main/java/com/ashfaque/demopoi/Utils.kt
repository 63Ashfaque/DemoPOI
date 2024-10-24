package com.ashfaque.demopoi

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {

    private const val TAG = "Ashu"

    fun logDebug(message: String) {
        Log.d(TAG, message)
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getCurrentDate(pattern:String): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

     fun arePointsWithinRadius(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double, radiusInMeters: Float
    ): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, result)
        return result[0] <= radiusInMeters
    }


}
