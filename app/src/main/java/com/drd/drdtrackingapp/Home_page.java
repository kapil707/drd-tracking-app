package com.drd.drdtrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.drd.drdtrackingapp.databinding.ActivityHomePageBinding;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home_page extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomePageBinding binding;
    UserSessionManager session;
    String user_code = "",user_altercode="",firebase_token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHomePage.toolbar);
        binding.appBarHomePage.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_delivery_list, R.id.nav_delivery_done_list)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);
        // navigationView.setNavigationItemSelectedListener(this);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_code = user.get(UserSessionManager.KEY_USERCODE);
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);
        firebase_token = user.get(UserSessionManager.KEY_FIREBASE_TOKEN);
        String user_name = user.get(UserSessionManager.KEY_USERNAME);

        View header = navigationView.getHeaderView(0);
        TextView nav_user_name = header.findViewById(R.id.nav_user_name);
        nav_user_name.setText(user_name);

        TextView nav_user_email = header.findViewById(R.id.nav_user_email);
        nav_user_email.setText("Code : " + user_altercode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startService(new Intent(this, BackgroundLocationUpdateService.class));
        insert_firebase_token();
    }
    void insert_firebase_token(){
        if (firebase_token.length() == 0) {
            Log.d("Bg-service", "firebase_token error");
        }else {

            java.util.Date noteTS = Calendar.getInstance().getTime();

            String time = "hh:mm"; // 12:00
            //tvTime.setText(DateFormat.format(time, noteTS));

            String date = "yyyy-MM-dd"; // 01 January 2013
            //tvDate.setText(DateFormat.format(date, noteTS));

            String gettime = DateFormat.format(time, noteTS) + "";
            String getdate = DateFormat.format(date, noteTS) + "";

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            RequestBody requestBody = new FormBody.Builder()
                    .add("user_code", user_code)
                    .add("firebase_token", firebase_token)
                    .add("getdate", getdate)
                    .add("gettime", gettime)
                    .build();
            Call<ResponseBody> call2 = apiService.insert_firebase_token(requestBody);
            call2.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // Handle response for the second request
                    if (response.isSuccessful()) {
                        Log.e("Bg-service", "done");
                        // Handle success response
                        // response.body() contains the response data
                        try {
                            Log.e("Bg-service", " " + (response.body().string()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // Handle error response
                        Log.e("Bg-service", "error");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle failure for the second request

                    Log.e("Bg-service", "onFailure");
                }
            });
        }
    }
}