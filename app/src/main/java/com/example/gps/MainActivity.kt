package com.example.gps

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.location.*
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {
    //TextView widgets
    private lateinit var tv_lat:TextView
    private lateinit var tv_lon:TextView
    private lateinit var tv_altitude:TextView
    private lateinit var tv_accuracy:TextView
    private lateinit var tv_speed:TextView
    private lateinit var tv_sensor:TextView
    private lateinit var tv_updates:TextView
    private lateinit var tv_address:TextView
    private lateinit var tv_waypointcounts:TextView
    private lateinit var sw_locationsupdates:Switch
    private lateinit var sw_gps:Switch
    private lateinit var btn_newWayPoint:Button
    private lateinit var btn_showWayPoint:Button
    private lateinit var btn_showMap:Button

    //Location provider client
    private lateinit var locationRequest:LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallBack:LocationCallback

    //Update boolean
    var updateOn:Boolean = false

    //current location
    private lateinit var currentLocation: Location

    //List of saved locations
    private lateinit var savedLocations: MutableList<Location>

    //Constants
    val DEFAULT_UPDATE_INTERVAL = 30
    val FASTEST_UPDATE_INTERVAL = 5
    val PERMISSIONS_FINE_LOCATION = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialization
        tv_lat=findViewById(R.id.tv_lat)
        tv_lon=findViewById(R.id.tv_lon)
        tv_altitude=findViewById(R.id.tv_altitude)
        tv_accuracy=findViewById(R.id.tv_accuracy)
        tv_speed=findViewById(R.id.tv_speed)
        tv_sensor=findViewById(R.id.tv_sensor)
        tv_updates=findViewById(R.id.tv_updates)
        tv_address=findViewById(R.id.tv_address)
        tv_waypointcounts=findViewById(R.id.tv_waypointcounts)
        sw_locationsupdates=findViewById(R.id.sw_locationsupdates)
        sw_gps=findViewById(R.id.sw_gps)
        btn_newWayPoint=findViewById(R.id.btn_newWayPoint)
        btn_showWayPoint=findViewById(R.id.btn_showWayPointList)
        btn_showMap=findViewById(R.id.btn_showMap)

        locationRequest = LocationRequest()

        locationRequest.interval = (1000 * DEFAULT_UPDATE_INTERVAL).toLong()

        locationRequest.fastestInterval = (5000 * FASTEST_UPDATE_INTERVAL).toLong()

        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        locationCallBack = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                var location = p0!!.lastLocation
                updateUIValues(location)
            }
        }

        //Save current location
        btn_newWayPoint.setOnClickListener {
            var myApplication:MyApplication = (applicationContext as MyApplication)
            savedLocations = myApplication.getMyLocations()
            savedLocations.add(currentLocation)
            tv_waypointcounts.text = savedLocations.size.toString()
        }

        //Show saved locations
        btn_showWayPoint.setOnClickListener {
            var intent = Intent(this,ShowSavedLocations::class.java)
            startActivity(intent)
        }

        //Shows Google maps
        btn_showMap.setOnClickListener {
            var intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
        }

        //Enable/Disable GPS
        sw_gps.setOnClickListener {
            if (sw_gps.isChecked){
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                tv_sensor.text = "Using GPS sensors"
            }else{
                locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                tv_sensor.text = "Using Tower + Wifi"
            }
        }
        sw_locationsupdates.setOnClickListener {
            if (sw_locationsupdates.isChecked){
                startLocationUpdates()
            }else{
                stopLocationUpdates()
            }
        }
        updateGPS()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            //Requests fine location permissions
            when (requestCode){
                PERMISSIONS_FINE_LOCATION->{
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        updateGPS()
                    }else{
                        Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
        }

    }

    private fun updateGPS(){
        //Location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.lastLocation.addOnSuccessListener{ location ->
                updateUIValues(location)
                currentLocation = location
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                val perms = listOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                requestPermissions(perms.toTypedArray(), PERMISSIONS_FINE_LOCATION)
            }
        }
    }

    private fun updateUIValues(location:Location) {
        tv_lat.text = location.latitude.toString()
        tv_lon.text = location.longitude.toString()
        tv_accuracy.text = location.accuracy.toString()


        if (location.hasAltitude()){
            tv_altitude.text = location.altitude.toString()
        }else{
            tv_altitude.text = "Not available"
        }

        if (location.hasSpeed()){
            tv_speed.text = location.speed.toString()
        }else{
            tv_speed.text = "Not available"
        }

        var geocoder = Geocoder(this)

        try{
            var addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            tv_address.text = addresses[0].getAddressLine(0)
        }catch (e:Exception){
            tv_address.text = "Unable to get street address"
        }
        var myApplication:MyApplication = (applicationContext as MyApplication)
        savedLocations = myApplication.getMyLocations()
        tv_waypointcounts.text = savedLocations.size.toString()
    }

    private fun startLocationUpdates(){
        tv_updates.text = "Location is being tracked"
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                val perms = listOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                requestPermissions(perms.toTypedArray(), PERMISSIONS_FINE_LOCATION)
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null)
        updateGPS()
    }

    private fun stopLocationUpdates(){
        tv_updates.text = "Location is NOT being tracked"
        tv_lat.text = "Not tracking location"
        tv_lon.text = "Not tracking location"
        tv_speed.text = "Not tracking location"
        tv_address.text = "Not tracking location"
        tv_accuracy.text = "Not tracking location"
        tv_altitude.text = "Not tracking location"
        tv_sensor.text = "Not tracking location"

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
    }
}