package com.example.pricerunner

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.EdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pricerunner.databinding.ActivityPriceListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class PriceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPriceListBinding
    private val productList = mutableListOf<Information>()
    private lateinit var adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EdgeToEdge.enable(this)
        
        binding = ActivityPriceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        
        binding.backPriceList.setOnClickListener { finish() }

        val barcode = intent.getStringExtra(MainActivity.EXTRA_BARCODE) ?: ""
        if (barcode.isNotEmpty()) {
            fetchPrices(barcode)
        }
    }

    private fun setupRecyclerView() {
        adapter = RecyclerAdapter(productList)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PriceListActivity)
            adapter = this@PriceListActivity.adapter
        }
    }

    private fun fetchPrices(barcode: String) {
        showLoading(true)
        
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
                showError("Error fetching data")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun httpGet(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        
        return try {
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.connect()
            BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseAndDisplayResults(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val shoppingResults = jsonObject.optJSONArray("shopping_results")
            
            if (shoppingResults == null || shoppingResults.length() == 0) {
                showEmpty(true)
                return
            }

            val newItems = mutableListOf<Information>()
            for (i in 0 until shoppingResults.length()) {
                val item = shoppingResults.getJSONObject(i)
                newItems.add(
                    Information(
                        image = item.optString("image", ""),
                        title = item.optString("title", ""),
                        price = item.optString("price_raw", ""),
                        seller = item.optString("merchant", "")
                    )
                )
            }

            productList.clear()
            productList.addAll(newItems)
            adapter.notifyDataSetChanged()
            
            Toast.makeText(this, "${productList.size} item(s) found", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            showError("Error parsing results")
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showEmpty(show: Boolean) {
        binding.emptyText.visibility = if (show) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        showEmpty(true)
    }
}
