package com.example.gps

import android.app.Application
import android.content.Context
import android.location.Location


class MyApplication : Application(){

    private lateinit var singleton: MyApplication

    private lateinit var MyLocations: MutableList<Location>

    fun getMyLocations():MutableList<Location>{
        return this.MyLocations
    }

    fun setMyLocations(myLocations: MutableList<Location>){
        this.MyLocations = MyLocations
    }

    fun getInstance(): MyApplication{
        return singleton
    }

    override fun onCreate() {
        super.onCreate()
        singleton = this
        MyLocations = ArrayList()
    }
}