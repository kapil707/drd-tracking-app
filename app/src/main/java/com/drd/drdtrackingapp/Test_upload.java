package com.drd.drdtrackingapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Test_upload extends AppCompatActivity {

    private static final int camra_code = 1;
    ActivityResultLauncher<Uri> tackpic;
    Uri imageuri;

    ImageView iv;
    Button btn,btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);

        imageuri = createuri();
        regpiclun();

        iv = findViewById(R.id.imageviews);
        btn = findViewById(R.id.captureButton);
        btn1 = findViewById(R.id.upload_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                checkcamrapermissionandopencamra();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                uploadFile();
            }
        });
    }

    private Uri createuri(){
        File imagefile = new File(getApplicationContext().getFilesDir(),"kamalg.jpg");
        return FileProvider.getUriForFile(
                getApplicationContext(),
                "com.drd.drdtrackingapp.FileProvider",
                imagefile
        );
    }


    private void regpiclun() {
        tackpic = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        try {
                            iv.setImageURI(null);
                            iv.setImageURI(imageuri);

                            Toast.makeText(Test_upload.this,imageuri.toString(), Toast.LENGTH_LONG).show();

                        } catch (Exception exception) {
                            exception.getStackTrace();
                        }
                    }
                }

        );
    }

    private void checkcamrapermissionandopencamra(){
        if(ActivityCompat.checkSelfPermission(Test_upload.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Test_upload.this,
                    new String[]{Manifest.permission.CAMERA},camra_code);
        }else{
            tackpic.launch(imageuri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==camra_code){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                tackpic.launch(imageuri);

            }else{
                Toast.makeText(this,"camra no prmission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadFile() {

        String URL = "content://com.drd.drdtrackingapp.FileProvider"+imageuri.getPath();

        Uri students = Uri.parse(URL);

        File photoFile = new File(getApplicationContext().getFilesDir(), "kamalg.jpg");

        //File photoFile = new File(String.valueOf(students));

        Toast.makeText(getApplicationContext(),  photoFile.toString(), Toast.LENGTH_LONG).show();


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