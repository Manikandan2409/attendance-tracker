package com.hereManikandan.attendancetracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.hereManikandan.attendancetracker.Constants.SharedData
import com.hereManikandan.attendancetracker.db.AppDatabase
import com.hereManikandan.attendancetracker.db.entity.User
import com.hereManikandan.attendancetracker.util.SharedPreferenceManager
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val user: User? = getUserFromPreferences()

        if (user != null) {
            startActivity(Intent(this, Dashboard::class.java))
            finish() // Finish Login activity to avoid going back to it
        }
        val  db:AppDatabase = AppDatabase.getDatabase(this)


        val loginbutton :Button = findViewById(R.id.loginButton)

        loginbutton.setOnClickListener {
            val  emailfield :EditText = findViewById(R.id.emailEditText)
            val  password : EditText = findViewById(R.id.passwordEditText)
            val emailvalue = emailfield.text.toString().trim()
            val  passvalue = password.text.toString().trim()
            Toast.makeText(this,emailvalue,Toast.LENGTH_LONG).show()
            if (emailvalue.isEmpty() || passvalue.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter email & password",
                    Toast.LENGTH_SHORT
                ).show()
            }

            lifecycleScope.launch {
                val user = db.userDao().getUserByName(emailvalue)

                if (user != null && user.password == passvalue) {
                    saveUserToPreferences(user)
                    startActivity(Intent(this@Login, Dashboard::class.java))
                } else {
                    Toast.makeText(this@Login, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    private fun saveUserToPreferences(user: User) {
        val manager :SharedPreferenceManager = SharedPreferenceManager.getInstance(this)
        manager.saveUser(user)
    }

fun getUserFromPreferences(): User? {
    val manager :SharedPreferenceManager = SharedPreferenceManager.getInstance(this)
    return manager.getUser()
}

}