package com.example.geolocationservice.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import com.example.geolocationservice.Constants
import java.util.ArrayList

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

    fun getAllDays(): ArrayList<String>
    {
        writableDatabase.let {
            val cursor = it.query(
                TABLE_JOURNAL,           // table
                arrayOf("strftime('%d-%m-%Y', dt) date"),    // columns
                null,            // where
                null,         // params for where
                "date",             // group by
                null,               // having
                "date"               // order by
            )
            val result = ArrayList<String>()

            if ( cursor.moveToFirst() ) {
                do {
                    result.add(cursor.getString(cursor.getColumnIndex("date")))
                } while ( cursor.moveToNext() )
            }
            cursor.close()

            return result
        }
    }

    /**
     * Returns all rows which date equals argument date
     * date must be like "dd-mm-YYYY"!
     *
     * @param date String
     */
    fun fetchRowsByDate(date: String): ArrayList<ArrayList<String>>
    {
        writableDatabase.let {
            val cursor = it.query(
                TABLE_JOURNAL,           // table
                arrayOf("dt date, loc"),    // columns
                "strftime('%d-%m-%Y', date) = ?",            // where
                arrayOf(date),         // params for where
                null,             // group by
                null,               // having
                "date"               // order by
            )
            val result = ArrayList<ArrayList<String>>()

            if ( cursor.moveToFirst() ) {
                do {
                    val data = ArrayList(
                        listOf<String>(
                            cursor.getString(cursor.getColumnIndex("date")),
                            cursor.getString(cursor.getColumnIndex("loc"))
                        )
                    )
                    result.add(data)
                } while ( cursor.moveToNext() )
            }
            cursor.close()

            return result
        }
    }
}