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

object LocatingHelper: LocationListener
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
            .also { locManager ->
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                    return false
            }
            // проверим активна ли GPS на телефоне
            val gpsStatus = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (gpsStatus) {
                // если да, то запускаем менеджер
                locManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000L,
                    10F,
                    this
                )
            } else {
                // если нет, то просим включить службу GPS
                Toast.makeText(context, R.string.gps_is_enabled, Toast.LENGTH_SHORT).show()
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).let{
                    context.startActivity(it)
                }
                return false
            }
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