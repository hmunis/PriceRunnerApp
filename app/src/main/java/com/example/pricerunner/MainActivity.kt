package com.example.pricerunner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.EdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pricerunner.databinding.ActivityMainBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonScan.setOnClickListener {
            barcodeLauncher.launch(ScanOptions())
        }

        binding.buttonPriceList.setOnClickListener {
            if (scannedBarcode.isNotEmpty()) {
                val intent = Intent(this, PriceListActivity::class.java).apply {
                    putExtra(EXTRA_BARCODE, scannedBarcode)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please scan a barcode first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    companion object {
        const val EXTRA_BARCODE = "extra_barcode"
    }
}
