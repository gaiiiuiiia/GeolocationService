package com.example.geolocationservice.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import com.example.geolocationservice.Constants
import com.example.geolocationservice.R
import com.example.geolocationservice.database.DBHelper
import com.example.geolocationservice.services.LocationData
import com.example.geolocationservice.services.LocationHelper
import com.example.geolocationservice.services.LocationService
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainLocation(
    private val act: Activity
): Fragment()
{
    private lateinit var myView: View
    private lateinit var btnLocating: FloatingActionButton
    private lateinit var tvLocatingLabel: TextView
    private lateinit var dbHelper: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        myView = inflater.inflate(R.layout.main_fragment, container, false)

        btnLocating = myView.findViewById(R.id.btn_start_locating)
        tvLocatingLabel = myView.findViewById(R.id.tv_main_locating_label)

        renderViewAccordingToServiceRunning()

        dbHelper = DBHelper(act.applicationContext)

        btnLocating.setOnClickListener {
            askPermissions() && changeServiceState()
        }

        return myView
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0
            && permissions.size > 1
            && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION
            && permissions[1] == Manifest.permission.ACCESS_COARSE_LOCATION
            && (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

            changeServiceState(true)
        } else {
            Toast.makeText(act.applicationContext, R.string.geolocation_not_allowed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun askPermissions(): Boolean
    {
        if (act.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && act.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
            return false
        }
        return true
    }

    private fun changeServiceState(forceStart: Boolean = false): Boolean
    {
        if (!LocationService.running || forceStart){
            return if ( checkGpsStatus() ) {
                sendCommand(Constants.START_LOCATION_SERVICE)
                LocationData.location.observe(this){
                    onLocationUpdate(it)
                }
                btnLocating.setImageResource(R.drawable.ic_start_location)
                tvLocatingLabel.text = getString(R.string.location_enable)
                true
            } else {
                Toast.makeText(act.applicationContext, R.string.gps_is_enabled, Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }, 2000)
                false
            }
        } else {
            sendCommand(Constants.STOP_LOCATING_SERVICE)
            LocationData.location.removeObservers(this)
            btnLocating.setImageResource(R.drawable.ic_location_disable)
            tvLocatingLabel.text = getString(R.string.location_disable)
        }
        return true
    }

    private fun sendCommand(command: String)
    {
        Intent(act, LocationService::class.java)
            .apply{
                action = command
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    startForegroundService(act.applicationContext, this)
                } else {
                    act.startService(this)
                }
            }
    }

    private fun onLocationUpdate(l: Location)
    {
        dbHelper.saveLocation(l)
    }

    /**
     * настраивает элементы фрагмента в соответствии с запуском службы
     *
     * кнопка будет не активна или наоборот активна, также и текст
     */
    private fun renderViewAccordingToServiceRunning()
    {
        if ( isServiceRunning() ) {
            btnLocating.setImageResource(R.drawable.ic_start_location)
            tvLocatingLabel.text = getString(R.string.location_enable)
        } else {
            btnLocating.setImageResource(R.drawable.ic_location_disable)
            tvLocatingLabel.text = getString(R.string.location_disable)
        }
    }

    private fun checkGpsStatus(): Boolean
    {
        val locationManager = (act.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun isServiceRunning(): Boolean
    {
        return LocationService.running
    }
}