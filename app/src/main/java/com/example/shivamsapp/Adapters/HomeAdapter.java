package com.example.shivamsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivamsapp.CommentActivity;
import com.example.shivamsapp.Models.HomeModel;
import com.example.shivamsapp.Models.NotificationTabModel;
import com.example.shivamsapp.Models.Users;
import com.example.shivamsapp.R;
import com.example.shivamsapp.databinding.HomeSampleBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.viewHolder> {

    ArrayList<HomeModel> list;
    Context context;

    public HomeAdapter(ArrayList<HomeModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_sample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        HomeModel model = list.get(position);
        Picasso.get().load(model.getPostImg())
                .placeholder(R.drawable.profile_user)
                .into(holder.binding.posted);
        holder.binding.likeNum.setText(model.getPostLike() + "");
        holder.binding.commnetsnum.setText(model.getCommentsNumber() + "");
        String time = TimeAgo.using(model.getPostedAt());
        holder.binding.timeNoti.setText(time);
        String description = model.getPostDescription();
        if (description.equals("")) {
            holder.binding.Discription.setVisibility(View.GONE);
        } else {
            holder.binding.Discription.setText(model.getPostDescription());
        }
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getPosteBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.profile_user)
                                .into(holder.binding.Profile);
                        holder.binding.profileName.setText(user.getName());
                        holder.binding.About.setText(user.getProfession());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

        FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(model.getPostID())
                .child("LikedBy")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.like.setBackgroundResource(R.drawable.hearted);
                        } else {
                            holder.binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Posts")
                                            .child(model.getPostID())
                                            .child("LikedBy")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Posts")
                                                            .child(model.getPostID())
                                                            .child("postLike")
                                                            .setValue(model.getPostLike() + 1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
//                                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hearted, 0, 0, 0);
                                                                    holder.binding.like.setBackgroundResource(R.drawable.hearted);
                                                                    NotificationTabModel notificationTabModel = new NotificationTabModel();
                                                                    notificationTabModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notificationTabModel.setNotificationAt(new Date().getTime());
                                                                    notificationTabModel.setPostId(model.getPostID());
                                                                    notificationTabModel.setPostedBy(notificationTabModel.getPostedBy());
                                                                    notificationTabModel.setType("Like");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Notification")
                                                                            .child(model.getPosteBy())
                                                                            .push().setValue(notificationTabModel);
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.binding.Comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sending firebase data from this activity to comment activity
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", model.getPostID());
                intent.putExtra("postedBy", model.getPosteBy());
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
        holder.binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.binding.more);
                popupMenu.getMenuInflater().inflate(R.menu.moreoption, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.EditPost) {
                            Toast.makeText(context, "Post Edited", Toast.LENGTH_SHORT).show();
                        } else if (item.getItemId() == R.id.DeletePost) {
                            if (model.getPosteBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Posts")
                                        .child(model.getPostID())
                                        .setValue(null);
                                Toast.makeText(context, "Your Post Deleted", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(context, "This is not your Post", Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            Toast.makeText(context, "Post Downloaded", Toast.LENGTH_SHORT).show();
                        }
                            return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        HomeSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomeSampleBinding.bind(itemView);

        }
    }
}
