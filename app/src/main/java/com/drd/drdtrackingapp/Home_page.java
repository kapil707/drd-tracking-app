package com.drd.drdtrackingapp;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home_page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomePageBinding binding;
    NavController navController;
    UserSessionManager session;
    String user_session = "", user_code = "", user_altercode = "", user_password = "", user_fname = "", user_image = "", firebase_token = "";
    ImageView nav_user_image;

    GPSTracker mGPS;
    double latitude1, longitude1;
    String getlatitude = "", getlongitude = "";
    String currentPhotoPath, selectedPath;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int REQUEST_CODE_GET_ACCOUNTS = 105;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 106;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int INSTALL_PACKAGES_REQUEST_CODE = 2;

    String useremail = "";
    String m_versionName = "";
    int m_versionCode = 0;

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


        // Check if the app has the GET_ACCOUNTS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission("android.permission.GET_ACCOUNTS") == PackageManager.PERMISSION_GRANTED) {
            // If the permission is granted, get the user's email
            useremail = getUserEmail(this);
        } else {
            // Request the GET_ACCOUNTS permission
            requestPermissions(new String[]{"android.permission.GET_ACCOUNTS"}, REQUEST_CODE_GET_ACCOUNTS);
        }

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
                askCameraPermissions();
                //update_app("hello","ram ram sa");
            }
        });
        //OpenNewVersion();
        //set_parmission();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_delivery_list) {
            //Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            //alertMessage_logoutUser();

            Intent in = new Intent();
            in.setClass(getBaseContext(), Delivery_order_list.class);
            in.putExtra("status","0");
            startActivity(in);
        } else {
            if (id == R.id.nav_delivery_done_list) {
                //Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                //alertMessage_logoutUser();

                Intent in = new Intent();
                in.setClass(getBaseContext(), Delivery_order_list.class);
                in.putExtra("status", "1");
                startActivity(in);
            } else {
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
    private void open_qr_scanner() {
        mGPS_info(); // locacation ati ha iss say scnner open kartay he

        IntentIntegrator intentIntegrator = new IntentIntegrator(Home_page.this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        //intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    public void attendance_upload(int requestCode, int resultCode, Intent data) {
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

                        String return_id = jsonObject.getString("return_id");
                        String token_key = jsonObject.getString("token_key");
                        String date = jsonObject.getString("date");
                        String time = jsonObject.getString("time");

                        mGPS_info(); // locacation ati ha iss say scnner open kartay he

                        if (return_id.equals("1")) {
                            upload_attendance_api(getlatitude, getlongitude, date, time, token_key);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Qr Scanner error", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private String getUserEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");

        if (accounts.length > 0) {
            // Assuming the first account is the primary Google account on the device
            return accounts[0].name;
        } else {
            return null; // No Google account found
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

    private void upload_attendance_api(String latitude, String longitude, String date, String time, String token_key) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.upload_attendance_api("98c08565401579448aad7c64033dcb4081906dcb", user_code, user_altercode, latitude, longitude, date, time, token_key);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle success response
                    // response.body() contains the response data
                    try {
                        JSONArray jArray = new JSONArray(response.body().string());
                        for (int i = 0; i < jArray.length(); i++) {

                            JSONObject jsonObject = jArray.getJSONObject(i);
                            String return_id = jsonObject.getString("return_id");
                            String return_message = jsonObject.getString("return_message");

//                            if(return_id.equals("1")){
//                                finish();
//                            }
                            Toast.makeText(getApplicationContext(), return_message.toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.e("Bg-service", "Error parsing data" + e.toString());
                        Toast.makeText(getApplicationContext(), "upload_attendance_api error2", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle error response
                    Toast.makeText(getApplicationContext(), "upload_attendance_api error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failures or other errors
                Log.e("Bg-service", " " + t.toString());
                Toast.makeText(getApplicationContext(), "upload_attendance_api onFailure", Toast.LENGTH_LONG).show();
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

//        startService(new Intent(this, BackgroundLocationUpdateService.class));
        home_page_api();
    }

    void home_page_api() {
        if (firebase_token.length() == 0) {
            Log.d("Bg-service", "firebase_token error");
        } else {

            java.util.Date noteTS = Calendar.getInstance().getTime();

            String time = "hh:mm"; // 12:00
            //tvTime.setText(DateFormat.format(time, noteTS));

            String date = "yyyy-MM-dd"; // 01 January 2013
            //tvDate.setText(DateFormat.format(date, noteTS));

            //String gettime = DateFormat.format(time, noteTS) + "";
            //String getdate = DateFormat.format(date, noteTS) + "";

            mGPS_info(); // locacation ati ha iss say scnner open kartay he

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Call<ResponseBody> call = apiService.home_page_api("98c08565401579448aad7c64033dcb4081906dcb",
                    user_code, user_altercode, firebase_token, getlatitude, getlongitude,
                    String.valueOf(m_versionCode), m_versionName,useremail);
            //Call<ResponseBody> call = apiService.testing("loginRequest");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONArray jArray = new JSONArray(response.body().string());
                            for (int i = 0; i < jArray.length(); i++) {

                                JSONObject jsonObject = jArray.getJSONObject(i);
                                String return_id = jsonObject.getString("return_id");
                                String return_title = jsonObject.getString("return_title");
                                String return_message = jsonObject.getString("return_message");
                                String return_url = jsonObject.getString("return_url");
                                String return_logout = jsonObject.getString("return_logout");

                                if(return_logout.equals("1")){
                                    session.logoutUser();
                                    finish();
                                }

                                if (return_title.equals("")) {
                                    //finish();
                                    //session.createUserLoginSession(user_session,user_code,user_altercode,user_password,user_fname,user_image1,firebase_token);
                                } else {
                                    update_app(return_title, return_message, return_url);
                                }
                                //Toast.makeText(Home_page.this, return_message.toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            Log.e("Bg-service", "Error parsing data" + e.toString());
                            Toast.makeText(Home_page.this, "Error " + e.toString(), Toast.LENGTH_LONG).show();
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
    }


    /*********image upload*********************/
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getBaseContext(),String.valueOf(requestCode), Toast.LENGTH_LONG).show();

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                nav_user_image.setImageURI(Uri.fromFile(f));
                upload_profile_image_api();
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        } else {
            attendance_upload(requestCode, resultCode, data);
        }
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_GET_ACCOUNTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the user's email
                useremail = getUserEmail(this);
                // Use the userEmail as needed
            } else {
                // Permission denied, handle accordingly
            }
        }

        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with your app logic
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // in the below line, we are checking if permission is granted.
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if permissions are granted we are displaying below toast message.
                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                // in the below line, we are displaying toast message
                // if permissions are not granted.
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.drd.drdtrackingapp.FileProvider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void upload_profile_image_api() {
        try {

            selectedPath = compressImage(currentPhotoPath);
            File imageFile = new File(selectedPath);

            RequestBody requestFile =
                    RequestBody.create(MultipartBody.FORM, imageFile);
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part image1 = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
            // add another part within the multipart request
            RequestBody api_key1 = RequestBody.create(MultipartBody.FORM, "98c08565401579448aad7c64033dcb4081906dcb");
            RequestBody user_code1 = RequestBody.create(MultipartBody.FORM, user_code);
            RequestBody user_altercode1 = RequestBody.create(MultipartBody.FORM, user_altercode);

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Call<ResponseBody> call = apiService.upload_profile_image_api(
                    api_key1,
                    user_code1,
                    user_altercode1,
                    image1);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Image uploaded successfully
                        // Handle the response, if any
                        try {
                            JSONArray jArray = new JSONArray(response.body().string());
                            for (int i = 0; i < jArray.length(); i++) {

                                JSONObject jsonObject = jArray.getJSONObject(i);
                                String return_id = jsonObject.getString("return_id");
                                String return_message = jsonObject.getString("return_message");
                                String user_image1 = jsonObject.getString("user_image");

                                if (return_id.equals("1")) {
                                    //finish();
                                    session.createUserLoginSession(user_session, user_code, user_altercode, user_password, user_fname, user_image1, firebase_token);
                                }
                                Toast.makeText(Home_page.this, return_message.toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            Log.e("Bg-service", "Error parsing data" + e.toString());
                            Toast.makeText(Home_page.this, "Error " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                        delete_image(currentPhotoPath);
                        delete_image(selectedPath);
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
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void delete_image(String val) {
        File fdelete = new File(val);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                //Toast.makeText(this, "Delete Photo", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Not delete Photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String compressImage(String imageUri) {
        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        //     by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //     you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        //     max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //     width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //     setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //     inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //     this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            //         load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            //exception.printStackTrace();
            Toast.makeText(getApplicationContext(), "compressImage1", Toast.LENGTH_LONG).show();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            //exception.printStackTrace();
            Toast.makeText(getApplicationContext(), "compressImage2", Toast.LENGTH_LONG).show();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        //     check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            //e.printStackTrace();
            Toast.makeText(getApplicationContext(), "compressImage3", Toast.LENGTH_LONG).show();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            Toast.makeText(getApplicationContext(), "compressImage4", Toast.LENGTH_LONG).show();
        }
        return filename;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Pictures/");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".png");
        return uriSting;
    }

    public void update_app(String broadcast_title, String broadcast, String myurl) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Html.fromHtml(broadcast_title));
        builder.setMessage(Html.fromHtml(broadcast));
        builder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
//                Intent it = new Intent(android.content.Intent.ACTION_VIEW);
//                it.setData(Uri.parse(myurl));
//                startActivity(it);
                //finish();
                String apkUrl = myurl;
                String title = "Your App Update";
                String description = "Downloading the latest version of Your App";

                ApkDownloader apkDownloader = new ApkDownloader(Home_page.this);
                apkDownloader.downloadAndInstallApk(apkUrl, title, description);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void set_parmission() {

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "YourApp.apk")), "application/vnd.android.package-archive");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                // Permission already granted
                // Continue with your app logic
            }
        } else {
            // Permission not needed for lower versions
            // Continue with your app logic
        }
    }

    void OpenNewVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!getPackageManager().canRequestPackageInstalls()) {
                    // Request the user to grant INSTALL_PACKAGES permission
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, INSTALL_PACKAGES_REQUEST_CODE);
                }
            }
        }

        try {
            Context context = this;
            String PATH = Environment.getExternalStorageDirectory() + "/Download/";
            String fileName = "YourApp.apk";

            File apkFile = new File(PATH, fileName);

            if (apkFile.exists()) {
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", apkFile);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    // Handle the exception, e.g., show a message to the user
                }
            } else {
                // Handle the case where the APK file does not exist
                Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}