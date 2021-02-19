package com.example.qrdolgozat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private Button btnScan, btnKiir;
    private TextView tvQRkod;

    private boolean irasiEngedelyVanE;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        btnScan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setPrompt("QR kód beolvasás, Rendek Dániel 2/14SL");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });

        btnKiir.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (tvQRkod.getText() == "")
                {
                    Toast.makeText(MainActivity.this, "Nincs beolvasott QR kód!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Date datum = Calendar.getInstance().getTime();
                SimpleDateFormat formatum = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formazottDatum = formatum.format(datum);
                String szoveg = tvQRkod.getText() + ", " + formazottDatum;
                String allapot = Environment.getExternalStorageState();

                if (allapot.equals(Environment.MEDIA_MOUNTED))
                {
                    File fajl = new File(Environment.getExternalStorageDirectory(), "scannedCodes.csv");

                    if (irasiEngedelyVanE)
                    {
                        try
                        {
                            BufferedWriter bw = new BufferedWriter(new FileWriter(fajl, true));
                            bw.append(szoveg + System.lineSeparator());
                            bw.close();
                            Toast.makeText(MainActivity.this, "QR kód elmentve.", Toast.LENGTH_SHORT).show();
                        }
                        catch (IOException e)
                        {
                            Toast.makeText(MainActivity.this, "Hiba! QR kód nem lett elmentve.", Toast.LENGTH_SHORT).show();
                            //e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        IntentResult eredmeny = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (eredmeny != null)
        {
            if (eredmeny.getContents() == null)
            {
                Toast.makeText(this, "Szkennelő bezárva.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                tvQRkod.setText(eredmeny.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init()
    {
        btnScan = findViewById(R.id.btnScan);
        btnKiir = findViewById(R.id.btnKiir);
        tvQRkod = findViewById(R.id.tvQRkodkep);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED)
        {
            irasiEngedelyVanE = true;
        }
    }
}