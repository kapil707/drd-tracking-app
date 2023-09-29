package com.drd.drdtrackingapp.ui.yourlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.drd.drdtrackingapp.UserSessionManager;
import com.drd.drdtrackingapp.databinding.FragmentYourListBinding;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

public class YourListFragment extends Fragment {

    private FragmentYourListBinding binding;

    UserSessionManager session;
    String user_altercode = "";

    ListView listview;
    YourList_Adapter adapter;
    List<YourList_get_or_set> movieList = new ArrayList<YourList_get_or_set>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentYourListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        session = new UserSessionManager(this.getContext());
        HashMap<String, String> user = session.getUserDetails();
        user_altercode = user.get(UserSessionManager.KEY_USERALTERCODE);

//        final TextView textView = binding.textYourList;
//        textView.setText("Login User : " +user_altercode);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}