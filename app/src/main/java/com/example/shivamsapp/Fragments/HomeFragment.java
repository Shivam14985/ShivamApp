package com.example.shivamsapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivamsapp.Adapters.HomeAdapter;
import com.example.shivamsapp.Models.HomeModel;
import com.example.shivamsapp.R;
import com.example.shivamsapp.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    RecyclerView homeRecycler;
    ArrayList<HomeModel> list;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressBar progressBar;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        homeRecycler = view.findViewById(R.id.Home_recycler);
        list = new ArrayList<>();

        HomeAdapter homeAdapter = new HomeAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        homeRecycler.setLayoutManager(linearLayoutManager);
        homeRecycler.setAdapter(homeAdapter);

        database.getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HomeModel homeModel = dataSnapshot.getValue(HomeModel.class);
                    homeModel.setPostID(dataSnapshot.getKey());
                    list.add(homeModel);
                }
                homeAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}