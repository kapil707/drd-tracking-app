package com.drd.drdtrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_page extends AppCompatActivity {
    int PERMISSION_ID = 84697;
    FusedLocationProviderClient mFusedLocationClient;
    ProgressBar progressBar2;

    Button login_btn, login_btn1;
    TextView alert;
    EditText user_name, password;
    String user_name1 = "", password1 = "", firebase_token = "";
    UserSessionManager session;
    String m_versionName = "";
    int m_versionCode = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO || nightModeFlags == Configuration.UI_MODE_NIGHT_UNDEFINED) {
                window.setStatusBarColor(getResources().getColor(R.color.header_bg_light));
            }
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                window.setStatusBarColor(getResources().getColor(R.color.header_bg_dark));
            }

            LinearLayout login_page_theme_bg = findViewById(R.id.login_page_theme_bg);
            login_page_theme_bg.setBackgroundResource(R.drawable.login_page_shap);

            LinearLayout textbox_shap1 = findViewById(R.id.textbox_bg1);
            textbox_shap1.setBackgroundResource(R.drawable.textbox_shap);

            LinearLayout textbox_shap2 = findViewById(R.id.textbox_bg2);
            textbox_shap2.setBackgroundResource(R.drawable.textbox_shap);

            LinearLayout page_hr_line = findViewById(R.id.page_hr_line);
            page_hr_line.setBackgroundResource(R.drawable.page_hr_line_shap);

            GradientDrawable drawable = (GradientDrawable) login_page_theme_bg.getBackground();
            GradientDrawable drawable2 = (GradientDrawable) textbox_shap1.getBackground();
            GradientDrawable drawable3 = (GradientDrawable) textbox_shap2.getBackground();
            GradientDrawable drawable4 = (GradientDrawable) page_hr_line.getBackground();
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                drawable.setColor(getResources().getColor(R.color.header_bg_dark));
                drawable2.setColor(getResources().getColor(R.color.textbox_bg_dark));
                drawable3.setColor(getResources().getColor(R.color.textbox_bg_dark));
                drawable4.setColor(getResources().getColor(R.color.page_hr_bg_dark));
            }
        }

        /**************location parmisson************/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Login_page.this);
        getLastLocation();
        /*******************************************/

        PackageManager packageManager = getPackageManager();

        try {
            // Get the package information
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

            // Retrieve the version information
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;

            m_versionCode = versionCode;
            m_versionName = versionName;

            //Toast.makeText(getBaseContext(), String.valueOf(versionName), Toast.LENGTH_SHORT).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        progressBar2 = findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.GONE);

        login_btn = findViewById(R.id.login_btn);
        login_btn1 = findViewById(R.id.login_btn1);

        alert = findViewById(R.id.user_alert);
        user_name = findViewById(R.id.user_name);
        password = findViewById(R.id.user_password);
        TextView version_code = findViewById(R.id.version_code);
        version_code.setText("Version : " + m_versionName);

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

        session = new UserSessionManager(getApplicationContext());
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    user_name1 = user_name.getText().toString();
                    password1 = password.getText().toString();
                    if (firebase_token.length() == 0) {
                        //firebase_token = "1";
                        getFCMToken();
                        alert.setText(Html.fromHtml("<font color='red'>Token Key Miss Please Try Again</font>"));
                        Toast.makeText(Login_page.this, "Token Key Miss Please Try Again", Toast.LENGTH_SHORT).show();
                    } else {
                        if (user_name1.length() > 0) {
                            if (password1.length() > 0) {
                                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                @SuppressLint("MissingPermission")
                                NetworkInfo ni = cm.getActiveNetworkInfo();
                                if (ni != null) {
                                    try {
                                        login_funcation(user_name1, password1, firebase_token);
                                        login_btn1.setVisibility(View.VISIBLE);
                                        progressBar2.setVisibility(View.VISIBLE);
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
        getFCMToken();
    }

    void getFCMToken() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.d("Bg-service", "FirebaseMessaging Token - " + token);
                firebase_token = token;
            }
        });
    }

    private void login_funcation(String _user_name, String _password, String _firebase_token) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.get_login_api("98c08565401579448aad7c64033dcb4081906dcb", _user_name, _password, _firebase_token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle success response
                    // response.body() contains the response data

                    try {
                        writeTv(response.body().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Handle error response
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failures or other errors
                Log.e("Bg-service", " " + t.toString());

                login_btn1.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);
                login_btn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void writeTv(String response) {
        //https://demonuts.com/retrofit-android-get-json/
        //Log.e("Bg-service", response.toString());
        try {
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jsonObject = jArray.getJSONObject(i);
                String return_id = jsonObject.getString("return_id");
                String return_message = jsonObject.getString("return_message");

                String user_session = jsonObject.getString("user_session");
                String user_fname = jsonObject.getString("user_fname");
                String user_code = jsonObject.getString("user_code");
                String user_altercode = jsonObject.getString("user_altercode");
                String user_password = jsonObject.getString("user_password");
                String user_image = jsonObject.getString("user_image");

                //Log.e("Bg-service", user_session);

                if (return_id.equals("1")) {
                    Toast.makeText(Login_page.this, return_message, Toast.LENGTH_SHORT).show();
                    alert.setText(return_message);

                    session.createUserLoginSession(user_session, user_code, user_altercode, user_password, user_fname, user_image, firebase_token);

                    Intent in = new Intent();
                    in.setClass(Login_page.this, Home_page.class);
                    startActivity(in);
                    finish();
                } else {
                    Toast.makeText(Login_page.this, return_message, Toast.LENGTH_SHORT).show();
                    alert.setText(return_message);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Bg-service", "Error parsing data" + e.toString());

            login_btn1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            login_btn.setVisibility(View.VISIBLE);
        }

        login_btn1.setVisibility(View.GONE);
        progressBar2.setVisibility(View.GONE);
        login_btn.setVisibility(View.VISIBLE);
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
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
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
    private void requestNewLocationData() {

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