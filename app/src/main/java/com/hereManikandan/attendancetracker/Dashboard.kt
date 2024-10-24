package com.hereManikandan.attendancetracker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hereManikandan.attendancetracker.Constants.SharedData
import com.hereManikandan.attendancetracker.db.AppDatabase
import com.hereManikandan.attendancetracker.db.dao.EventDao
import com.hereManikandan.attendancetracker.db.entity.Event
import com.hereManikandan.attendancetracker.db.entity.User
import com.hereManikandan.attendancetracker.util.SharedPreferenceManager
import kotlinx.coroutines.launch
import java.time.LocalDateTime

import android.widget.LinearLayout.LayoutParams as LinearLayoutParams

class Dashboard : AppCompatActivity() {


    private lateinit var cardContainer: LinearLayout


    private var cardCount = 0
    private  val eventnamesize =24f
    private  val eventdescsize =18f

    private val eventnames :MutableList<String> = mutableListOf()
   private val eventdesc :MutableList<String> = mutableListOf()

    private lateinit var  eventname :EditText
    private  lateinit var eventDesc :EditText
    private  lateinit var eventdate :EditText
    private  lateinit var eventtime : EditText
    private  lateinit var  addButton :Button
    private  lateinit var  cancelButton : Button

    val db : AppDatabase = AppDatabase.getDatabase(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        // pre-settlement


       var  user:User? = getUserFromPreferences()
        if (user != null){
            Toast.makeText(this,"Welcome ${user.name}",Toast.LENGTH_LONG).show()
            val username:TextView = findViewById(R.id.username)
            username.text = user.name
            username.setTextColor(Color.BLUE)
            loadAllPreviousEvents()
        }else{
            finish()
        }



        cardContainer = findViewById(R.id.dynamiccard)
        val addCardButton: FloatingActionButton = findViewById(R.id.addEvent)

        addCardButton.setOnClickListener {
            Toast.makeText(this,
            "count: $cardCount",Toast.LENGTH_LONG).show()
            getEventDetail()

        }
    }

    @SuppressLint("MissingInflatedId")
    fun getEventDetail(){
        val dialogView = layoutInflater.inflate(R.layout.create_event, null);

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        eventname   = dialogView.findViewById(R.id.event_name)
        eventDesc   = dialogView.findViewById(R.id.event_description)
        eventdate   = dialogView.findViewById(R.id.editTexteventDate)
        eventtime   = dialogView.findViewById(R.id.editTextEventTime)
        addButton   = dialogView.findViewById(R.id.addbutton)
        cancelButton= dialogView.findViewById(R.id.canceleventbutton)

        eventdate.setOnClickListener {
            showDatePicker()
        }
        eventtime.setOnClickListener {
            showTimePicker()
        }



        addButton.setOnClickListener {
           if (!validateInputs()){
               return@setOnClickListener
           }


            val eventnameval= eventname.text.toString().trim()
            val  eventDescval = eventDesc.text.toString().trim()
            val date = eventdate.text.toString()
            val  time = eventtime.text.toString()




            if (eventnameval.isEmpty()){
                Toast.makeText(this,
                "Event name Empty!",
                Toast.LENGTH_LONG).show()
            }
            lifecycleScope.launch {
                val eventDao: EventDao = db.eventDao()

                // Insert the event into the database

                val newEvent = Event(name = eventnameval, description = eventDescval, userId = getUserFromPreferences()!!.id, eventDate = date, eventTime = time)
                eventDao.insertEvent(newEvent)

                // Add the event details to the lists
                eventnames.add(eventnameval)
                eventdesc.add(eventDescval)

                // Dismiss the dialog and add the CardView
                dialog.dismiss()
                val createdAt =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDateTime.now().toLocalDate().toString()
                    } else {
                        "Date unavailable"
                    }
                cardCount++
                addCardView(cardCount,eventnameval,eventDescval,createdAt,date,time)
            }
        }

        cancelButton.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()
    }







    private fun addCardView(id: Int, eventnameval: String, eventdescval: String, createdAtval: String, eventdate: String, eventtime: String) {

        // Create a new CardView
        val cardView = CardView(this).apply {
            layoutParams = LinearLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }

            radius = 16f
            cardElevation = 8f
        }


        val card_linear_layout = LinearLayout(this).apply {
            layoutParams = LinearLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
        }
    //   card_linear_layout.setBackgroundColor(Color.BLUE)
//        val eventlayout : LinearLayout = LinearLayout(this).apply {
//            layoutParams = LinearLayoutParams(
//                210,
//                LinearLayout.LayoutParams.MATCH_PARENT
//            )
//            orientation = LinearLayout.VERTICAL
//        }
//        eventlayout.setBackgroundColor(Color.BLUE)
//        cardView.addView(eventlayout)



        val eventname: TextView = createText(eventnameval, eventnamesize)
        var drawable = ContextCompat.getDrawable(this, R.drawable.baseline_event_24)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        eventname.setCompoundDrawables(drawable, null, null, null)

        // Add the three-dot menu ImageView
        val menuButton = ImageView(this).apply {
            setImageResource(R.drawable.baseline_more_vert_24) // Use your own drawable for the three-dot icon
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END // Align the button to the right

            }

            setPadding(16, 0, 16, 0) // Add some padding for better spacing
        }

        // Handle menu click
        menuButton.setOnClickListener {
            val popup = PopupMenu(this, menuButton)
            popup.inflate(R.menu.menu_card_options) // Inflate your menu resource
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_manage -> {
                        // Handle "Manage" action
                        Toast.makeText(this, "Manage clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_scan -> {
                        // Handle "Scan" action
                        Toast.makeText(this, "Scan clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_certificate -> {
                        // Handle "Certificate" action
                        Toast.makeText(this, "Certificate clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        val eventnameLayout: LinearLayout = LinearLayout(this).apply {
            layoutParams = LinearLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
        }

        eventnameLayout.addView(eventname)
       // eventnameLayout.addView(menuButton) // Add the menu button to the layout
        card_linear_layout.addView(menuButton)
        val eventdesc: TextView = createText(eventdescval, eventdescsize)
        val eventdateview: TextView = createText(eventdate, eventdescsize)
        drawable = ContextCompat.getDrawable(this, R.drawable.baseline_calendar_month_24)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        eventdateview.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        val descDateLayout: LinearLayout = createLinearLayout()
        descDateLayout.addView(eventdesc)
        descDateLayout.addView(eventdateview)

        val createdate: TextView = createText(createdAtval, eventdescsize)
        drawable = ContextCompat.getDrawable(this, R.drawable.baseline_event_24)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        createdate.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        val eventtimeview: TextView = createText(eventtime, eventdescsize)
        drawable = ContextCompat.getDrawable(this, R.drawable.baseline_access_time_24)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        eventtimeview.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        val createdateTimeLayout: LinearLayout = createLinearLayout()
        createdateTimeLayout.addView(createdate)
        createdateTimeLayout.addView(eventtimeview)

        card_linear_layout.apply {
            addView(eventnameLayout)
            createSpaceView()
            addView(descDateLayout)
            createSpaceView()
            addView(createdateTimeLayout)
        }

       cardView.addView(card_linear_layout)
        cardView.id = id

        cardView.setOnClickListener {
            val intent = Intent(this, EventBoard::class.java)
            intent.putExtra(SharedData.EVENT_ID, cardView.id)
            startActivity(intent)
        }
       // Toast.makeText(this,"${eventnameval} created successful",Toast.LENGTH_SHORT).show()
        cardContainer.addView(cardView)

    }


    fun  createText(value:String, size:Float): TextView{

          val VIEW_PADDING =16

        val view:TextView = TextView(this).apply {
            layoutParams = LinearLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            text="${value}"
            textSize=size
            setPadding(VIEW_PADDING,VIEW_PADDING,VIEW_PADDING,VIEW_PADDING)

        }

        return view
    }

    fun createSpaceView(): View {
        return View(this).apply {
            layoutParams = LinearLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                16
            )
        }
    }


    fun getUserFromPreferences(): User? {

            val manager : SharedPreferenceManager = SharedPreferenceManager.getInstance(this)
             return manager.getUser()
    }

    fun loadAllPreviousEvents(){
        lifecycleScope.launch {
            val event :EventDao = db.eventDao()
           val events: List<Event> = event.getEventsByUserId(getUserFromPreferences()!!.id)
            for (event in events){
                addCardView(event.id,event.name,event.description,event.createdAt.toString(),event.eventDate,event.eventTime)
            }
        }
    }


    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format date as dd/MM/yyyy and set it to the EditText
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                eventdate.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Format time as HH:mm and set it to the EditText
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                eventtime.setText(formattedTime)
            },
            hour, minute, true
        )

        timePickerDialog.show()
    }


    private fun validateInputs()  :  Boolean {
        val name  = eventname.text.toString().trim()
        val desc = eventDesc.text.toString().trim()
        val date = eventdate.text.toString()
        val time = eventtime.text.toString()



        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return false
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private  fun createLinearLayout()  : LinearLayout{
        val layout : LinearLayout = LinearLayout(this).apply {
            layoutParams = LinearLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            this.orientation = LinearLayout.HORIZONTAL
        }
    return  layout
    }


}