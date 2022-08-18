package com.example.gps

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.gps.databinding.ActivityMaps2Binding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMaps2Binding

    private lateinit var savedLocations: MutableList<Location>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     binding = ActivityMaps2Binding.inflate(layoutInflater)
     setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        var myApplication:MyApplication = (applicationContext as MyApplication)
        savedLocations = myApplication.getMyLocations()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        var lastLocationPlaced:LatLng = sydney
        for (location:Location in savedLocations){
            var latlng: LatLng = LatLng(location.latitude, location.longitude)
            var markerOptions = MarkerOptions()
            markerOptions.position(latlng)
            markerOptions.title("Lat:"+location.latitude+" Lon:"+location.longitude)
            mMap.addMarker(markerOptions)
            lastLocationPlaced = latlng
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocationPlaced, 12.0f))
    }
}