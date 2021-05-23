package com.example.geolocationservice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.geolocationservice.R
import com.example.geolocationservice.database.DBHelper

class ShowActivities: Fragment()
{
    private lateinit var myView: View
    private lateinit var tvLabel: TextView
    private lateinit var spinnerDates: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        myView = inflater.inflate(R.layout.view_activities_fragment, container, false)

        tvLabel = myView.findViewById(R.id.tv_view_activities_label)
        spinnerDates = myView.findViewById(R.id.spinner_choose_date)

        initializeDateSpinner()

        spinnerDates.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerDates[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?)
            {
                Toast.makeText(context, R.string.choose_date, Toast.LENGTH_SHORT).show()
            }

        }

        return myView
    }

    private fun initializeDateSpinner()
    {
        context?.let { context ->
            val days = DBHelper(context).getAllDays()
            spinnerDates.adapter = ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, days)
        }
    }
}