package com.drd.drdtrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Delivery_order_list extends AppCompatActivity {

    ProgressBar menu_loading1;
    UserSessionManager session;
    String user_code = "", user_altercode = "";

    ListView listview1;
    Delivery_order_list_Adapter adapter;
    List<Delivery_order_list_get_or_set> arrayList = new ArrayList<Delivery_order_list_get_or_set>();
    String status="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_order_list);

        session = new UserSessionManager(Delivery_order_list.this);
        HashMap<String, String> user = session.getUserDetails();
        user_code = user.get(UserSessionManager.KEY_USERCODE);
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);

        Intent in = getIntent();
        status = in.getStringExtra("status");

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

        TextView action_bar_title1 = (TextView) findViewById(R.id.action_bar_title);
        action_bar_title1.setText("Delivery Order List");
        TextView action_bar_title11 = (TextView) findViewById(R.id.action_bar_title1);
        //action_bar_title11.setText(mydate+" - " +mytime);
        action_bar_title11.setVisibility(View.GONE);
        ImageButton imageButton = findViewById(R.id.action_bar_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        menu_loading1 = (ProgressBar) findViewById(R.id.menu_loading1);

        listview1 = findViewById(R.id.Listview1);
        adapter = new Delivery_order_list_Adapter(Delivery_order_list.this, arrayList);
        listview1.setAdapter(adapter);

        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                Delivery_order_list_get_or_set clickedCategory = arrayList.get(arg2);
                String mytagno = clickedCategory.mytagno();
                String mydate = clickedCategory.mydate();
                String mytime = clickedCategory.mytime();

                //alertMessage_selected_acm();

                Intent in = new Intent();
                in.setClass(Delivery_order_list.this, Delivery_order_list_tagno.class);
                in.putExtra("mytagno", mytagno);
                in.putExtra("mydate", mydate);
                in.putExtra("mytime",mytime);
                in.putExtra("mytime",mytime);
                startActivity(in);
            }
        });

        get_delivery_order_list_api();
        //Toast.makeText(Delivery_list_by_tagno.this, mytagno, Toast.LENGTH_LONG).show();
    }

    private void get_delivery_order_list_api() {
        menu_loading1.setVisibility(View.VISIBLE);
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.get_delivery_order_list_api(
                "98c08565401579448aad7c64033dcb4081906dcb",
                user_code,
                user_altercode,
                status);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                menu_loading1.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    //Toast.makeText(Delivery_list_by_tagno.this,"deliver_list_api onResponse",Toast.LENGTH_SHORT).show();
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
                menu_loading1.setVisibility(View.GONE);
                // Handle network failures or other errors
                Log.e("Bg-service-onFailure", " " + t.toString());
                Toast.makeText(Delivery_order_list.this, "get_delivery_order_api onFailure : " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeTv(String response) {
        //https://demonuts.com/retrofit-android-get-json/
        //Log.e("Bg-service", response.toString());
        arrayList.clear();
        try {
            int intid = 0;
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++) {

                JSONObject jsonObject = jArray.getJSONObject(i);
                String mytagno = jsonObject.getString("tagno");
                String mydate = jsonObject.getString("date");
                String mytime = jsonObject.getString("time");

                Delivery_order_list_get_or_set mylist = new Delivery_order_list_get_or_set();
                mylist.mytagno(mytagno);
                mylist.mydate(mydate);
                mylist.mytime(mytime);
                mylist.intid(String.valueOf(intid++));
                arrayList.add(mylist);
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Bg-service", "Error parsing data" + e.toString());
        }
        adapter.notifyDataSetChanged();
    }
}