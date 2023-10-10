package com.drd.drdtrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

public class Qr_code_scan extends AppCompatActivity implements View.OnClickListener {
    Button scanBtn;
    TextView messageText, messageFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scan);

        // referencing and initializing
        // the button and textviews
        scanBtn = findViewById(R.id.scanBtn);
        messageText = findViewById(R.id.textContent);
        messageFormat = findViewById(R.id.textFormat);

        // adding listener to the button
        scanBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // we need to create the object
        // of IntentIntegrator class
        // which is the class of QR library
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                messageText.setText(intentResult.getContents());
                messageFormat.setText(intentResult.getFormatName());

                try {
                    JSONArray jArray = new JSONArray(intentResult.getContents());
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject jsonObject = jArray.getJSONObject(i);
                        String time_id = jsonObject.getString("time_id");
//                        date_id = jsonObject.getString("date_id");
//                        return_id = jsonObject.getString("return_id");
//                        value_id = jsonObject.getString("value_id");

                        Toast.makeText(getApplicationContext(), time_id.toString(), Toast.LENGTH_LONG).show();

                        /*if(return_id.equals("1"))
                        {
                            //Toast.makeText(getApplicationContext(), value_id.toString(), Toast.LENGTH_LONG).show();
                            camera.stopScanner();

                            new json_insert_drd_attendance().execute();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Scanner try again later", Toast.LENGTH_LONG).show();
                        }*/
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Scanner error rply", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}