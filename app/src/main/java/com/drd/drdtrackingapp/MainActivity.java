package com.drd.drdtrackingapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public String main_url = "https://www.drdweb.co.in/drd_master_api/api01/";
    UserSessionManager session;
    Database db;
    SQLiteDatabase sql;
    Button btn1,btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new UserSessionManager(getApplicationContext());
        if (session.checkLogin()) {
            finish();
        }else{
            Intent in = new Intent();
            in.setClass(this, Home_page.class);
            startActivity(in);
            finish();
        }

        db = new Database(this);
        sql = db.getWritableDatabase();

        try {
            Cursor tbl_order_done = sql.rawQuery("Select * from tbl_user_loc", null);
            //Toast.makeText(MainActivity.this, "Total rec of : " + tbl_order_done.getCount(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            db.onUpgrade(sql, 1, 2);
            //Toast.makeText(MainActivity.this, "Update database", Toast.LENGTH_LONG).show();
        }

        /*AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 10;
        long ter = System.currentTimeMillis()+(time*1000);

        Intent it = new Intent(MainActivity.this, Myrec.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this,100,it,PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP,ter,pi);*/

        /*Log.e("main", "start: ");

        btn1 = findViewById(R.id.btnStart);
        btn2 = findViewById(R.id.btnStop);

        /*Intent s = new Intent(MainActivity.this, Myforgoundservices.class);
        startForegroundService(s);*/

        /*ComponentName componentName = new ComponentName(MainActivity.this,Examplejobservice.class);
        JobInfo info = new JobInfo.Builder(123,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(1*60*1000)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);

        Intent MusicService = new Intent(MainActivity.this, MusicService.class);
        startService(MusicService);*/

       /* Intent serviceIntent = new Intent(MainActivity.this, MyBackgroundService.class);
        MainActivity.this.startService(serviceIntent);*/

        /*Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        startService(serviceIntent);*/
        //ThreadExample

        //getFCMToken();

        /*Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        startService(serviceIntent);*/

        /*startService(new Intent(this, BackgroundLocationUpdateService.class));

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startService(new Intent(MainActivity.this,MusicService.class));

//                Intent s = new Intent(MainActivity.this, Myforgoundservices.class);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(s);
//                }
                /*ComponentName componentName = new ComponentName(MainActivity.this,Examplejobservice.class);
                JobInfo info = new JobInfo.Builder(123,componentName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                        .setPersisted(true)
                        .setPeriodic(15*60*1000)
                        .build();
                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.schedule(info);*/

                /*AlarmReceiver alarm = new AlarmReceiver();
                alarm.setAlarm(MainActivity.this);*/

                /*Log.e("BackgroundSyncWorker", "start1");
                ignoreBatteryOptimization();
                Log.e("BackgroundSyncWorker", "start2");*/

                //https://www.digitalocean.com/community/tutorials/android-broadcastreceiver-example-tutorial

                /*ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                Call<ResponseBody> call = apiService.update_api("chemist","v153","123");

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            // Handle success response
                            // response.body() contains the response data
                            try {
                                Log.e("MainActivity", " "+ (response.body().string()) );
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
                    }
                });*/
            /*}
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopService(new Intent(MainActivity.this,MusicService.class));
            }
        });*/
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void ignoreBatteryOptimization() {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }
    AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneModeChangeReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneModeChangeReceiver);
    }
}