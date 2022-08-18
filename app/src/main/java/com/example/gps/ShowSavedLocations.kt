package com.example.gps

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class ShowSavedLocations : AppCompatActivity() {
    private lateinit var lv_savedLocations: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_saved_locations)

        lv_savedLocations=findViewById(R.id.lv_savedLocations)

        var myApplication: MyApplication = (applicationContext as MyApplication)
        var savedLocations: MutableList<Location> = myApplication.getMyLocations()

        lv_savedLocations.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, savedLocations)
    }
}