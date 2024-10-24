package com.hereManikandan.attendancetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hereManikandan.attendancetracker.db.AppDatabase
import com.hereManikandan.attendancetracker.db.entity.Attendance
import com.hereManikandan.attendancetracker.util.SharedPreferenceManager
import kotlinx.coroutines.launch

class ViewScannedDetails : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val db: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_scanned_details)

        val manager :SharedPreferenceManager = SharedPreferenceManager.getInstance(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Launch coroutine to fetch data from the database
        lifecycleScope.launch {
            val attendanceList: List<Attendance> = db.attendanceDao().getAttendanceByUserId(manager.getUser()!!.id) // Fetch all attendance records
            attendanceAdapter = AttendanceAdapter(attendanceList)
            recyclerView.adapter = attendanceAdapter
        }

    }
}