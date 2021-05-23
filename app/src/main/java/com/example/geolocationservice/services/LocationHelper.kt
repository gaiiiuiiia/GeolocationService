package com.example.geolocationservice.services

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.geolocationservice.R

object LocationHelper: LocationListener
{
    private var locationManager: LocationManager? = null
    private var locationUpdater: ((Location)->Unit)? = null

    override fun onLocationChanged(location: Location)
    {
        locationUpdater?.invoke(location)
    }

    fun startLocating(context: Context, locationUpdateListener: (Location)->Unit): Boolean
    {
        locationUpdater = locationUpdateListener
        locationManager = (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
            .also { locationManager ->
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                    return false
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000L,
                10F,
                this
            )
        }
        return true
    }

    fun stopLocating()
    {
        locationManager?.removeUpdates(this)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }
}