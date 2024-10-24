//package com.hereManikandan.attendancetracker.db
//
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import android.content.Context
//import android.util.Log
//import android.widget.Toast
//import androidx.collection.intSetOf
//import com.hereManikandan.attendancetracker.db.dao.AttendanceDao
//import com.hereManikandan.attendancetracker.db.dao.EventDao
//import com.hereManikandan.attendancetracker.db.dao.UserDao
//import com.hereManikandan.attendancetracker.db.entity.Attendance
//import com.hereManikandan.attendancetracker.db.entity.Event
//import com.hereManikandan.attendancetracker.db.entity.User
//
//@Database(entities = [User::class, Event::class, Attendance::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//
//    abstract fun userDao(): UserDao
//    abstract fun eventDao(): EventDao
//    abstract fun attendanceDao(): AttendanceDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        suspend fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: createInstance(context)
//        }
//
//        suspend fun createInstance(context :Context) : AppDatabase{
//            synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "app_database"
//                ).build()
//                INSTANCE = instance
//                this.createUser(context)
//                return instance
//            }
//        }
//
//        private suspend  fun createUser(context: Context) {
//            // Get the database instance and user DAO
//            val userDao: UserDao? = INSTANCE?.userDao()
//
//            // Check if userDao is not null, and insert the user
//            userDao?.let {
//                // Insert a user with name "admin" and password "admin"
//                val user = User(name = "admin", password = "admin")
//                it.insertUser(user)
//                Toast.makeText(context,"${user.name} created successfully",Toast.LENGTH_SHORT).show()
//            } ?: run {
//                // Handle the case where INSTANCE is null
//                Log.e("Database Error", "Database instance is null!")
//            }
//        }
//
//    }
//
//
//}
package com.hereManikandan.attendancetracker.db

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hereManikandan.attendancetracker.Constants.Credentials
import com.hereManikandan.attendancetracker.db.dao.AttendanceDao
import com.hereManikandan.attendancetracker.db.dao.EventDao
import com.hereManikandan.attendancetracker.db.dao.UserDao
import com.hereManikandan.attendancetracker.db.entity.Attendance
import com.hereManikandan.attendancetracker.db.entity.Event
import com.hereManikandan.attendancetracker.db.entity.User
import com.hereManikandan.attendancetracker.util.LocalDateTimeConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, Event::class, Attendance::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance

                // Create default admin user after the database is built
                CoroutineScope(Dispatchers.IO).launch {
                    createUser(context)
                }

                instance
            }
        }

        private suspend fun createUser(context: Context) {
            val userDao: UserDao? = INSTANCE?.userDao()

            userDao?.let {
                // Insert a default admin user if it doesn't exist
                val existingUser = it.getUserByName(Credentials.ADMIN) // Assumes you have a method to check by name
                if (existingUser == null) {
                    val user = User(name = Credentials.ADMIN, password = Credentials.ADMIN_PASS)
                    it.insertUser(user)

                    // Show the toast on the main thread
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "${user.name} created successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("User Exists", "Admin user already exists")
                }
            } ?: run {
                Log.e("Database Error", "Database instance is null!")
            }
        }
    }
}
