package com.hereManikandan.attendancetracker.util


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.hereManikandan.attendancetracker.Constants.SharedData
import com.hereManikandan.attendancetracker.db.entity.User

public class SharedPreferenceManager private constructor(context: Context) {

    // SharedPreferences instance
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SharedData.SHARED_PREFERENCE,
        Context.MODE_PRIVATE
    )

    // Editor for SharedPreferences
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        @Volatile
        private var INSTANCE: SharedPreferenceManager? = null

        fun getInstance(context: Context): SharedPreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPreferenceManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun saveUser(user: User) {
        val gson = Gson()
        val userJson = gson.toJson(user)
        editor.putString(SharedData.LOGGED_IN_USER, userJson)
        editor.apply()  // Apply changes
    }

    // Retrieve User object from SharedPreferences
    fun getUser(): User? {
        val gson = Gson()
        val userJson = sharedPreferences.getString(SharedData.LOGGED_IN_USER, null)

        // Check if user data exists
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    // Clear user data (optional utility)
    fun clearUserData() {
        editor.remove(SharedData.LOGGED_IN_USER)
        editor.apply()
    }
}
