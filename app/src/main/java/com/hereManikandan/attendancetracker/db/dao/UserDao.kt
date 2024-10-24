package com.hereManikandan.attendancetracker.db.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hereManikandan.attendancetracker.db.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?


    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun getUserByName(name: String): User?
}
