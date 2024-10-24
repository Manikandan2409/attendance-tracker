package com.hereManikandan.attendancetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hereManikandan.attendancetracker.db.entity.Attendance

class AttendanceAdapter(private val attendanceList: List<Attendance>) :
    RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    // ViewHolder class to represent each item view
    class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rollNoTextView: TextView = itemView.findViewById(R.id.rollnoTextView)
        val eventIdTextView: TextView = itemView.findViewById(R.id.eventIdTextView)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return AttendanceViewHolder(itemView)
    }

    // Bind data to the view holder (invoked by the layout manager)
    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendance = attendanceList[position]
        holder.rollNoTextView.text = attendance.rollno
        holder.eventIdTextView.text = attendance.eventId.toString()
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount() = attendanceList.size
}
