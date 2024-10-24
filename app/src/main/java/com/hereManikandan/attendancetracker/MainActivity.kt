package com.hereManikandan.attendancetracker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.hereManikandan.attendancetracker.Constants.SharedData
import com.hereManikandan.attendancetracker.db.AppDatabase
import com.hereManikandan.attendancetracker.db.dao.AttendanceDao
import com.hereManikandan.attendancetracker.db.dao.EventDao
import com.hereManikandan.attendancetracker.db.entity.Attendance
import com.hereManikandan.attendancetracker.db.entity.Event
import com.hereManikandan.attendancetracker.util.SharedPreferenceManager
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    // List to store scanned results
    private val scannedList = mutableListOf<String>()
    private  val db : AppDatabase = AppDatabase.getDatabase(this)

    private var eventid: Int = -1
    private var userid: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eventid = intent.getIntExtra(SharedData.EVENT_ID, -1)
        userid = SharedPreferenceManager.getInstance(this).getUser()!!.id

        val eventnameview :TextView = findViewById(R.id.eventnameview)

        lifecycleScope.launch {
            val event :Event = db.eventDao().getEventById(eventid)
            eventnameview.text = event.name
        }

        val scan: Button = findViewById(R.id.scan)

        scan.setOnClickListener {
            initiateScan()
        }

        val viewdetails:Button =findViewById(R.id.view)
        viewdetails.setOnClickListener{
            startActivity(Intent(this,ViewScannedDetails::class.java))
        }




    }

    private fun initiateScan() {
        // Start the barcode scanner
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a barcode or QR code")
        integrator.setCameraId(0) // Use a specific camera of the device
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                showScanResultDialog(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showScanResultDialog(scannedData: String) {
        // Inflate the custom dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_scan_result, null)

        val imageView: ImageView = dialogView.findViewById(R.id.dialog_image)
        val btnScanMore: Button = dialogView.findViewById(R.id.btn_scan_more)
        val btnSaveClose: Button = dialogView.findViewById(R.id.btn_save_close)

        // Build the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Handle "Scan More" button click
        btnScanMore.setOnClickListener {
            // Add scanned data to the list

            scannedList.add(scannedData)
            lifecycleScope.launch {
                putAttendance(scannedData) // Call the suspend function
            }
            dialog.dismiss() // Dismiss dialog
            initiateScan()    // Start a new scan
        }

        // Handle "Save & Close" button click
        btnSaveClose.setOnClickListener {
            // Add scanned data to the list
            scannedList.add(scannedData)
            lifecycleScope.launch {
                putAttendance(scannedData) // Call the suspend function
            } // Show a Toast with the data saved
            Toast.makeText(this, "Data Saved: $scannedList", Toast.LENGTH_LONG).show()
            dialog.dismiss() // Dismiss the dialog and go back to main screen
        }

        // Show the dialog
        dialog.show()
    }

     suspend fun putAttendance(rollno :String){

         val attendance :AttendanceDao = db.attendanceDao()
       val userAttendance:List<Attendance> = attendance.getAttendanceByUserId(userid)
         val existingAttendance = userAttendance.find { it.rollno == rollno && it.eventId == eventid }

         if (existingAttendance == null){
             val newAttendance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 Attendance(
                     userId = userid,
                     eventId = eventid,
                     rollno = rollno,
                     timestamp = LocalDateTime.now()

                 )
             } else {
                 TODO("VERSION.SDK_INT < O")
             }
             attendance.insertAttendance(newAttendance)
             Toast.makeText(this,"${rollno} saved",Toast.LENGTH_SHORT).show()
         }else{
             Toast.makeText(this,"${rollno} already exist in the event",Toast.LENGTH_LONG).show()
         }


     }

}
