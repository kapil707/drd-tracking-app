package com.drd.drdtrackingapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.provider.MediaStore;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
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
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Bitmap bitmap1, bitmap2, bitmap3, bitmap4;
    Button photo_btn1, photo_btn2, photo_btn3, photo_btn4;
    TextView tv_error1, tv_error2, tv_error3, tv_error4, enter_remarks_error,enter_remarks_tv;
    Button buttonUpload, buttonUpload1;
    EditText enter_remarks;
    boolean check = true;
    String upload_delivery_order_photo_api = "";

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

        MainActivity ma = new MainActivity();
        String mainurl = ma.main_url;
        upload_delivery_order_photo_api = mainurl + "upload_delivery_order_photo_api";

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
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1891);
                }
            }
        });

        photo_btn2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1892);
                }
            }
        });

        photo_btn3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1893);
                }
            }
        });

        photo_btn4.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1894);
                }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1891);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {
        super.onActivityResult(RC, RQC, I);
        //Toast.makeText(User_image_uploading.this, String.valueOf(RC), Toast.LENGTH_LONG).show();
        if (RC == 1891) {
            bitmap1 = (Bitmap) I.getExtras().get("data");
            photo1.setImageBitmap(bitmap1);
            tv_error1.setVisibility(View.GONE);
        }
        if (RC == 1892) {
            bitmap2 = (Bitmap) I.getExtras().get("data");
            photo2.setImageBitmap(bitmap2);
            tv_error2.setVisibility(View.GONE);
        }
        if (RC == 1893) {
            bitmap3 = (Bitmap) I.getExtras().get("data");
            photo3.setImageBitmap(bitmap3);
            tv_error3.setVisibility(View.GONE);
        }
        if (RC == 1894) {
            bitmap4 = (Bitmap) I.getExtras().get("data");
            photo4.setImageBitmap(bitmap4);
            tv_error4.setVisibility(View.GONE);
        }
    }

    public void alertMessage_complete_order() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        try {
                            upload_delivery_order_photo_api();
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


    public void upload_delivery_order_photo_api() {
        mGPS_info();
        String message = enter_remarks.getText().toString();

        String ConvertImage1 = "", ConvertImage2 = "", ConvertImage3 = "", ConvertImage4 = "";
        try {
            ByteArrayOutputStream byteArrayOutputStreamObject;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamObject);
            byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
            ConvertImage1 = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        } catch (Exception ee) {

        }

        try {
            ByteArrayOutputStream byteArrayOutputStreamObject;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamObject);
            byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
            ConvertImage2 = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        } catch (Exception ee) {

        }

        try {
            ByteArrayOutputStream byteArrayOutputStreamObject;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();
            bitmap3.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamObject);
            byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
            ConvertImage3 = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        } catch (Exception ee) {

        }

        try {
            ByteArrayOutputStream byteArrayOutputStreamObject;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();
            bitmap4.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamObject);
            byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
            ConvertImage4 = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        } catch (Exception ee) {

        }
        final String finalConvertImage1 = ConvertImage1;
        final String finalConvertImage2 = ConvertImage2;
        final String finalConvertImage3 = ConvertImage3;
        final String finalConvertImage4 = ConvertImage4;

        int error = 0;
        tv_error1.setVisibility(View.GONE);
        tv_error2.setVisibility(View.GONE);
        tv_error3.setVisibility(View.GONE);
        tv_error4.setVisibility(View.GONE);
        enter_remarks_error.setVisibility(View.GONE);
        if (ConvertImage1.equals(null) || ConvertImage1.isEmpty()) {
            error = 1;
            tv_error1.setVisibility(View.VISIBLE);
            //Toast.makeText(Delivery_chemist_page.this, "Select Material photo1", Toast.LENGTH_LONG).show();
        }
        if (ConvertImage2.equals(null) || ConvertImage2.isEmpty()) {
            error = 1;
            tv_error2.setVisibility(View.VISIBLE);
            //Toast.makeText(Delivery_chemist_page.this, "Select Material photo2", Toast.LENGTH_LONG).show();
        }
        if (ConvertImage3.equals(null) || ConvertImage3.isEmpty()) {
            error = 1;
            tv_error3.setVisibility(View.VISIBLE);
            //Toast.makeText(Delivery_chemist_page.this, "Select Payment Detail", Toast.LENGTH_LONG).show();
        }
        if (ConvertImage3.equals(null) || ConvertImage3.isEmpty()) {
            error = 1;
            tv_error4.setVisibility(View.VISIBLE);
            //Toast.makeText(Delivery_chemist_page.this, "Select NR ackn", Toast.LENGTH_LONG).show();
        }
        if (message.equals(null) || message.isEmpty()) {
            error = 1;
            enter_remarks_error.setVisibility(View.VISIBLE);
            //Toast.makeText(Delivery_chemist_page.this, "Enter Remarks", Toast.LENGTH_LONG).show();
        }
        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressDialog = ProgressDialog.show(User_image_uploading.this,"Uploading","Please Wait",false,false);
                menu_loading1.setVisibility(View.VISIBLE);

                buttonUpload.setVisibility(View.GONE);
                buttonUpload1.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();

                HashMapParams.put("api_key", "98c08565401579448aad7c64033dcb4081906dcb");
                HashMapParams.put("user_code", user_code);
                HashMapParams.put("user_altercode", user_altercode);

                HashMapParams.put("ImagePath1", finalConvertImage1);
                HashMapParams.put("ImagePath2", finalConvertImage2);
                HashMapParams.put("ImagePath3", finalConvertImage3);
                HashMapParams.put("ImagePath4", finalConvertImage4);

                HashMapParams.put("chemist_id", chemist_id);
                HashMapParams.put("gstvno", gstvno);

                HashMapParams.put("message", message);
                HashMapParams.put("latitude", getlatitude);
                HashMapParams.put("longitude", getlongitude);

                String FinalData = imageProcessClass.ImageHttpRequest(upload_delivery_order_photo_api, HashMapParams);
                return FinalData;
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                menu_loading1.setVisibility(View.GONE);

                try {
                    JSONArray jArray = new JSONArray(response);
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject jsonObject = jArray.getJSONObject(i);
                        String return_id = jsonObject.getString("return_id");
                        String return_message = jsonObject.getString("return_message");

                        if (return_id.equals("1")) {
                            get_delivery_order_photo_api();
                        }

                        Toast.makeText(Delivery_chemist_page.this, return_message.toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("Bg-service", "Error parsing data" + e.toString());
                }
            }
        }

        if (error == 0) {
            AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
            AsyncTaskUploadClassOBJ.execute();
        }
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