package com.drd.drdtrackingapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.drd.drdtrackingapp.UserSessionManager;
import com.drd.drdtrackingapp.databinding.FragmentHomeBinding;

import java.util.HashMap;

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

        final TextView textView = binding.textHome;
        textView.setText("Login User : " +user_altercode);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}