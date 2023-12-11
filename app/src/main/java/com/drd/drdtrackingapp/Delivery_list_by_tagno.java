package com.drd.drdtrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
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

public class Delivery_list_by_tagno extends AppCompatActivity {


    UserSessionManager session;
    String user_code = "", user_altercode = "";

    ListView listview1;
    Delivery_list_by_tagno_Adapter adapter;
    List<Delivery_list_by_tagno_get_or_set> arrayList = new ArrayList<Delivery_list_by_tagno_get_or_set>();
    String tagno ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_list_by_tagno);

        session = new UserSessionManager(Delivery_list_by_tagno.this);
        HashMap<String, String> user = session.getUserDetails();
        user_code = user.get(UserSessionManager.KEY_USERCODE);
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);

        listview1 = findViewById(R.id.Listview1);
        adapter = new Delivery_list_by_tagno_Adapter(Delivery_list_by_tagno.this, arrayList);
        listview1.setAdapter(adapter);

        Intent in = getIntent();
        tagno = in.getStringExtra("mytagno");

        get_delivery_order_by_tagno_api();
        //Toast.makeText(Delivery_list_by_tagno.this, mytagno, Toast.LENGTH_LONG).show();
    }

    private void get_delivery_order_by_tagno_api() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.get_delivery_order_by_tagno_api(
                "98c08565401579448aad7c64033dcb4081906dcb",
                user_code,
                user_altercode,
                tagno);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                // Handle network failures or other errors
                Log.e("Bg-service-onFailure", " " + t.toString());
                Toast.makeText(Delivery_list_by_tagno.this, "get_delivery_order_api onFailure : " + t.toString(), Toast.LENGTH_SHORT).show();
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
                String gstvno = jsonObject.getString("gstvno");
                String mydate = jsonObject.getString("mydate");
                String chemist_code = jsonObject.getString("chemist_code");
                String chemist_name = jsonObject.getString("chemist_name");
                String amount = jsonObject.getString("amount");
                String medicine_items = jsonObject.getString("medicine_items");

                Delivery_list_by_tagno_get_or_set mylist = new Delivery_list_by_tagno_get_or_set();
                mylist.gstvno(gstvno);
                mylist.mydate(mydate);
                mylist.chemist_code(chemist_code);
                mylist.chemist_name(chemist_name);
                mylist.amount(amount);
                mylist.medicine_items(medicine_items);
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