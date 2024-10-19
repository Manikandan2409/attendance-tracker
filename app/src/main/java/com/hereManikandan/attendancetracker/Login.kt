package com.hereManikandan.attendancetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val loginbutton :Button = findViewById(R.id.loginButton)

        loginbutton.setOnClickListener {
            val  emailfield :EditText = findViewById(R.id.emailEditText)
            val  password : EditText = findViewById(R.id.passwordEditText)
            val emailvalue = emailfield.text.toString().trim()
            val  passvalue = password.text.toString().trim()
            if (emailvalue.equals(Constants.Credentials.ADMIN) && passvalue.equals(Constants.Credentials.ADMIN_PASS))
                startActivity(Intent(this,Dashboard::class.java))
        }
    }
}