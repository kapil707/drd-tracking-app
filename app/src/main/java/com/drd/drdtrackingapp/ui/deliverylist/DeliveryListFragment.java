package com.drd.drdtrackingapp.ui.deliverylist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.drd.drdtrackingapp.ApiService;
import com.drd.drdtrackingapp.Delivery_order_list_tagno;
import com.drd.drdtrackingapp.RetrofitClient;
import com.drd.drdtrackingapp.UserSessionManager;
import com.drd.drdtrackingapp.Delivery_order_list_Adapter;
import com.drd.drdtrackingapp.Delivery_order_list_get_or_set;
import com.drd.drdtrackingapp.databinding.FragmentDeliveryListBinding;

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

public class DeliveryListFragment extends Fragment {

    private FragmentDeliveryListBinding binding;

    UserSessionManager session;
    String user_code = "", user_altercode = "";

    ListView listview;
    Delivery_order_list_Adapter adapter;
    List<Delivery_order_list_get_or_set> movieList = new ArrayList<Delivery_order_list_get_or_set>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDeliveryListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        session = new UserSessionManager(this.getContext());
        HashMap<String, String> user = session.getUserDetails();
        user_code = user.get(UserSessionManager.KEY_USERCODE);
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);

        listview = binding.DeliveryListListView;
        adapter = new Delivery_order_list_Adapter(getContext(), movieList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                Delivery_order_list_get_or_set clickedCategory = movieList.get(arg2);
                String mytagno = clickedCategory.mytagno();
                String mydate = clickedCategory.mydate();
                String mytime = clickedCategory.mytime();

                //alertMessage_selected_acm();

                Intent in = new Intent();
                in.setClass(getContext(), Delivery_order_list_tagno.class);
                in.putExtra("mytagno", mytagno);
                in.putExtra("mydate", mydate);
                in.putExtra("mytime", mytime);
                startActivity(in);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        deliver_list_api();
    }

    private void deliver_list_api() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.get_delivery_order_list_api("98c08565401579448aad7c64033dcb4081906dcb", user_code, user_altercode,"");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(getContext(),"deliver_list_api onResponse",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "get_delivery_order_api onFailure : " + t.toString(), Toast.LENGTH_SHORT).show();
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
                String tagno = jsonObject.getString("tagno");
                String date = jsonObject.getString("date");
                String time = jsonObject.getString("time");

                Delivery_order_list_get_or_set movie = new Delivery_order_list_get_or_set();
                movie.mytagno(tagno);
                movie.mydate(date);
                movie.mytime(time);
                movie.intid(String.valueOf(intid++));
                movieList.add(movie);
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Bg-service", "Error parsing data" + e.toString());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}