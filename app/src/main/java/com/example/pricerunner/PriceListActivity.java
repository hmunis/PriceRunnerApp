package com.example.pricerunner;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PriceListActivity extends AppCompatActivity {

    private String barcodeText;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    Button backPriceList;

    int dataCount = 0;

    private ArrayList<Information> informationArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_price_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        barcodeText = MainActivity.barcodeNumber;

        recyclerView = findViewById(R.id.recyclerView);
        backPriceList = findViewById(R.id.backPriceList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PriceListActivity.this));
        recyclerView.getLayoutManager().scrollToPosition(0);

        backPriceList.setOnClickListener(view -> finish());

        new HTTPAsyncTask().execute("https://api.scaleserp.com/search?api_key=BCBAE8D55801452F8BB6BA48A7930127&search_type=shopping&q="+barcodeText+"&location=Istanbul%2CTurkey&google_domain=google.com.tr&gl=tr&hl=tr");
    }

    private class HTTPAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return HttpGet(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                parseJSON(result);
                adapter = new RecyclerAdapter(informationArrayList, PriceListActivity.this);
                recyclerView.setAdapter(adapter);
                if (dataCount != 0)
                    Toast.makeText(PriceListActivity.this, dataCount + " item(s) found!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(PriceListActivity.this, "No items found!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String HttpGet(String myURL) throws IOException {
        InputStream inputStream = null;
        String result = "";

        URL url = new URL(myURL);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.connect();

        inputStream = conn.getInputStream();

        if (inputStream != null)
            result = convertInputStreamToString(inputStream);
        else
            result = "Did not work!";

        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public void parseJSON(String result) throws JSONException {
        JSONArray jsonArray = new JSONObject(result).getJSONArray("shopping_results");
        for(int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = new JSONObject(result).getJSONArray("shopping_results").getJSONObject(i);
            String title = jsonObject.getString("title");
            String price = jsonObject.getString("price_raw");
            String image = jsonObject.getString("image");
            String seller = jsonObject.getString("merchant");
            dataCount = jsonArray.length();
            informationArrayList.add(new Information(image,title,price,seller));
        }

    }
}