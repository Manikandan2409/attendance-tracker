package com.hereManikandan.attendancetracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hereManikandan.attendancetracker.db.entity.Attendance

@Dao
interface AttendanceDao {
    @Insert
    suspend fun insertAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance WHERE userId = :userId")
    suspend fun getAttendanceByUserId(userId: Int): List<Attendance>
}
