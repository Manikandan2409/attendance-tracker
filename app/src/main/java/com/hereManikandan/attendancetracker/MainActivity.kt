package com.hereManikandan.attendancetracker

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class MainActivity : AppCompatActivity() {

    // List to store scanned results
    private val scannedList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        integrator.setBeepEnabled(true)
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
            dialog.dismiss() // Dismiss dialog
            initiateScan()    // Start a new scan
        }

        // Handle "Save & Close" button click
        btnSaveClose.setOnClickListener {
            // Add scanned data to the list
            scannedList.add(scannedData)
            // Show a Toast with the data saved
            Toast.makeText(this, "Data Saved: $scannedList", Toast.LENGTH_LONG).show()
            dialog.dismiss() // Dismiss the dialog and go back to main screen
        }

        // Show the dialog
        dialog.show()
    }
}
