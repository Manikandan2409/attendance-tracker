package com.hereManikandan.attendancetracker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ViewUtils.dpToPx

class Dashboard : AppCompatActivity() {

    var previousCardBottomMargin = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        var dashboard: ConstraintLayout = findViewById(R.id.dashboard)
        val add :FloatingActionButton = findViewById(R.id.addEvent)
        add.setOnClickListener {
            Toast.makeText(this,
            "AddButton clicked",
            Toast.LENGTH_LONG).show()
            dashboard.addView(createCard())
        }


    }

    fun createCard():CardView{

        var  newCard:CardView = CardView(this)
        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,  // Width set to match_parent
            TypedValue.applyDimension(  // Height set to 40dp
                TypedValue.COMPLEX_UNIT_DIP,
                40f,
                resources.displayMetrics
            ).toInt()
        )

        newCard.layoutParams=params
        newCard.cardElevation=16f
        newCard.setPadding(16,16,16,16)
        newCard.useCompatPadding=true
        newCard.preventCornerOverlap =true
        newCard.radius=60f

        return  newCard
    }


}