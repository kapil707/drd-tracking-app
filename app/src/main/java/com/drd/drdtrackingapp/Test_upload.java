package com.drd.drdtrackingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Test_upload extends AppCompatActivity {
    private File photoFile;
    private ImageCapture imageCapture;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);

        Button captureButton = findViewById(R.id.captureButton);
        Button captureButton1 = findViewById(R.id.captureButton1);

        // Initialize the ImageCapture use case
        imageCapture = new ImageCapture.Builder().build();

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1894);
                }

//                if (ContextCompat.checkSelfPermission(getApplicationContext(),
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(intent, 10);
//                } else {
//                    ActivityCompat.requestPermissions(Test_upload.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//                }
            }
        });

        captureButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }

    Uri selectedImage;
    String path;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(Test_upload.this, String.valueOf(requestCode), Toast.LENGTH_LONG).show();

        if (requestCode == 1894 && resultCode == RESULT_OK) {


            selectedImage = data.getData();

            Bundle ext = data.getExtras();
            Bitmap imagebit = (Bitmap) ext.get("data");

            try {
                saveImage(imagebit);
            } catch (IOException ex) {

            }

            //uploadFile(selectedImage, "My Image");

//            Uri uri = data.getData();
//            Context context = Test_upload.this;
//            path = RealPathUtil.getRealPath(context, uri);
//            Bitmap bitmap = BitmapFactory.decodeFile(path);
//
//
//            ImageView pg_photo1 = findViewById(R.id.pg_photo1);
//            pg_photo1.setImageBitmap(bitmap);

        }
    }

    private void saveImage(Bitmap finalBitmap) throws IOException {
        Toast.makeText(Test_upload.this, "saveImage", Toast.LENGTH_LONG).show();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        File photoFile = new File(image.getAbsolutePath());

        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.drd.drdtrackingapp.FileProvider",
                    photoFile);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }

//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/saved_images");
//        myDir.mkdirs();
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String fname = "Shutta_"+ timeStamp +".jpg";
//
//        File file = new File(myDir, fname);
//        if (file.exists()) file.delete ();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//
//            Toast.makeText(Test_upload.this, "saveImage 1", Toast.LENGTH_LONG).show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            Toast.makeText(Test_upload.this, "saveImage 2", Toast.LENGTH_LONG).show();
//
//        }
    }

    private void uploadFile() {

        File photoFile = new File(getExternalFilesDir(null), "photo.jpg");

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), photoFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", photoFile.getName(), requestFile);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.uploadImage(imagePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Image uploaded successfully
                    // Handle the response, if any
                    try {
                        Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Handle the error
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network errors or exceptions
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}