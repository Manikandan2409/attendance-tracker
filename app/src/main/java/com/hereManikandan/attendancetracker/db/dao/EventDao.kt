package com.hereManikandan.attendancetracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hereManikandan.attendancetracker.db.entity.Event

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events WHERE userId = :userId")
    suspend fun getEventsByUserId(userId: Int): List<Event>

    @Query("SELECT * FROM events WHERE id =:id")
    suspend fun  getEventById(id :Int) :Event
}
