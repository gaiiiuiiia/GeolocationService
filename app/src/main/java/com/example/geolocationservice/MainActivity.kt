package com.example.geolocationservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.geolocationservice.fragments.MainLocation
import com.example.geolocationservice.fragments.ShowActivities
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open_Drawer, R.string.Close_Drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setStartFragment()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, MainLocation(this))
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_watch_activities -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, ShowActivities())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_about -> {
                Toast.makeText(this, getAboutText(), Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setStartFragment(): Boolean
    {
        navigationView.setCheckedItem(R.id.nav_home)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, MainLocation(this))
            .addToBackStack(null)
            .commit()

        return true
    }

    override fun onBackPressed()
    {
        supportFragmentManager.popBackStackImmediate()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun getAboutText(): String
    {
        return "Geolocation Service \n by Maxim Titov 2021"
    }
}
