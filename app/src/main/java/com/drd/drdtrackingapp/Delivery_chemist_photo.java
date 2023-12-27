package com.drd.drdtrackingapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Delivery_chemist_photo extends AppCompatActivity {
    ProgressBar menu_loading1;
    ImageButton menu_camera_btn;
    UserSessionManager session;
    String user_code = "", user_altercode = "";
    String chemist_code = "", gstvno = "",mytagno="";
    private ImageView photo0,photo1, photo2, photo3, photo4;
    public static final int CAMERA_PERM_CODE = 101;
    String currentPhotoPath0="",currentPhotoPath1="",currentPhotoPath2="",currentPhotoPath3="",currentPhotoPath4="",
            selectedPath0="",selectedPath1="",selectedPath2="",selectedPath3="",selectedPath4="";
    Button photo_btn1, photo_btn2, photo_btn3, photo_btn4;
    TextView tv_error1, tv_error2, tv_error3, tv_error4, enter_remarks_error,enter_remarks_error2;
    Button buttonUpload, buttonUpload1;
    EditText enter_message,enter_message2;
    Spinner spinner;
    ArrayAdapter<String> dataAdapter;
    String _id = "0";
    String image_path1="",image_path2="",image_path3="",image_path4="";
    GPSTracker mGPS;
    double latitude1, longitude1;
    String getlatitude = "", getlongitude = "";

    GridView listview1;
    Delivery_chemist_photo_Adapter adapter;
    List<Delivery_chemist_photo_get_or_set> arrayList = new ArrayList<Delivery_chemist_photo_get_or_set>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_chemist_photo);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_code = user.get(UserSessionManager.KEY_USERCODE);
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);

        Intent in = getIntent();
        chemist_code = in.getStringExtra("chemist_code");
        gstvno = in.getStringExtra("gstvno");
        mytagno = in.getStringExtra("mytagno");
        String edit_yes_no = in.getStringExtra("edit_yes_no");

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
            LinearLayout textbox_bg2 = findViewById(R.id.textbox_bg2);
            textbox_bg1.setBackgroundResource(R.drawable.textbox_shap);
            textbox_bg2.setBackgroundResource(R.drawable.textbox_shap);

            GradientDrawable drawable1 = (GradientDrawable) textbox_bg1.getBackground();
            GradientDrawable drawable2 = (GradientDrawable) textbox_bg2.getBackground();
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                drawable1.setColor(getResources().getColor(R.color.textbox_bg_dark));
                drawable2.setColor(getResources().getColor(R.color.textbox_bg_dark));
            }
        }

        TextView action_bar_title1 = (TextView) findViewById(R.id.action_bar_title);
        action_bar_title1.setText("Update image (" + chemist_code + ")");
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

        menu_loading1 = findViewById(R.id.menu_loading1);
        menu_camera_btn = findViewById(R.id.menu_camera_btn);

        menu_camera_btn.setVisibility(View.VISIBLE);
        photo0 = findViewById(R.id.pg_photo0);

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

        enter_message = findViewById(R.id.enter_message);
        enter_message2 = findViewById(R.id.enter_message2);
        enter_remarks_error = findViewById(R.id.enter_remarks_error);
        enter_remarks_error2 = findViewById(R.id.enter_remarks_error2);

        spinner = findViewById(R.id.spinner);

        tv_error1.setVisibility(View.GONE);
        tv_error2.setVisibility(View.GONE);
        tv_error3.setVisibility(View.GONE);
        tv_error4.setVisibility(View.GONE);
        enter_remarks_error.setVisibility(View.GONE);
        enter_remarks_error2.setVisibility(View.GONE);

        buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload1 = findViewById(R.id.buttonUpload1);

        listview1 = findViewById(R.id.listview_more);
        adapter = new Delivery_chemist_photo_Adapter(Delivery_chemist_photo.this, arrayList);
        listview1.setAdapter(adapter);
        listview1.setVisibility(View.GONE);
        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                // TODO Auto-generated method stub

                Delivery_chemist_photo_get_or_set clickedCategory = arrayList.get(arg2);
                String myid = clickedCategory.id();

                //Toast.makeText(Delivery_chemist_photo.this, myid, Toast.LENGTH_LONG).show();

                alertMessage_delete_delivery_order_photo_more(myid);
                return true;
            }
        });

        edit_or_not(edit_yes_no);

        menu_camera_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                askCameraPermissions0();
            }
        });

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
            @Override
            public void onClick(View view) {

                int work = 0;
                String message = enter_message.getText().toString();
                if (message.length() > 0) {
                    enter_remarks_error.setVisibility(View.GONE);
                } else {
                    work++;
                    enter_remarks_error.setVisibility(View.VISIBLE);
                    //Toast.makeText(Delivery_chemist_page.this, "Enter Message", Toast.LENGTH_LONG).show();
                }

                String message2 = enter_message2.getText().toString();
                if (message2.length() > 0) {
                    enter_remarks_error2.setVisibility(View.GONE);
                } else {
                    work++;
                    enter_remarks_error2.setVisibility(View.VISIBLE);
                    //Toast.makeText(Delivery_chemist_page.this, "Enter Message", Toast.LENGTH_LONG).show();
                }

                if (image_path1.equals("")) {
                    if (currentPhotoPath1.isEmpty()) {
                        work++;
                        tv_error1.setVisibility(View.VISIBLE);
                        //Toast.makeText(Delivery_chemist_page.this, "Select Material photo1", Toast.LENGTH_LONG).show();
                    } else {
                        tv_error1.setVisibility(View.GONE);
                    }
                }

                if (image_path2.equals("")) {
                    if (currentPhotoPath2.isEmpty()) {
                        work++;
                        tv_error2.setVisibility(View.VISIBLE);
                        //Toast.makeText(Delivery_chemist_page.this, "Select Material photo1", Toast.LENGTH_LONG).show();
                    } else {
                        tv_error2.setVisibility(View.GONE);
                    }
                }

                if (image_path3.equals("")) {
                    if (currentPhotoPath3.isEmpty()) {
                        work++;
                        tv_error3.setVisibility(View.VISIBLE);
                        //Toast.makeText(Delivery_chemist_page.this, "Select Material photo1", Toast.LENGTH_LONG).show();
                    } else {
                        tv_error3.setVisibility(View.GONE);
                    }
                }

                if (image_path4.equals("")) {
                    if (currentPhotoPath4.isEmpty()) {
                        work++;
                        tv_error4.setVisibility(View.VISIBLE);
                        //Toast.makeText(Delivery_chemist_page.this, "Select Material photo1", Toast.LENGTH_LONG).show();
                    } else {
                        tv_error4.setVisibility(View.GONE);
                    }
                }

                if (work == 0) {
                    alertMessage_complete_order();
                }
            }
        });

        // Spinner element
        //spinner = findViewById(R.id.spinner);

        // Spinner click listener
        //spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Cash");
        categories.add("Cheque");
        categories.add("Online");

        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        // page load
        get_delivery_order_photo_api();
    }

    private void askCameraPermissions0() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent("0",1990);
        }
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
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        if(id.equals("0")) {
            currentPhotoPath0 = image.getAbsolutePath();
        }
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

        if (requestCode == 1990) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath0);
                photo0.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                upload_delivery_order_photo_more();
            }
        }

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
        if(ii.equals("no")) {
            menu_camera_btn.setVisibility(View.GONE);
            tv_error1.setVisibility(View.GONE);
            tv_error2.setVisibility(View.GONE);
            tv_error3.setVisibility(View.GONE);
            tv_error4.setVisibility(View.GONE);

            photo_btn1.setVisibility(View.GONE);
            photo_btn2.setVisibility(View.GONE);
            photo_btn3.setVisibility(View.GONE);
            photo_btn4.setVisibility(View.GONE);

            enter_message.setVisibility(View.GONE);
            enter_remarks_error.setVisibility(View.GONE);

            enter_message2.setVisibility(View.GONE);
            enter_remarks_error2.setVisibility(View.GONE);

            spinner.setVisibility(View.GONE);

            LinearLayout textbox_bg1 = findViewById(R.id.textbox_bg1);
            textbox_bg1.setVisibility(View.GONE);

            LinearLayout textbox_bg2 = findViewById(R.id.textbox_bg2);
            textbox_bg2.setVisibility(View.GONE);

            buttonUpload.setVisibility(View.GONE);
            buttonUpload1.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //get_delivery_order_photo_api();
    }

    public void alertMessage_complete_order() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        try {
                            mGPS_info();
                            upload_delivery_order_photo();
                        } catch (Exception e) {
                            // TODO: handle exception
                            Toast.makeText(Delivery_chemist_photo.this, "alertMessage_complete_order error", Toast.LENGTH_SHORT).show();
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
        Call<ResponseBody> call = apiService.get_delivery_order_photo_api(
                "98c08565401579448aad7c64033dcb4081906dcb",
                user_code,
                user_altercode,
                chemist_code,
                gstvno);
        //Call<ResponseBody> call = apiService.testing("loginRequest");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle success response
                    // response.body() contains the response data
                    try {
                        menu_loading1.setVisibility(View.GONE);
                        //JSONArray jArray = new JSONArray(response.body().string());

                        JSONArray jArray0 = new JSONArray(response.body().string());
                        JSONObject jsonObject0 = jArray0.getJSONObject(0);
                        String items = jsonObject0.getString("items");
                        String items_others = jsonObject0.getString("items_others");

                        //Toast.makeText(getApplicationContext(),items_others.toString(), Toast.LENGTH_LONG).show();

                        JSONArray jArray_items = new JSONArray(items);
                        for (int i = 0; i < jArray_items.length(); i++) {

                            JSONObject jsonObject = jArray_items.getJSONObject(i);
                            _id =  jsonObject.getString("id");

                            String at_a = jsonObject.getString("at_a");
                            String nrx = jsonObject.getString("nrx");

                            if(!_id.equals("0")) {
                                image_path1 = jsonObject.getString("image1");
                                image_path2 = jsonObject.getString("image2");
                                image_path3 = jsonObject.getString("image3");
                                image_path4 = jsonObject.getString("image4");
                                String message = jsonObject.getString("message");
                                String payment_type = jsonObject.getString("payment_type");
                                String payment_message = jsonObject.getString("payment_message");
                                String is_edit = jsonObject.getString("is_edit");

                                Picasso.get().load(image_path1).into(photo1);
                                Picasso.get().load(image_path2).into(photo2);
                                Picasso.get().load(image_path3).into(photo3);
                                Picasso.get().load(image_path4).into(photo4);

                                enter_message.setText(message);
                                enter_message2.setText(payment_message);

                                spinner.setSelection(dataAdapter.getPosition(payment_type));

                                if(is_edit.equals("1")){
                                    edit_or_not("no");
                                    TextView payment_tv = findViewById(R.id.payment_tv);
                                    payment_tv.setText(payment_type + " / "+payment_message);
                                    TextView message_tv = findViewById(R.id.message_tv);
                                    message_tv.setText(message);
                                }
                            }

                            if(_id.equals("0")){

                            }else{
                                buttonUpload.setText("Update");
                                buttonUpload1.setText("Update");

                                buttonUpload.setVisibility(View.VISIBLE);
                                buttonUpload1.setVisibility(View.GONE);
                            }

                            if(at_a.equals("0")){
                                LinearLayout lin_at_a = findViewById(R.id.lin_at_a);
                                lin_at_a.setVisibility(View.GONE);
                                image_path2 = "1"; // yha validation ko rokta ha
                            }

                            if(nrx.equals("0")){
                                LinearLayout lin_nrx = findViewById(R.id.lin_nrx);
                                lin_nrx.setVisibility(View.GONE);
                                image_path4 = "1"; // yha validation ko rokta ha
                            }
                        }

                        listview1.setVisibility(View.GONE);
                        arrayList.clear();
                        int intid = 0;
                        JSONArray jArray_items_others = new JSONArray(items_others);
                        for (int i = 0; i < jArray_items_others.length(); i++) {

                            JSONObject jsonObject = jArray_items_others.getJSONObject(i);

                            String myid = jsonObject.getString("id");
                            String image = jsonObject.getString("image");
                            String date = jsonObject.getString("date");
                            String time = jsonObject.getString("time");

                            Delivery_chemist_photo_get_or_set mylist = new Delivery_chemist_photo_get_or_set();
                            mylist.id(myid);
                            mylist.image(image);
                            mylist.datetime(date + " at " +time);
                            mylist.intid(String.valueOf(intid++));
                            arrayList.add(mylist);
                        }
                        adapter.notifyDataSetChanged();
                        if(intid!=0){
                            listview1.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        menu_loading1.setVisibility(View.GONE);
                        Log.e("Bg-service", "Error parsing data" + e.toString());
                        Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle the error
                    menu_loading1.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failures or other errors
                menu_loading1.setVisibility(View.GONE);
                Log.e("Bg-service-onFailure", " " + t.toString());
                Toast.makeText(Delivery_chemist_photo.this, "show_rider_chemist_photo_api onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void upload_delivery_order_photo() {
        menu_loading1.setVisibility(View.VISIBLE);
        buttonUpload.setVisibility(View.GONE);
        buttonUpload1.setVisibility(View.VISIBLE);
        try {
            String payment_type = String.valueOf(spinner.getSelectedItem());
            String message = enter_message.getText().toString();
            String payment_message = enter_message2.getText().toString();

            String latitude = getlatitude;
            String longitude = getlongitude;

            Context context = getApplicationContext();
            AssetManager assetManager = context.getAssets();
            File imageFile0 = File.createTempFile("temp_image", null, getCacheDir());
            File imageFile1 = File.createTempFile("temp_image", null, getCacheDir());
            File imageFile2 = File.createTempFile("temp_image", null, getCacheDir());
            File imageFile3 = File.createTempFile("temp_image", null, getCacheDir());
            File imageFile4 = File.createTempFile("temp_image", null, getCacheDir());

            if(!currentPhotoPath0.isEmpty()) {
                selectedPath0 = compressImage(currentPhotoPath0);
                imageFile0      = new File(selectedPath0);
            }if(!currentPhotoPath1.isEmpty()) {
                selectedPath1 = compressImage(currentPhotoPath1);
                imageFile1      = new File(selectedPath1);
            }if(!currentPhotoPath2.isEmpty()) {
                selectedPath2 = compressImage(currentPhotoPath2);
                imageFile2      = new File(selectedPath2);
            }if(!currentPhotoPath3.isEmpty()) {
                selectedPath3 = compressImage(currentPhotoPath3);
                imageFile3      = new File(selectedPath3);
            }if(!currentPhotoPath4.isEmpty()) {
                selectedPath4 = compressImage(currentPhotoPath4);
                imageFile4     = new File(selectedPath4);
            }

            RequestBody requestFile1 = RequestBody.create(MultipartBody.FORM, imageFile1);
            MultipartBody.Part image1 = MultipartBody.Part.createFormData("image1", imageFile1.getName(), requestFile1);
            RequestBody requestFile2 = RequestBody.create(MultipartBody.FORM, imageFile2);
            MultipartBody.Part image2 = MultipartBody.Part.createFormData("image2", imageFile2.getName(), requestFile2);
            RequestBody requestFile3 = RequestBody.create(MultipartBody.FORM, imageFile3);
            MultipartBody.Part image3 = MultipartBody.Part.createFormData("image3", imageFile3.getName(), requestFile3);
            RequestBody requestFile4 = RequestBody.create(MultipartBody.FORM, imageFile4);
            MultipartBody.Part image4 = MultipartBody.Part.createFormData("image4", imageFile4.getName(), requestFile4);

            // add another part within the multipart request
            RequestBody api_key1 = RequestBody.create(MultipartBody.FORM, "98c08565401579448aad7c64033dcb4081906dcb");
            RequestBody user_code1 = RequestBody.create(MultipartBody.FORM, user_code);
            RequestBody user_altercode1 = RequestBody.create(MultipartBody.FORM, user_altercode);
            RequestBody latitude11 = RequestBody.create(MultipartBody.FORM, latitude);
            RequestBody longitude11 = RequestBody.create(MultipartBody.FORM, longitude);
            RequestBody chemist_code1 = RequestBody.create(MultipartBody.FORM, chemist_code);
            RequestBody id1 = RequestBody.create(MultipartBody.FORM, _id);
            RequestBody tagno1 = RequestBody.create(MultipartBody.FORM, mytagno);
            RequestBody gstvno1 = RequestBody.create(MultipartBody.FORM, gstvno);
            RequestBody message1 = RequestBody.create(MultipartBody.FORM, message);
            RequestBody payment_message1 = RequestBody.create(MultipartBody.FORM, payment_message);
            RequestBody payment_type1 = RequestBody.create(MultipartBody.FORM, payment_type);

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Call<ResponseBody> call = apiService.upload_delivery_order_photo_api(
                    api_key1,
                    user_code1,
                    user_altercode1,
                    chemist_code1,
                    gstvno1,
                    id1,
                    tagno1,
                    message1,
                    payment_message1,
                    payment_type1,
                    image1,
                    image2,
                    image3,
                    image4,
                    latitude11,
                    longitude11);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Image uploaded successfully
                        // Handle the response, if any
                        try {
                            //Toast.makeText(Delivery_chemist_page.this, response.body().string(), Toast.LENGTH_LONG).show();
                            menu_loading1.setVisibility(View.GONE);
                            JSONArray jArray = new JSONArray(response.body().string());
                            for (int i = 0; i < jArray.length(); i++) {

                                JSONObject jsonObject = jArray.getJSONObject(i);
                                String return_id = jsonObject.getString("return_id");
                                String return_message = jsonObject.getString("return_message");

                                if (return_id.equals("1")) {
                                    //finish();
                                    get_delivery_order_photo_api();
                                }
                                Toast.makeText(Delivery_chemist_photo.this, return_message.toString(), Toast.LENGTH_LONG).show();
                            }

                            delete_image(currentPhotoPath1);
                            delete_image(selectedPath1);
                            delete_image(currentPhotoPath2);
                            delete_image(selectedPath2);
                            delete_image(currentPhotoPath3);
                            delete_image(selectedPath3);
                            delete_image(currentPhotoPath4);
                            delete_image(selectedPath4);
                        } catch (Exception e) {
                            // TODO: handle exception
                            menu_loading1.setVisibility(View.GONE);
                            Log.e("Bg-service", "Error parsing data" + e.toString());
                            Toast.makeText(Delivery_chemist_photo.this, "Error " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Handle the error
                        menu_loading1.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle network errors or exceptions
                    menu_loading1.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "e1 " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            menu_loading1.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "e2 " + e.getMessage(), Toast.LENGTH_LONG).show();
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



    private void upload_delivery_order_photo_more() {
        Toast.makeText(Delivery_chemist_photo.this,"Uploading Start", Toast.LENGTH_LONG).show();

        menu_loading1.setVisibility(View.VISIBLE);
        try {
            Context context = getApplicationContext();
            AssetManager assetManager = context.getAssets();
            File imageFile0 = File.createTempFile("temp_image", null, getCacheDir());

            if(!currentPhotoPath0.isEmpty()) {
                selectedPath0 = compressImage(currentPhotoPath0);
                imageFile0      = new File(selectedPath0);
            }

            RequestBody requestFile0 = RequestBody.create(MultipartBody.FORM, imageFile0);
            MultipartBody.Part image0 = MultipartBody.Part.createFormData("image", imageFile0.getName(), requestFile0);

            // add another part within the multipart request
            RequestBody api_key1 = RequestBody.create(MultipartBody.FORM, "98c08565401579448aad7c64033dcb4081906dcb");
            RequestBody user_code1 = RequestBody.create(MultipartBody.FORM, user_code);
            RequestBody user_altercode1 = RequestBody.create(MultipartBody.FORM, user_altercode);
            RequestBody chemist_code1 = RequestBody.create(MultipartBody.FORM, chemist_code);
            RequestBody gstvno1 = RequestBody.create(MultipartBody.FORM, gstvno);

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Call<ResponseBody> call = apiService.upload_delivery_order_photo_more_api(
                    api_key1,
                    user_code1,
                    user_altercode1,
                    chemist_code1,
                    gstvno1,
                    image0);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Image uploaded successfully
                        // Handle the response, if any
                        try {
                            //Toast.makeText(Delivery_chemist_photo.this, response.body().string(), Toast.LENGTH_LONG).show();
                            menu_loading1.setVisibility(View.GONE);
                            JSONArray jArray = new JSONArray(response.body().string());
                            for (int i = 0; i < jArray.length(); i++) {

                                JSONObject jsonObject = jArray.getJSONObject(i);
                                String return_id = jsonObject.getString("return_id");
                                String return_message = jsonObject.getString("return_message");

                                if (return_id.equals("1")) {
                                    //finish();
                                    get_delivery_order_photo_api();
                                }
                                Toast.makeText(Delivery_chemist_photo.this, return_message.toString(), Toast.LENGTH_LONG).show();
                            }
                            delete_image(currentPhotoPath0);
                            delete_image(selectedPath0);
                        } catch (Exception e) {
                            // TODO: handle exception
                            menu_loading1.setVisibility(View.GONE);
                            Log.e("Bg-service", "Error parsing data" + e.toString());
                            Toast.makeText(Delivery_chemist_photo.this, "Error " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Handle the error
                        menu_loading1.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle network errors or exceptions
                    menu_loading1.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "e1 " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            menu_loading1.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "e2 " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void alertMessage_delete_delivery_order_photo_more(String myid) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        try {
                            delete_delivery_order_photo_more(myid);
                        } catch (Exception e) {
                            // TODO: handle exception
                            Toast.makeText(Delivery_chemist_photo.this, "alertMessage_delete_delivery_order_photo_more error", Toast.LENGTH_SHORT).show();
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
        builder.setMessage("Are you sure to Delete this photo?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void delete_delivery_order_photo_more(String myid) {
        Toast.makeText(Delivery_chemist_photo.this, "Loading....", Toast.LENGTH_LONG).show();
        menu_loading1.setVisibility(View.VISIBLE);
        try {

            // add another part within the multipart request
            RequestBody api_key1 = RequestBody.create(MultipartBody.FORM, "98c08565401579448aad7c64033dcb4081906dcb");
            RequestBody user_code1 = RequestBody.create(MultipartBody.FORM, user_code);
            RequestBody user_altercode1 = RequestBody.create(MultipartBody.FORM, user_altercode);
            RequestBody chemist_code1 = RequestBody.create(MultipartBody.FORM, chemist_code);
            RequestBody gstvno1 = RequestBody.create(MultipartBody.FORM, gstvno);
            RequestBody myid1 = RequestBody.create(MultipartBody.FORM, myid);

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Call<ResponseBody> call = apiService.delete_delivery_order_photo_more_api(
                    api_key1,
                    user_code1,
                    user_altercode1,
                    chemist_code1,
                    gstvno1,
                    myid1);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Image uploaded successfully
                        // Handle the response, if any
                        try {
                            //Toast.makeText(Delivery_chemist_photo.this, response.body().string(), Toast.LENGTH_LONG).show();
                            menu_loading1.setVisibility(View.GONE);
                            JSONArray jArray = new JSONArray(response.body().string());
                            for (int i = 0; i < jArray.length(); i++) {

                                JSONObject jsonObject = jArray.getJSONObject(i);
                                String return_id = jsonObject.getString("return_id");
                                String return_message = jsonObject.getString("return_message");

                                if (return_id.equals("1")) {
                                    //finish();
                                    get_delivery_order_photo_api();
                                }
                                Toast.makeText(Delivery_chemist_photo.this, return_message.toString(), Toast.LENGTH_LONG).show();
                            }
                            delete_image(currentPhotoPath0);
                            delete_image(selectedPath0);
                        } catch (Exception e) {
                            // TODO: handle exception
                            menu_loading1.setVisibility(View.GONE);
                            Log.e("Bg-service", "Error parsing data" + e.toString());
                            Toast.makeText(Delivery_chemist_photo.this, "Error " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Handle the error
                        menu_loading1.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle network errors or exceptions
                    menu_loading1.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "e1 " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            menu_loading1.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "e2 " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}