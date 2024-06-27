package com.example.shivamsapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shivamsapp.Adapters.FriendsAdapter;
import com.example.shivamsapp.Models.FriendsModel;
import com.example.shivamsapp.Models.Users;
import com.example.shivamsapp.R;
import com.example.shivamsapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    ArrayList<FriendsModel> list;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    private FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(getContext());
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        database.getReference()
                .child("Users")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Users users = snapshot.getValue(Users.class);
                            Picasso.get().load(users.getBackgroundProfile())
                                    .placeholder(R.drawable.profile_user)
                                    .into(binding.backgroundImage);
                            Picasso.get().load(users.getProfile())
                                    .placeholder(R.drawable.profile_user)
                                    .into(binding.profileProimg);
                            binding.NAme.setText(users.getName());
                            binding.Profession.setText(users.getProfession());
                            binding.followers.setText(users.getFollowerCount() + "");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        list = new ArrayList<>();


        FriendsAdapter adapter = new FriendsAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.FriendRecycler.setLayoutManager(linearLayoutManager);
        binding.FriendRecycler.setAdapter(adapter);

        database.getReference().child("Users")
                .child(auth.getUid())
                .child("Followers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            FriendsModel friendsModel = dataSnapshot.getValue(FriendsModel.class);
                            list.add(friendsModel);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Uploading background profile
        binding.Changebgprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });
        binding.ChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 12);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri uri = data.getData();
                binding.backgroundImage.setImageURI(uri);
                progressDialog.setMessage("Updaing Cover Photo");
                progressDialog.show();
                final StorageReference reference = storage.getReference()
                        .child("BackgroundProfile")
                        .child(FirebaseAuth.getInstance().getUid());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Cover Photo Saved", Toast.LENGTH_SHORT).show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("Users")
                                        .child(auth.getUid()).child("BackgroundProfile")
                                        .setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        } else {
            if (requestCode == 12 && resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    binding.profileProimg.setImageURI(uri);
                    progressDialog.setMessage("Updating Profile");
                    progressDialog.show();
                    final StorageReference reference = storage.getReference()
                            .child("Profile")
                            .child(FirebaseAuth.getInstance().getUid());
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Profile Photo Saved", Toast.LENGTH_SHORT).show();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child("Users")
                                            .child(auth.getUid()).child("Profile")
                                            .setValue(uri.toString());
                                }
                            });
                        }
                    });
                }
            }
        }
    }
}