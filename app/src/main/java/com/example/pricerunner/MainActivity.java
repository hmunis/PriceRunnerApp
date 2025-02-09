package com.example.pricerunner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    Button scanButton, aboutButton, priceListButton;

    public static String barcodeNumber = "";
    // 8681212063520 - sleepy ıslak mendil 90 adet barkod

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        scanButton = findViewById(R.id.buttonScan);
        aboutButton = findViewById(R.id.buttonAbout);
        priceListButton = findViewById(R.id.buttonPriceList);

        scanButton.setOnClickListener(view -> {
            ScanOptions scanOptions = new ScanOptions();
            barcodeLauncher.launch(scanOptions);
        });

        priceListButton.setOnClickListener(view -> {
            if (barcodeNumber.length() != 0) {
                Intent intent = new Intent(MainActivity.this, PriceListActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Please scan a barcode", Toast.LENGTH_SHORT).show();
            }
        });

        aboutButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AboutActivity.class)));

    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    barcodeNumber = result.getContents();
                    Toast.makeText(MainActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_SHORT).show();
                }
            });
}