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
import com.drd.drdtrackingapp.RetrofitClient;
import com.drd.drdtrackingapp.Delivery_chemist_photo;
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

public class DeliveryDoneListFragment extends Fragment {

    private FragmentDeliveryListBinding binding;

    UserSessionManager session;
    String user_code = "", user_altercode = "";

    ListView listview;
    Delivery_order_list_Adapter adapter;
    List<Delivery_order_list_get_or_set> get_set = new ArrayList<Delivery_order_list_get_or_set>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDeliveryListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        session = new UserSessionManager(this.getContext());
        HashMap<String, String> user = session.getUserDetails();
        user_code = user.get(UserSessionManager.KEY_USERCODE);
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);

        listview = binding.DeliveryListListView;
        adapter = new Delivery_order_list_Adapter(getContext(), get_set);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                Delivery_order_list_get_or_set clickedCategory = get_set.get(arg2);
                String chemist_id = clickedCategory.mytagno();
                String gstvno = clickedCategory.mydate();

                //alertMessage_selected_acm();

                Intent in = new Intent();
                in.setClass(getContext(), Delivery_chemist_photo.class);
                in.putExtra("chemist_id", chemist_id);
                in.putExtra("gstvno", gstvno);
                in.putExtra("edit_yes_no", "no");
                startActivity(in);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void writeTv(String response) {
        //https://demonuts.com/retrofit-android-get-json/
        //Log.e("Bg-service", response.toString());
        get_set.clear();
        try {
            int intid = 0;
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++) {

                JSONObject jsonObject = jArray.getJSONObject(i);
                String chemist_id = jsonObject.getString("chemist_id");
                String name = jsonObject.getString("name");
                String amt = jsonObject.getString("amt");
                String gstvno = jsonObject.getString("gstvno");

                Delivery_order_list_get_or_set mylist = new Delivery_order_list_get_or_set();
                mylist.mytagno(chemist_id);
                mylist.mydate(name);
                mylist.mytime(amt);
                mylist.intid(String.valueOf(intid++));
                get_set.add(mylist);
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