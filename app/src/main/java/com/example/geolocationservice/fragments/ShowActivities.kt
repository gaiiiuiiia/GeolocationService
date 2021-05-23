package com.example.geolocationservice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.geolocationservice.R
import com.example.geolocationservice.database.DBHelper

class ShowActivities: Fragment()
{
    private lateinit var myView: View
    private lateinit var tvLabel: TextView
    private lateinit var tvDatesDropdownList: AutoCompleteTextView

    override fun onResume() {
        super.onResume()
        initDatesDropdownList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        myView = inflater.inflate(R.layout.view_activities_fragment, container, false)
        tvLabel = myView.findViewById(R.id.tv_view_activities_label)

        tvLabel.text = getString(R.string.show_activities_label)

        return myView
    }

    private fun initDatesDropdownList()
    {
        tvDatesDropdownList =  myView.findViewById(R.id.tv_view_activities_list)
        tvDatesDropdownList.focusable = AutoCompleteTextView.FOCUSABLE
        var days = ArrayList<String>()
        requireContext().let { context ->
            days = DBHelper(context).getAllDays()
            val arrayAdapter = ArrayAdapter(context, R.layout.dropdown_item_view_activities, days)
            tvDatesDropdownList.setAdapter(arrayAdapter)
        }
        tvDatesDropdownList.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position) as String

                requireActivity()
                    .supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, ReportActivities(selectedItem))
                    .addToBackStack(null)
                    .commit()

                Toast.makeText(context, selectedItem, Toast.LENGTH_SHORT).show()
            }
        }
        tvDatesDropdownList.setText(tvDatesDropdownList.adapter.getItem(tvDatesDropdownList.adapter.count - 1).toString(), false)
    }
}