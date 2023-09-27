package com.drd.drdtrackingapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.drd.drdtrackingapp.ApiService;
import com.drd.drdtrackingapp.Home_page;
import com.drd.drdtrackingapp.Login_page;
import com.drd.drdtrackingapp.RetrofitClient;
import com.drd.drdtrackingapp.UserSessionManager;
import com.drd.drdtrackingapp.databinding.FragmentHomeBinding;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    UserSessionManager session;
    String user_altercode = "";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        session = new UserSessionManager(this.getContext());
        HashMap<String, String> user = session.getUserDetails();
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);



        slider_api();

        final TextView textView = binding.textHome;
        textView.setText("Login User : " +user_altercode);
        return root;
    }

    private void slider_api(){
        Toast.makeText(getContext(),"slider_api working",Toast.LENGTH_SHORT).show();
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<ResponseBody> call = apiService.slider_api("98c08565401579448aad7c64033dcb4081906dcb");
        //Call<ResponseBody> call = apiService.testing("loginRequest");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle success response
                    // response.body() contains the response data

                    Toast.makeText(getContext(),"slider_api onResponse",Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getContext(),"slider_api onFailure",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeTv(String response){
        //https://demonuts.com/retrofit-android-get-json/
        //Log.e("Bg-service", response.toString());
        try {
            ArrayList<SlideModel> imageList = new ArrayList<>();
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++)
            {
                JSONObject jsonObject   = jArray.getJSONObject(i);
                String image     = jsonObject.getString("image");


                imageList.add(new SlideModel(image, "The animal population decreased by 58 percent in 42 years.", ScaleTypes.FIT));



            }
            ImageSlider imageSlider = binding.imageSlider;
            imageSlider.setImageList(imageList);
        }
        catch (Exception e) {
            // TODO: handle exception
            Log.e("Bg-service", "Error parsing data"+e.toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}