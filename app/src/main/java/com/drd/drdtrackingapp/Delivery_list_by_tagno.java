package com.drd.drdtrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Delivery_list_by_tagno extends AppCompatActivity {


    UserSessionManager session;
    String user_code = "", user_altercode = "";

    ListView listview1;
    Delivery_list_by_tagno_Adapter adapter;
    List<Delivery_list_by_tagno_get_or_set> arrayList = new ArrayList<Delivery_list_by_tagno_get_or_set>();
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
        String mytagno = in.getStringExtra("mytagno");

        Toast.makeText(Delivery_list_by_tagno.this, mytagno, Toast.LENGTH_LONG).show();
    }
}