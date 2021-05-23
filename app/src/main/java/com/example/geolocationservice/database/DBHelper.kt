package com.example.geolocationservice.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import com.example.geolocationservice.Constants

class DBHelper(context: Context):
    SQLiteOpenHelper(context, Constants.DB_NAME, null, Constants.DB_VER)
{
    companion object
    {
        private const val TABLE_JOURNAL = "loc_journal"
    }

    override fun onCreate(db: SQLiteDatabase?)
    {
        db?.execSQL(
            "CREATE TABLE $TABLE_JOURNAL (" +
                    "id integer primary key," +
                    "dt datetime default current_timestamp," +
                    "loc text" +
                    ")")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
    {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_JOURNAL")
        onCreate(db)
    }

    fun saveLocation(location: Location)
    {
        writableDatabase.let {
            val contentValues = ContentValues()
            contentValues.put("loc", "${location.altitude} - ${location.longitude}")

            it.insert(TABLE_JOURNAL, null, contentValues)
        }
    }

    fun getAllDays(): MutableList<String>
    {
        writableDatabase.let {
            val cursor = it.query(
                TABLE_JOURNAL,           // table
                arrayOf("dt", "loc"),    // columns
                null,            // where
                null,         // params for where
                "CAST(dt AS DATE)",             // group by
                null,               // having
                "dt"               // order by
            )
            val result = mutableListOf<String>()

            if ( cursor.moveToFirst() ) {
                val dtIndex = cursor.getColumnIndex("dt")
                //val locIndex = cursor.getColumnIndex("loc")

                do {
                    result.add("${cursor.getString(dtIndex)}")
                } while ( cursor.moveToNext() )
            }
            cursor.close()

            return result
        }
    }

/*    fun findLocationsByDate(date: Date)
    {

    }*/
}