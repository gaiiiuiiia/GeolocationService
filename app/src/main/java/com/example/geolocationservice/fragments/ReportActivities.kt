package com.example.geolocationservice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.geolocationservice.R
import com.example.geolocationservice.database.DBHelper
import java.util.ArrayList

class ReportActivities(
    private val date: String
): Fragment()
{
    private lateinit var myView: View
    private lateinit var dbHelper: DBHelper

    private lateinit var headerLabel: TextView
    private lateinit var svContainer: LinearLayout
    private lateinit var btnBack: Button
    private lateinit var btnExport: Button

    private lateinit var activities: ArrayList<ArrayList<String>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myView = inflater.inflate(R.layout.report_activities_fragment, container, false)
        dbHelper = DBHelper(requireContext())

        headerLabel = myView.findViewById(R.id.tv_report_activities_header)
        svContainer = myView.findViewById(R.id.sv_container)
        btnBack = myView.findViewById(R.id.btn_back)
        btnExport = myView.findViewById(R.id.btn_export)

        activities = getAllActivitiesByDate(date)

        headerLabel.text = getString(R.string.report_activities_label).format(date)
        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        btnExport.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.export_not_realized), Toast.LENGTH_SHORT).show()
        }

        showActivities()

        return myView
    }

    private fun showActivities()
    {
        svContainer.removeAllViews()
        activities.forEach { act ->
            val textView = TextView(requireContext())
            val time = act[0]
            val coordinates = act[1]
            val (lat, lon) = coordinates.split("-").map { elem -> elem.trim() }
            textView.text = renderActivityText(lat, lon, time)
            svContainer.addView(textView)
        }
    }

    private fun renderActivityText(latitude: String, longitude: String, time: String): CharSequence
    {
        return "${getString(R.string.latitude)}: ${latitude}, " +
                "${getString(R.string.longitude).lowercase()}: ${longitude} " +
                "\t ${getString(R.string.time_pre)} ${time.split(" ")[1]}"
    }

    private fun getAllActivitiesByDate(date: String): ArrayList<ArrayList<String>>
    {
        return dbHelper.fetchRowsByDate(date)
    }
}