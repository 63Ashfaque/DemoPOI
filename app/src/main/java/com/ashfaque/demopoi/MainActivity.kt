package com.ashfaque.demopoi

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity() , OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // Initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
            moveToCurrentLocation()
        } else {
            checkLocationPermission()
        }
    }

    private val radiusInMeters = 5.0f //1f=1meter

    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        Log.d("ashu","Location: ${location.latitude}, ${location.longitude}")
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
                        mMap.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))

                        mMap.addCircle(
                            CircleOptions()
                                .center(currentLatLng)    // Set the center of the circle
                                .radius(radiusInMeters.toDouble())   // Set the radius in meters
                                .strokeColor(0xFF0000FF.toInt())  // Stroke color (Blue)
                                .fillColor(0x220000FF)    // Fill color (Transparent Blue)
                                .strokeWidth(2f)          // Stroke width
                        )

                        loadPOIData(location.latitude, location.longitude,radiusInMeters)
                        loadPOIData(location.latitude, location.longitude,radiusInMeters)
                    }
                }


        }
    }


//    private fun loadPOIData(latitude: Double, longitude: Double) {
//        val pois = listOf(
//            LatLng(21.0888184, 79.0812856),  // Example POI locations
//            LatLng(21.0888884, 79.0813886),
//            LatLng(21.0888984, 79.0813886),
//
//        )
//
//        pois.forEach { poi ->
//            mMap.addMarker(MarkerOptions().position(poi).title("POI"))
//
//            addPoiMarkerAndHandleClick(poi)
//            val isWithinRadius = arePointsWithinRadius(latitude, longitude, poi.latitude, poi.longitude, radiusInMeters)
//
//            if (isWithinRadius) {
//                Log.d("ashu", "You are within $radiusInMeters meters of the target location.")
//            } else {
//                Log.d("ashu", "You are outside the $radiusInMeters meters radius.")
//            }
//
//        }
//
//    }

//    private fun addPoiMarkerAndHandleClick(poiLatLng: LatLng) {
//        // Add a marker to the map
//        val poiMarker = mMap.addMarker(MarkerOptions().position(poiLatLng).title("POI"))
//
//        // Set a click listener for the marker
//        mMap.setOnMarkerClickListener { marker ->
//            if (marker == poiMarker) {
//                // Perform action when this specific marker is clicked
//                // Example: Show a toast message or navigate to a new activity
//                Toast.makeText(this, "POI marker clicked!", Toast.LENGTH_SHORT).show()
//
//                // Return true if you want to consume the event here
//                true
//            } else {
//                // Allow other markers to handle their click events
//                false
//            }
//        }
//    }

    private fun loadPOIData(latitude: Double, longitude: Double, radiusInMeters: Float) {
        val pois = listOf(
            LatLng(21.0888384, 79.0812956),  // Example POI locations
            LatLng(21.0888884, 79.0813898),
            LatLng(21.0888984, 79.0813486)
        )

        val poiMarkers = mutableListOf<Marker>()


        // Loop through each POI and add a marker
        pois.forEach { poi ->
            // Add a marker to the map for each POI
          //  val poiMarker = mMap.addMarker(MarkerOptions().position(poi).title("POI ${poi.latitude }, ${poi.longitude}"))
            val poiMarker = mMap.addMarker( MarkerOptions().position(poi).title("POI ${poi.latitude }, ${poi.longitude}")
                .snippet("My Snippet"+"\n"+"1st Line Text"+"\n"+"2nd Line Text"+"\n"+"3rd Line Text")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            mMap.setInfoWindowAdapter(object : InfoWindowAdapter {
                override fun getInfoWindow(arg0: Marker): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker): View? {
                    val info = LinearLayout(applicationContext)
                    info.orientation = LinearLayout.VERTICAL

                    val title = TextView(applicationContext)
                    title.setTextColor(Color.BLACK)
                    title.gravity = Gravity.CENTER
                    title.setTypeface(null, Typeface.BOLD)
                    title.text = marker.title

                    val snippet = TextView(applicationContext)
                    snippet.setTextColor(Color.GRAY)
                    snippet.text = marker.snippet

                    info.addView(title)
                    info.addView(snippet)

                    return info
                }
            })

            poiMarker?.let { poiMarkers.add(it) }

            // Check if the current location is within the given radius of the POI
            val isWithinRadius = arePointsWithinRadius(latitude, longitude, poi.latitude, poi.longitude, radiusInMeters)

            if (isWithinRadius) {
                Log.d("ashu", "You are within $radiusInMeters meters of the target location.")
            } else {
                Log.d("ashu", "You are outside the $radiusInMeters meters radius.")
            }


//            mMap.setOnMarkerClickListener { clickedMarker ->
//                // Find the clicked marker in the poiMarkers array
//                val markerIndex = poiMarkers.indexOf(clickedMarker)
//
//                if (markerIndex != -1) {
//                    // Perform action for the clicked marker
//                    val clickedPoi = pois[markerIndex]  // Get the corresponding POI for this marker
//                    Toast.makeText(this, "POI marker clicked! ${clickedPoi.latitude}, ${clickedPoi.longitude}", Toast.LENGTH_SHORT).show()
//
//                    // Return true to indicate the click event has been handled
//                    true
//                } else {
//                    // If the clicked marker is not in the list, return false
//                    false
//                }
//            }


//            poiMarker?.let {
//                mMap.setOnMarkerClickListener { clickedMarker ->
//                    if (clickedMarker == poiMarker) {
//                        // Handle click on this specific POI marker
//                        Toast.makeText(this, "POI marker clicked! ${poi.latitude }, ${poi.longitude}", Toast.LENGTH_SHORT).show()
//
//                        // Return true to indicate that the event has been handled
//                        true
//                    } else {
//                        false
//                    }
//                }
//            }

        }
    }




    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable location
                enableMyLocation()
            } else {
                // Permission denied
                // Handle the case where the user denies the permission
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun arePointsWithinRadius(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double, radiusInMeters: Float
    ): Boolean {
        val result = FloatArray(1)

        // Calculate the distance between the two points
        Location.distanceBetween(lat1, lon1, lat2, lon2, result)

        // Check if the distance is within the given radius
        return result[0] <= radiusInMeters
    }

}