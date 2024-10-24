package com.hereManikandan.attendancetracker.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("eventId")]  // Adding indices for faster queries on foreign keys
)
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,               // Foreign key referencing the User entity
    val eventId: Int,              // Foreign key referencing the Event entity
    val rollno: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
