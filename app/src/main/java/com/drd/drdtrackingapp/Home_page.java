package com.drd.drdtrackingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.drd.drdtrackingapp.databinding.ActivityHomePageBinding;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home_page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomePageBinding binding;
    NavController navController;
    UserSessionManager session;
    String user_session="",user_code="",user_altercode="",user_password="",user_fname="",user_image="",firebase_token="";
    ImageView nav_user_image;

    GPSTracker mGPS;
    double latitude1, longitude1;
    String getlatitude = "", getlongitude = "";

    String upload_profile_image_api = "";
    Bitmap bitmap;
    boolean check = true;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHomePage.toolbar);

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
        }
        binding.appBarHomePage.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                open_qr_scanner();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_delivery_list, R.id.nav_delivery_done_list, R.id.login_btn)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setNavigationViewListener();

        //NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);
        // navigationView.setNavigationItemSelectedListener(this);

        /*********************************************************/
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_session = user.get(UserSessionManager.KEY_USERID);
        user_code = user.get(UserSessionManager.KEY_USERCODE);
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);
        user_fname = user.get(UserSessionManager.KEY_USERNAME);
        user_image = user.get(UserSessionManager.KEY_USERIMAGE);
        firebase_token = user.get(UserSessionManager.KEY_FIREBASE_TOKEN);
        /*********************************************************/

        View header = navigationView.getHeaderView(0);
        TextView nav_user_name = header.findViewById(R.id.nav_user_name);
        nav_user_name.setText(user_fname);

        TextView nav_user_email = header.findViewById(R.id.nav_user_email);
        nav_user_email.setText("Code : " + user_altercode);

        nav_user_image = header.findViewById(R.id.nav_user_image);
        try {
            Picasso.get().load(user_image).into(nav_user_image);
        } catch (Exception e) {
            // TODO: handle exception
            //mProgressDialog.dismiss();
            Toast.makeText(getBaseContext(), "error load image", Toast.LENGTH_SHORT).show();
        }

        TextView edit_profile_btn = header.findViewById(R.id.edit_profile_btn);
        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                //Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        MainActivity ma = new MainActivity();
        String mainurl = ma.main_url;
        upload_profile_image_api = mainurl + "upload_profile_image_api";
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout_btn) {
            //Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            alertMessage_logoutUser();
        } else {
            if (id == R.id.attendance_btn) {
                open_qr_scanner();
            } else {
                if (id == R.id.meter_btn) {
                    Intent in = new Intent();
                    in.setClass(getBaseContext(), Meter_photo.class);
                    //in.putExtra("","");
                    startActivity(in);
                } else {
                    // Make your navController object final above
                    // or call Navigation.findNavController() again here
                    NavigationUI.onNavDestinationSelected(item, navController);
                }
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void alertMessage_logoutUser() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        try {
                            session.logoutUser();
                            finish();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to logout?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    // iss say qr code ka json response ata ha
    private void open_qr_scanner(){
        mGPS_info(); // locacation ati ha iss say scnner open kartay he

        IntentIntegrator intentIntegrator = new IntentIntegrator(Home_page.this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        //intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    public void attendance_upload(int requestCode, int resultCode,Intent data){
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONArray jArray = new JSONArray(intentResult.getContents());
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject jsonObject = jArray.getJSONObject(i);

                        String token_id = jsonObject.getString("token_id");
                        String return_id = jsonObject.getString("return_id");
                        String date = jsonObject.getString("date");
                        String time = jsonObject.getString("time");

                        mGPS_info(); // locacation ati ha iss say scnner open kartay he

                        if(return_id=="1") {
                            upload_attendance_api(getlatitude, getlongitude, date, time, token_id);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Qr Scanner error", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void mGPS_info() {
        mGPS = new GPSTracker(this);
        mGPS.getLocation();

        latitude1 = mGPS.getLatitude();
        longitude1 = mGPS.getLongitude();

        getlatitude = String.valueOf(latitude1);
        getlongitude = String.valueOf(longitude1);
    }

    private void upload_attendance_api(String latitude,String longitude,String date,String time,String token_key){
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<ResponseBody> call = apiService.upload_attendance_api("98c08565401579448aad7c64033dcb4081906dcb", user_code,user_altercode,latitude,longitude,date,time,token_key);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle success response
                    // response.body() contains the response data

                    try {
                        Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_LONG).show();
                        //writeTv(response.body().string());
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
            }
        });
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home_page, menu);
//        return true;
//    }

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

            //String gettime = DateFormat.format(time, noteTS) + "";
            //String getdate = DateFormat.format(date, noteTS) + "";

            mGPS_info(); // locacation ati ha iss say scnner open kartay he

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

            Call<ResponseBody> call = apiService.update_firebase_token_api("98c08565401579448aad7c64033dcb4081906dcb", user_code,user_altercode,firebase_token,getlatitude,getlongitude);
            //Call<ResponseBody> call = apiService.testing("loginRequest");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Handle success response
                        // response.body() contains the response data

//                        try {
//                            writeTv(response.body().string());
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
                    } else {
                        // Handle error response
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle network failures or other errors
                    Log.e("Bg-service", " " + t.toString());
                }
            });
        }
    }


    /*********image upload*********************/
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
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getBaseContext(),String.valueOf(requestCode), Toast.LENGTH_LONG).show();
        if (requestCode == CAMERA_REQUEST && requestCode == 1888) {
            bitmap = (Bitmap) data.getExtras().get("data");
            upload_master_profile_image();
        }else{
            attendance_upload(requestCode, resultCode, data);
        }
    }

    public void upload_master_profile_image() {
        String ConvertImage = null;
        try {
            ByteArrayOutputStream byteArrayOutputStreamObject;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStreamObject);
            byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
            ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        } catch (Exception ee) {

        }
        final String finalConvertImage = ConvertImage;
        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(getBaseContext(), "Start Uploading", Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();

                HashMapParams.put("api_key", "98c08565401579448aad7c64033dcb4081906dcb");
                HashMapParams.put("image_path", finalConvertImage);
                HashMapParams.put("user_code", user_code);
                HashMapParams.put("user_altercode", user_altercode);

                String FinalData = imageProcessClass.ImageHttpRequest(upload_profile_image_api, HashMapParams);
                return FinalData;
            }
            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                //Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
                try {
                    int intid = 0;
                    JSONArray jArray = new JSONArray(response);
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject jsonObject = jArray.getJSONObject(i);
                        String return_id =  jsonObject.getString("return_id");
                        String return_message =  jsonObject.getString("return_message");
                        String user_image =  jsonObject.getString("user_image");

                        try {
                            Picasso.get().load(user_image).into(nav_user_image);
                        } catch (Exception e) {
                            // TODO: handle exception
                            //mProgressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "error load image", Toast.LENGTH_SHORT).show();
                        }

                        session.createUserLoginSession(user_session,user_code,user_altercode,user_password,user_fname,user_image,firebase_token);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("Bg-service", "Error parsing data" + e.toString());
                }
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass {
        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;
                url = new URL(requestURL);
                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);
                OutPutStream = httpURLConnectionObject.getOutputStream();
                bufferedWriterObject = new BufferedWriter(
                        new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();
                RC = httpURLConnectionObject.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReaderObject.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;
            stringBuilderObject = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilderObject.append("=");
                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilderObject.toString();
        }
    }
}