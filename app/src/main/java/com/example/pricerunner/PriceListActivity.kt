package com.example.pricerunner

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.EdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class PriceListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val productList = mutableListOf<Information>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EdgeToEdge.enable(this)
        setContentView(R.layout.activity_price_list)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.backPriceList).setOnClickListener { finish() }

        val barcode = intent.getStringExtra(MainActivity.EXTRA_BARCODE) ?: ""
        if (barcode.isNotEmpty()) {
            fetchPrices(barcode)
        }
    }

    private fun fetchPrices(barcode: String) {
        val apiUrl = "https://api.scaleserp.com/search?api_key=BCBAE8D55801452F8BB6BA48A7930127" +
                "&search_type=shopping&q=$barcode&location=Istanbul%2CTurkey" +
                "&google_domain=google.com.tr&gl=tr&hl=tr"

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    httpGet(apiUrl)
                }
                parseAndDisplayResults(result)
            } catch (e: Exception) {
                Toast.makeText(this@PriceListActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun httpGet(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        
        return try {
            connection.connect()
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            reader.use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseAndDisplayResults(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val shoppingResults = jsonObject.optJSONArray("shopping_results")
            
            if (shoppingResults == null || shoppingResults.length() == 0) {
                Toast.makeText(this, "No items found!", Toast.LENGTH_SHORT).show()
                return
            }

            for (i in 0 until shoppingResults.length()) {
                val item = shoppingResults.getJSONObject(i)
                productList.add(
                    Information(
                        image = item.optString("image", ""),
                        title = item.optString("title", ""),
                        price = item.optString("price_raw", ""),
                        seller = item.optString("merchant", "")
                    )
                )
            }

            recyclerView.adapter = RecyclerAdapter(productList)
            Toast.makeText(this, "${productList.size} item(s) found!", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error parsing results", Toast.LENGTH_SHORT).show()
        }
    }
}
