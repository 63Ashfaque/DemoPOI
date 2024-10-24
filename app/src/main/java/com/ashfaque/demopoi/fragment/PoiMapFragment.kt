package com.ashfaque.demopoi.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ashfaque.demopoi.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.ashfaque.demopoi.R
import com.ashfaque.demopoi.Utils
import com.ashfaque.demopoi.databinding.FragmentPoiMapBinding
import com.ashfaque.demopoi.shared_preference.SharedPreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class PoiMapFragment : Fragment(), OnMapReadyCallback {

    private var mBinding: FragmentPoiMapBinding? = null

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val radiusInMeters = 10.0f // 1f = 1 meter

    val poiMarkers = mutableListOf<Marker>()
    private var isFirst: Boolean = true
    private var currentMarker: Marker? = null


    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onResume() {
        super.onResume()

        if (!Utils.isLocationEnabled(requireContext())) {
            Utils.showToast(requireContext(), "Location is disabled, redirecting to settings...")
            openLocationSettings()
        }
//        else
//        {
//            val fragmentManager = requireActivity().supportFragmentManager
//            val newFragment = PoiMapFragment()
//            fragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, newFragment)
//                .addToBackStack(null)
//                .commit()
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentPoiMapBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sharedPreferenceManager = SharedPreferenceManager.getInstance(requireActivity())


//        sharedPreferenceManager.saveString("username", "JohnDoe")
//        val username = sharedPreferenceManager.getString("username")
//        Utils.showToast(requireContext(),"Username: $username")
//        sharedPreferenceManager.saveInt("userAge", 25)
//        val userAge = sharedPreferenceManager.getInt("userAge")
//        sharedPreferenceManager.saveBoolean("isLoggedIn", true)
//        val isLoggedIn = sharedPreferenceManager.getBoolean("isLoggedIn")
//        sharedPreferenceManager.clearAll()



        return mBinding!!.root
    }


    override fun onMapReady(googleMap: GoogleMap) {
        Utils.logDebug("Map is ready")
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
            moveToCurrentLocation()
        } else {
            checkLocationPermission()
        }
    }

    private fun moveToCurrentLocation() {
        Utils.logDebug("moveToCurrentLocation")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
                        mMap.addMarker(
                            MarkerOptions().position(currentLatLng).title("You are here")
                        )

                        mMap.addCircle(
                            CircleOptions()
                                .center(currentLatLng)
                                .radius(radiusInMeters.toDouble())
                                .strokeColor(0xFFffff00.toInt())
                                .fillColor(0x22ffff00)
                                .strokeWidth(2f)
                        )

                        loadPOIData(location.latitude, location.longitude, radiusInMeters)
                        setupMapClickListener(location.latitude, location.longitude, radiusInMeters)
                    }
                }
        }
    }

    private fun loadPOIData(latitude: Double, longitude: Double, radiusInMeters: Float) {
        val pois = listOf(
            LatLng(21.0888384, 79.0812956),  // Example POI locations
            LatLng(21.0888884, 79.0813898),
            LatLng(21.0888984, 79.0813486)
        )

        pois.forEach { poi ->
            markerOption(poi, BitmapDescriptorFactory.HUE_ORANGE)
        }
    }

    private fun markerOption(poi: LatLng, pinColor: Float) {
        if (pinColor == BitmapDescriptorFactory.HUE_GREEN) {
            if (!isFirst) {
                currentMarker?.remove()
            }
            isFirst = false
        }

        currentMarker = mMap.addMarker(
            MarkerOptions().position(poi)
                .title("POI ${poi.latitude}, ${poi.longitude}")
                .snippet("My Snippet\n1st Line Text\n2nd Line Text\n3rd Line Text")
                .icon(BitmapDescriptorFactory.defaultMarker(pinColor))
        )

        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val info = LinearLayout(requireContext())
                info.orientation = LinearLayout.VERTICAL

                val title = TextView(requireContext())
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title

                val snippet = TextView(requireContext())
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet

                val editButton = ImageView(requireContext())
                editButton.setImageResource(R.drawable.icon_edit)  // Make sure you have this drawable

                info.addView(title)
                info.addView(snippet)
                info.addView(editButton)

                return info
            }
        })

        currentMarker?.let { poiMarkers.add(it) }
    }

    private fun setupMapClickListener(latitude: Double, longitude: Double, radiusInMeters: Float) {
        mMap.setOnMapClickListener { latLng ->
            val markerTitle = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"

            val isWithinRadius = Utils.arePointsWithinRadius(latitude, longitude,
                latLng.latitude, latLng.longitude, radiusInMeters)

            if (isWithinRadius) {
                markerOption(latLng, BitmapDescriptorFactory.HUE_GREEN)

                val currentZoomLevel = mMap.cameraPosition.zoom
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, currentZoomLevel)
                mMap.animateCamera(cameraUpdate)

                Toast.makeText(
                    requireContext(),
                    "Clicked location: $markerTitle",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "You are outside the $radiusInMeters meters radius.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun enableMyLocation() {
        Utils.logDebug("enableMyLocation")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        }
    }

    private fun checkLocationPermission() {

        Utils.logDebug("checkLocationPermission")
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}