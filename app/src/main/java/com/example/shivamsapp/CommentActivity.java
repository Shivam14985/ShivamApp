package com.example.shivamsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.shivamsapp.Adapters.CommentsAdapter;
import com.example.shivamsapp.Models.Comments;
import com.example.shivamsapp.Models.HomeModel;
import com.example.shivamsapp.Models.NotificationTabModel;
import com.example.shivamsapp.Models.Users;
import com.example.shivamsapp.databinding.ActivityCommentBinding;
import com.example.shivamsapp.databinding.HomeSampleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    ActivityCommentBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Intent intent;
    String postId;
    String postedBy;
   ArrayList<Comments>list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        database.getReference()
                .child("Posts")
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HomeModel homeModel=snapshot.getValue(HomeModel.class);
                        Picasso.get()
                                .load(homeModel.getPostImg())
                                .placeholder(R.drawable.profile_user)
                                .into(binding.PostImg);
                        binding.Description.setText(homeModel.getPostDescription());
                        binding.likeNum.setText(homeModel.getPostLike()+"");
                        binding.commnetsnum.setText(homeModel.getCommentsNumber()+"");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        database.getReference()
                .child("Users")
                .child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users=snapshot.getValue(Users.class);
                        Picasso.get()
                                .load(users.getProfile())
                                .placeholder(R.drawable.profile_user)
                                .into(binding.ProfileImage);
                        binding.UserName.setText(users.getName());
                        binding.toolname.setText(users.getName()+"'s Post");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.Commentsbyn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.CommensTExt.getText().toString().isEmpty()) {
                    Toast.makeText(CommentActivity.this, "Please Write your comment", Toast.LENGTH_SHORT).show();
                } else {
                    Comments comments = new Comments();
                    comments.setCommentedText(binding.CommensTExt.getText().toString());
                    comments.setCommentedAt(new Date().getTime());
                    comments.setCommentedBy(FirebaseAuth.getInstance().getUid());
                    database.getReference()
                            .child("Posts")
                            .child(postId)
                            .child("Comments")
                            .push()
                            .setValue(comments)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference()
                                            .child("Posts")
                                            .child(postId)
                                            .child("CommentsNumber")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int CommentsNumber = 0;
                                                    if (snapshot.exists()) {
                                                        CommentsNumber = snapshot.getValue(Integer.class);
                                                    }
                                                    database.getReference()
                                                            .child("Posts")
                                                            .child(postId)
                                                            .child("CommentsNumber")
                                                            .setValue(CommentsNumber + 1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    binding.CommensTExt.setText("");
                                                                    Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();


                                                                    NotificationTabModel notificationTabModel = new NotificationTabModel();
                                                                    notificationTabModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notificationTabModel.setNotificationAt(new Date().getTime());
                                                                    notificationTabModel.setPostId(postId);
                                                                    notificationTabModel.setPostedBy(postedBy);
                                                                    notificationTabModel.setType("Comment");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Notification")
                                                                            .child(postedBy)
                                                                            .push()
                                                                            .setValue(notificationTabModel);

                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            });

                }
            }
        });

        CommentsAdapter adapter=new CommentsAdapter(this,list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(false);

        binding.commentsRecycler.setLayoutManager(layoutManager);
        binding.commentsRecycler.setAdapter(adapter);
        database.getReference()
                .child("Posts")
                .child(postId)
                .child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren())
                        {
                            Comments comments=dataSnapshot.getValue(Comments.class);
                            list.add(comments);

                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}