package com.example.geolocationservice.services

import android.location.Location
import androidx.lifecycle.MutableLiveData

object LocationData
{
    val location  = MutableLiveData<Location>()
}