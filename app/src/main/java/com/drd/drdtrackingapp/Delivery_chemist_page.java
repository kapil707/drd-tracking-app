package com.drd.drdtrackingapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Delivery_chemist_page extends AppCompatActivity {
    ProgressBar menu_loading1;
    UserSessionManager session;
    String user_code = "", user_altercode = "";
    String chemist_id = "", gstvno = "";
    private ImageView photo1, photo2, photo3, photo4;
    public static final int CAMERA_PERM_CODE = 101;
    String currentPhotoPath1,currentPhotoPath2,currentPhotoPath3,currentPhotoPath4,
            selectedPath1,selectedPath2,selectedPat3,selectedPath4;
    Bitmap bitmap1, bitmap2, bitmap3, bitmap4;
    Button photo_btn1, photo_btn2, photo_btn3, photo_btn4;
    TextView tv_error1, tv_error2, tv_error3, tv_error4, enter_remarks_error,enter_remarks_tv;
    Button buttonUpload, buttonUpload1;
    EditText enter_remarks;

    //    GridView gridview;
//    Delivery_chemist_photo_Adapter adapter;
//    List<Dilivery_chemist_photo_get_or_set> get_set = new ArrayList<Dilivery_chemist_photo_get_or_set>();
    GPSTracker mGPS;
    double latitude1, longitude1;
    String getlatitude = "", getlongitude = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_chemist_photo);

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

            LinearLayout textbox_bg1 = findViewById(R.id.textbox_bg1);
            textbox_bg1.setBackgroundResource(R.drawable.textbox_shap);

            GradientDrawable drawable2 = (GradientDrawable) textbox_bg1.getBackground();
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                drawable2.setColor(getResources().getColor(R.color.textbox_bg_dark));
            }
        }

        Intent in = getIntent();
        chemist_id = in.getStringExtra("chemist_id");
        gstvno = in.getStringExtra("gstvno");
        String edit_yes_no = in.getStringExtra("edit_yes_no");

        TextView action_bar_title1 = (TextView) findViewById(R.id.action_bar_title);
        action_bar_title1.setText("Update image (" + chemist_id + ")");
        TextView action_bar_title11 = (TextView) findViewById(R.id.action_bar_title1);
        action_bar_title11.setText(gstvno);
        action_bar_title11.setVisibility(View.VISIBLE);
        ImageButton imageButton = findViewById(R.id.action_bar_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        menu_loading1 = (ProgressBar) findViewById(R.id.menu_loading1);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_code = user.get(UserSessionManager.KEY_USERCODE);
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);

        photo1 = findViewById(R.id.pg_photo1);
        tv_error1 = findViewById(R.id.pg_photo_error1);
        photo_btn1 = findViewById(R.id.pg_photo_btn1);

        photo2 = findViewById(R.id.pg_photo2);
        tv_error2 = findViewById(R.id.pg_photo_error2);
        photo_btn2 = findViewById(R.id.pg_photo_btn2);

        photo3 = findViewById(R.id.pg_photo3);
        tv_error3 = findViewById(R.id.pg_photo_error3);
        photo_btn3 = findViewById(R.id.pg_photo_btn3);

        photo4 = findViewById(R.id.pg_photo4);
        tv_error4 = findViewById(R.id.pg_photo_error4);
        photo_btn4 = findViewById(R.id.pg_photo_btn4);

        enter_remarks = findViewById(R.id.enter_remarks);
        enter_remarks_error = findViewById(R.id.enter_remarks_error);
        enter_remarks_tv = findViewById(R.id.enter_remarks_tv);

        tv_error1.setVisibility(View.GONE);
        tv_error2.setVisibility(View.GONE);
        tv_error3.setVisibility(View.GONE);
        tv_error4.setVisibility(View.GONE);
        enter_remarks_error.setVisibility(View.GONE);

        buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload1 = findViewById(R.id.buttonUpload1);


        edit_or_not(edit_yes_no);
        photo_btn1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                askCameraPermissions1();
            }
        });

        photo_btn2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                askCameraPermissions2();
            }
        });

        photo_btn3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                askCameraPermissions3();
            }
        });

        photo_btn4.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                askCameraPermissions4();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                alertMessage_complete_order();
            }
        });
    }

    private void askCameraPermissions1() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent("1",1991);
        }
    }

    private void askCameraPermissions2() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent("2",1992);
        }
    }

    private void askCameraPermissions3() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent("3",1993);
        }
    }

    private void askCameraPermissions4() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent("4",1994);
        }
    }

    private void dispatchTakePictureIntent(String id,int camcode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(id);
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.drd.drdtrackingapp.FileProvider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, camcode);
            }
        }
    }

    private File createImageFile(String id) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + id + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        if(id.equals("1")) {
            currentPhotoPath1 = image.getAbsolutePath();
        }
        if(id.equals("2")) {
            currentPhotoPath2 = image.getAbsolutePath();
        }
        if(id.equals("3")) {
            currentPhotoPath3 = image.getAbsolutePath();
        }
        if(id.equals("4")) {
            currentPhotoPath4 = image.getAbsolutePath();
        }
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1991) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath1);
                photo1.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }

        if (requestCode == 1992) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath2);
                photo2.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }

        if (requestCode == 1993) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath3);
                photo3.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }

        if (requestCode == 1994) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath4);
                photo4.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }
    }

    public void edit_or_not(String ii){
        if(ii.equals("no")){
            tv_error1.setVisibility(View.GONE);
            tv_error2.setVisibility(View.GONE);
            tv_error3.setVisibility(View.GONE);
            tv_error4.setVisibility(View.GONE);

            photo_btn1.setVisibility(View.GONE);
            photo_btn2.setVisibility(View.GONE);
            photo_btn3.setVisibility(View.GONE);
            photo_btn4.setVisibility(View.GONE);

            enter_remarks.setVisibility(View.GONE);
            enter_remarks_error.setVisibility(View.GONE);

            LinearLayout textbox_bg1 = findViewById(R.id.textbox_bg1);
            textbox_bg1.setVisibility(View.GONE);

            buttonUpload.setVisibility(View.GONE);
            buttonUpload1.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        get_delivery_order_photo_api();
    }

    public void alertMessage_complete_order() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        try {
                            //upload_delivery_order_photo_api();
                        } catch (Exception e) {
                            // TODO: handle exception
                            Toast.makeText(Delivery_chemist_page.this, "alertMessage_complete_order error", Toast.LENGTH_SHORT).show();
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
        builder.setMessage("Are you sure to completed order?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void mGPS_info() {
        mGPS = new GPSTracker(this);
        mGPS.getLocation();

        latitude1 = mGPS.getLatitude();
        longitude1 = mGPS.getLongitude();

        getlatitude = String.valueOf(latitude1);
        getlongitude = String.valueOf(longitude1);
    }



    private void get_delivery_order_photo_api() {
        menu_loading1.setVisibility(View.VISIBLE);
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.get_delivery_order_photo_api("98c08565401579448aad7c64033dcb4081906dcb", user_code, user_altercode, chemist_id, gstvno);
        //Call<ResponseBody> call = apiService.testing("loginRequest");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle success response
                    // response.body() contains the response data
                    try {
                        menu_loading1.setVisibility(View.GONE);
                        JSONArray jArray = new JSONArray(response.body().string());
                        for (int i = 0; i < jArray.length(); i++) {

                            JSONObject jsonObject = jArray.getJSONObject(i);
                            String image1 =  jsonObject.getString("image1");
                            String image2 =  jsonObject.getString("image2");
                            String image3 =  jsonObject.getString("image3");
                            String image4 =  jsonObject.getString("image4");
                            String message =  jsonObject.getString("message");

                            Picasso.get().load(image1).into(photo1);
                            Picasso.get().load(image2).into(photo2);
                            Picasso.get().load(image3).into(photo3);
                            Picasso.get().load(image4).into(photo4);

                            enter_remarks_tv.setText(message);

                            edit_or_not("no");
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.e("Bg-service", "Error parsing data" + e.toString());
                        Toast.makeText(getApplicationContext(),"get_delivery_order_photo_api error2", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle error response
                    menu_loading1.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failures or other errors
                menu_loading1.setVisibility(View.GONE);
                Log.e("Bg-service-onFailure", " " + t.toString());
                Toast.makeText(Delivery_chemist_page.this, "show_rider_chemist_photo_api onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}