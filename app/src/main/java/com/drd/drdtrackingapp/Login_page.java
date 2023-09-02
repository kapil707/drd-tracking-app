package com.drd.drdtrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_page extends AppCompatActivity {
    int PERMISSION_ID = 84697;
    FusedLocationProviderClient mFusedLocationClient;
    ProgressBar progressBar2;

    Button login_btn,login_btn1;
    TextView alert;
    EditText user_name,password;
    String user_name1 = "",password1 = "",firebase_token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        /**************location parmisson************/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Login_page.this);
        getLastLocation();
        /*******************************************/

        progressBar2 = findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.GONE);

        login_btn = findViewById(R.id.login_btn);
        login_btn1 = findViewById(R.id.login_btn1);

        alert = findViewById(R.id.user_alert);
        user_name = findViewById(R.id.user_name);
        password = findViewById(R.id.user_password);

        final ImageView eyes = findViewById(R.id.eyes);
        final ImageView eyes1 = findViewById(R.id.eyes1);
        eyes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                eyes1.setVisibility(View.GONE);
                eyes.setVisibility(View.VISIBLE);
                password.setInputType(InputType.TYPE_CLASS_TEXT);
                password.setSelection(password.getText().length());
            }
        });

        eyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                eyes.setVisibility(View.GONE);
                eyes1.setVisibility(View.VISIBLE);
                password.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setSelection(password.getText().length());
            }
        });


        firebase_token = "xxx";
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    user_name1 = user_name.getText().toString();
                    password1 = password.getText().toString();
                    if (firebase_token.length() == 0) {
                        alert.setText(Html.fromHtml("<font color='red'>button click error 2</font>"));
                        Toast.makeText(Login_page.this, "button click error 2", Toast.LENGTH_SHORT).show();
                    } else {
                        if (user_name1.length() > 0) {
                            if (password1.length() > 0) {
                                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                @SuppressLint("MissingPermission")
                                NetworkInfo ni = cm.getActiveNetworkInfo();
                                if (ni != null) {
                                    try {
                                        login_funcation();
                                        login_btn1.setVisibility(View.VISIBLE);
                                        login_btn.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                } else {
                                    alert.setText(Html.fromHtml("<font color='red'>Check your internet connection</font>"));
                                    Toast.makeText(Login_page.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                alert.setText(Html.fromHtml("<font color='red'>Enter password</font>"));
                                Toast.makeText(Login_page.this, "Enter Password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            alert.setText(Html.fromHtml("<font color='red'>Enter username</font>"));
                            Toast.makeText(Login_page.this, "Enter Username", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    alert.setText(Html.fromHtml("<font color='red'>button click error</font>"));
                    Toast.makeText(Login_page.this, "button click error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login_funcation(){
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        /*RequestBody requestBody = new FormBody.Builder()
                .add("submit", "98c08565401579448aad7c64033dcb4081906dcb")
                .add("user_name", user_name1)
                .add("password", password1)
                .add("getfcmtoken", firebase_token)
                .build();*/

        Login_model modal = new Login_model();
        modal.setSubmit("98c08565401579448aad7c64033dcb4081906dcb");
        modal.setUser_name("608");
        modal.setPassword("123456");
        modal.setGetfcmtoken("123456");

        Call<Login_model> call2 = apiService.post_login(modal);
        call2.enqueue(new Callback<Login_model>() {
            @Override
            public void onResponse(Call<Login_model> call, Response<Login_model> response) {
                // Handle response for the second request
                if (response.isSuccessful()) {
                    Log.e("Bg-service", "done");
                    // Handle success response
                    // response.body() contains the response data

                    Login_model mm = response.body();
                    Log.e("Bg-service", " " + (mm.getUser_alert()));

                } else {
                    // Handle error response
                    Log.e("Bg-service", "error");
                }
            }

            @Override
            public void onFailure(Call<Login_model> call, Throwable t) {
                // Handle failure for the second request

                Log.e("Bg-service", "onFailure");
            }
        });
    }

    private void writeTv(String response){

        //https://demonuts.com/retrofit-android-get-json/
        Log.e("Bg-service", response.toString());
        try {
            JSONArray jArray = new JSONArray(response);
            /*for (int i = 0; i < jArray.length(); i++)
            {
                JSONObject jsonObject = jArray.getJSONObject(i);
                String user_session    = jsonObject.getString("user_session");

                Log.e("Bg-service", user_session);
            }*/
        }
        catch (Exception e) {
            // TODO: handle exception
            Log.e("Bg-service", "Error parsing data"+e.toString());
        }

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };
}