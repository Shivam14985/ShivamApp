package com.example.shivamsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.shivamsapp.Models.HomeModel;
import com.example.shivamsapp.Models.Users;
import com.example.shivamsapp.databinding.ActivityAddPostBinding;
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

import java.util.Date;

public class AddPostActivity extends AppCompatActivity {
    ActivityAddPostBinding binding;
    Uri uri;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //remove toolbar
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog=new ProgressDialog(AddPostActivity.this);
        progressDialog.setMessage("Post Uploading");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.baseline_history_toggle_off_24));


        binding.postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });
//to show profile, name , profession in post
        database.getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Users users = snapshot.getValue(Users.class);
                            Picasso.get()
                                    .load(users.getProfile())
                                    .placeholder(R.drawable.profile_user)
                                    .into(binding.profile);
                            binding.Name.setText(users.getName());
                            binding.profession.setText(users.getProfession());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //postbutton
        binding.PostButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final StorageReference reference = storage.getReference()
                        .child("Posts")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime() + "");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HomeModel model=new HomeModel();
                                model.setPostImg(uri.toString());
                                model.setPosteBy(FirebaseAuth.getInstance().getUid());
                                model.setPostDescription(binding.EditboxMind.getText().toString());
                                model.setPostedAt(new Date().getTime());

                                database.getReference().child("Posts")
                                        .push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddPostActivity.this, "Posted Succesfully", Toast.LENGTH_SHORT).show();
                                                Intent intent =new Intent(AddPostActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null) {

            uri = data.getData();
            binding.Postimage.setImageURI(uri);
            binding.Postimage.setVisibility(View.VISIBLE);

            binding.PostButtn.setBackgroundDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.following));
            binding.PostButtn.setTextColor(getBaseContext().getResources().getColor(R.color.white));
            binding.PostButtn.setEnabled(true);
        } else {
            binding.PostButtn.setBackgroundDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.follow));
            binding.PostButtn.setTextColor(getBaseContext().getResources().getColor(R.color.black));
            binding.PostButtn.setEnabled(false);
        }
    }
}