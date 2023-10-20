package com.drd.drdtrackingapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

public class Meter_photo extends AppCompatActivity {
    UserSessionManager session;
    String user_code="",user_altercode = "";

    Bitmap bitmap;
    Button take_photo,galery_select;
    private ImageView imageView;
    Button UploadImageServer, UploadImageServer1;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meter_photo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags== Configuration.UI_MODE_NIGHT_NO || nightModeFlags== Configuration.UI_MODE_NIGHT_UNDEFINED) {
                window.setStatusBarColor(getResources().getColor(R.color.header_bg_light));
            }
            if (nightModeFlags== Configuration.UI_MODE_NIGHT_YES) {
                window.setStatusBarColor(getResources().getColor(R.color.header_bg_dark));
            }
        }

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_code = user.get(UserSessionManager.KEY_USERCODE);
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);

        imageView = (ImageView) findViewById(R.id.imageView);
        take_photo = findViewById(R.id.take_photo);
        //galery_select = findViewById(R.id.galery_select);
        UploadImageServer = (Button) findViewById(R.id.buttonUpload);
        UploadImageServer1 = (Button) findViewById(R.id.buttonUpload1);

        UploadImageServer.setVisibility(View.GONE);
        UploadImageServer1.setVisibility(View.VISIBLE);

        /*galery_select.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image from gallery"), 1989);

            }
        });*/

        take_photo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {
        super.onActivityResult(RC, RQC, I);
        //Toast.makeText(User_image_uploading.this, String.valueOf(RC), Toast.LENGTH_LONG).show();
        if (RC == CAMERA_REQUEST) {
            bitmap = (Bitmap) I.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);

            UploadImageServer.setVisibility(View.VISIBLE);
            UploadImageServer1.setVisibility(View.GONE);
        }
        if (RC == 1989) {
            Uri uri = I.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                UploadImageServer.setVisibility(View.VISIBLE);
                UploadImageServer1.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}