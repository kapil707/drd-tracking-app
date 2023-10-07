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
import android.graphics.Bitmap;
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

public class Dilivery_chemist_info_page extends AppCompatActivity {
    ProgressBar menu_loading1;
    UserSessionManager session;
    String session_id = "", user_altercode = "";
    String chemist_id = "", gstvno = "";

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Bitmap bitmap;
    Button take_photo,galery_select;
    Button UploadImageServer, UploadImageServer1;
    boolean check = true;
    String ImagePath = "image_path";
    String ServerUploadPath = "";

    GridView listview;
    Dilivery_chemist_info_page_Adapter adapter;
    List<Dilivery_chemist_info_page_get_or_set> movieList = new ArrayList<Dilivery_chemist_info_page_get_or_set>();


    GPSTracker mGPS;
    double latitude1, longitude1;
    String getlatitude = "", getlongitude = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dilivery_chemist_info_page);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.new_theme_color));
        }

        Intent in = getIntent();
        chemist_id = in.getStringExtra("chemist_id");
        gstvno = in.getStringExtra("gstvno");

        TextView action_bar_title1 = (TextView) findViewById(R.id.action_bar_title);
        action_bar_title1.setText("Update image (" + chemist_id + ")");
        TextView action_bar_title11 = (TextView) findViewById(R.id.action_bar_title1);
        action_bar_title11.setText(gstvno);
        action_bar_title11.setVisibility(View.VISIBLE);
        ImageButton imageButton = (ImageButton) findViewById(R.id.action_bar_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        menu_loading1 = (ProgressBar) findViewById(R.id.menu_loading1);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        session_id = user.get(UserSessionManager.KEY_USERID);
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);

        MainActivity ma = new MainActivity();
        String mainurl = ma.main_url;
        ServerUploadPath = mainurl + "upload_delivery_order_photo_api";

        imageView = (ImageView) findViewById(R.id.imageView);
        take_photo = findViewById(R.id.take_photo);
        galery_select = findViewById(R.id.galery_select);
        UploadImageServer = (Button) findViewById(R.id.buttonUpload);
        UploadImageServer1 = (Button) findViewById(R.id.buttonUpload1);

        UploadImageServer.setVisibility(View.GONE);
        UploadImageServer1.setVisibility(View.VISIBLE);

        galery_select.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image from gallery"), 1989);

            }
        });

        take_photo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        UploadImageServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUploadToServerFunction();
            }
        });

        final LinearLayout upload_order_LinearLayout = findViewById(R.id.upload_order_LinearLayout);
        final LinearLayout complete_order_LinearLayout = findViewById(R.id.complete_order_LinearLayout);
        final ImageView upload_btn = findViewById(R.id.upload_btn);
        upload_btn.setVisibility(View.VISIBLE);
        upload_order_LinearLayout.setVisibility(View.VISIBLE);
        final ImageView upload_cancel = findViewById(R.id.upload_cancel);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_btn.setVisibility(View.GONE);
                upload_order_LinearLayout.setVisibility(View.GONE);
                upload_cancel.setVisibility(View.VISIBLE);
                complete_order_LinearLayout.setVisibility(View.VISIBLE);
            }
        });

        upload_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_btn.setVisibility(View.VISIBLE);
                upload_order_LinearLayout.setVisibility(View.VISIBLE);
                upload_cancel.setVisibility(View.GONE);
                complete_order_LinearLayout.setVisibility(View.GONE);
            }
        });

        Button complete_order = findViewById(R.id.complete_order);
        complete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertMessage_complete_order();
            }
        });


        listview = findViewById(R.id.listView1);
        adapter = new Dilivery_chemist_info_page_Adapter(Dilivery_chemist_info_page.this, movieList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                Dilivery_chemist_info_page_get_or_set clickedCategory = movieList.get(arg2);
                String id = clickedCategory.id();
                //alertMessage_delete_rider_chemist_photo(id);
                //alertMessage_selected_acm();
            }
        });

        listview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                // TODO Auto-generated method stub
                /* Toast.makeText(getApplicationContext(), "Position",Toast.LENGTH_LONG).show(); */
                return false;
            }
        });

        get_delivery_order_photo_api();
    }

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
    protected void onActivityResult(int RC, int RQC, Intent I) {
        super.onActivityResult(RC, RQC, I);
        //Toast.makeText(User_image_uploading.this, String.valueOf(RC), Toast.LENGTH_LONG).show();
        if (RC == CAMERA_REQUEST) {
            bitmap = (Bitmap) I.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);

            UploadImageServer.setVisibility(View.VISIBLE);
            UploadImageServer1.setVisibility(View.GONE);
        }
        if (RC == 1989) {
            Uri uri = I.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                UploadImageServer.setVisibility(View.VISIBLE);
                UploadImageServer1.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ImageUploadToServerFunction() {
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
                //progressDialog = ProgressDialog.show(User_image_uploading.this,"Uploading","Please Wait",false,false);
                menu_loading1.setVisibility(View.VISIBLE);
                UploadImageServer.setVisibility(View.GONE);
                UploadImageServer1.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String user_image_server) {
                super.onPostExecute(user_image_server);
                menu_loading1.setVisibility(View.GONE);
                // Dismiss the progress dialog after done uploading.
                //progressDialog.dismiss();
                // Printing uploading success message coming from server on android app.
                Toast.makeText(Dilivery_chemist_info_page.this, user_image_server.toString(), Toast.LENGTH_LONG).show();
                imageView.setVisibility(View.GONE);

                get_delivery_order_photo_api();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();

                HashMapParams.put("api_key", "98c08565401579448aad7c64033dcb4081906dcb");

                HashMapParams.put(ImagePath, finalConvertImage);
                HashMapParams.put("user_altercode", user_altercode);
                HashMapParams.put("chemist_id", chemist_id);
                HashMapParams.put("gstvno", gstvno);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
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

    private void get_delivery_order_photo_api(){
        Toast.makeText(Dilivery_chemist_info_page.this,"deliver_list_api working",Toast.LENGTH_SHORT).show();
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<ResponseBody> call = apiService.get_delivery_order_photo_api("98c08565401579448aad7c64033dcb4081906dcb",user_altercode,chemist_id,gstvno);
        //Call<ResponseBody> call = apiService.testing("loginRequest");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle success response
                    // response.body() contains the response data

                    Toast.makeText(Dilivery_chemist_info_page.this,"show_rider_chemist_photo_api onResponse",Toast.LENGTH_SHORT).show();

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
                Log.e("Bg-service-onFailure", " " + t.toString());
                Toast.makeText(Dilivery_chemist_info_page.this,"show_rider_chemist_photo_api onFailure",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeTv(String response) {
        //https://demonuts.com/retrofit-android-get-json/
        //Log.e("Bg-service", response.toString());
        movieList.clear();
        try {
            int intid = 0;
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++) {

                JSONObject jsonObject = jArray.getJSONObject(i);
                String id =  jsonObject.getString("id");
                String image = jsonObject.getString("image");
                String time = jsonObject.getString("time");

                Dilivery_chemist_info_page_get_or_set movie = new Dilivery_chemist_info_page_get_or_set();
                movie.id(id);
                movie.image(image);
                movie.time(time);
                movie.intid(String.valueOf(intid++));
                movieList.add(movie);
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Bg-service", "Error parsing data" + e.toString());
        }
        adapter.notifyDataSetChanged();
    }

    public void alertMessage_complete_order() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        try {
                            mGPS_info();
                            upload_delivery_order_completed_api();
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

    private void upload_delivery_order_completed_api(){
        Toast.makeText(Dilivery_chemist_info_page.this,"deliver_list_api working",Toast.LENGTH_SHORT).show();
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        EditText enter_remarks = findViewById(R.id.enter_remarks);
        String message = enter_remarks.getText().toString();

        Call<ResponseBody> call = apiService.upload_delivery_order_completed_api("98c08565401579448aad7c64033dcb4081906dcb",user_altercode,chemist_id,gstvno,message,getlatitude,getlongitude);
        //Call<ResponseBody> call = apiService.testing("loginRequest");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle success response
                    // response.body() contains the response data

                    Toast.makeText(Dilivery_chemist_info_page.this,"show_rider_chemist_photo_api onResponse",Toast.LENGTH_SHORT).show();

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
                Log.e("Bg-service-onFailure", " " + t.toString());
                Toast.makeText(Dilivery_chemist_info_page.this,"show_rider_chemist_photo_api onFailure",Toast.LENGTH_SHORT).show();
            }
        });
    }
}