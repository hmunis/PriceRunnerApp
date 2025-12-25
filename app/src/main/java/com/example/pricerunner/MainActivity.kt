package com.example.pricerunner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.EdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {

    private var scannedBarcode: String = ""

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            scannedBarcode = result.contents
            Toast.makeText(this, "Scanned: ${result.contents}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EdgeToEdge.enable(this)
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.buttonScan).setOnClickListener {
            barcodeLauncher.launch(ScanOptions())
        }

        findViewById<Button>(R.id.buttonPriceList).setOnClickListener {
            if (scannedBarcode.isNotEmpty()) {
                val intent = Intent(this, PriceListActivity::class.java).apply {
                    putExtra(EXTRA_BARCODE, scannedBarcode)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please scan a barcode", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.buttonAbout).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    companion object {
        const val EXTRA_BARCODE = "extra_barcode"
    }
}
