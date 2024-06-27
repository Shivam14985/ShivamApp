package com.example.shivamsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivamsapp.CommentActivity;
import com.example.shivamsapp.Models.NotificationTabModel;
import com.example.shivamsapp.Models.Users;
import com.example.shivamsapp.R;
import com.example.shivamsapp.databinding.NotificationTabRecyclerViewBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

    ArrayList<NotificationTabModel> list;
    Context context;

    public NotificationAdapter(ArrayList<NotificationTabModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_tab_recycler_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationTabModel model = list.get(position);
        String type = model.getType();

        if (!model.getNotificationBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.binding.ClickNotificatio.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        Picasso.get()
                                .load(users.getProfile())
                                .placeholder(R.drawable.profile_user)
                                .into(holder.binding.notiProfile);
                        if (type.equals("Like")) {
                            holder.binding.commentNotification.setText(Html.fromHtml("<b>" + users.getName() + "</b>" + " liked your post"));
                        } else if (type.equals("Comment")) {
                            holder.binding.commentNotification.setText(Html.fromHtml("<b>" + users.getName()+"</b>" +" commented your post"));
                        } else {
                            holder.binding.commentNotification.setText(Html.fromHtml("<b>" + users.getName()+ "</b>" + " started following you"));
                        }
                        String time = TimeAgo.using(model.getNotificationAt());
                        holder.binding.timeNoti.setText(time);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });}
        else {holder.binding.ClickNotificatio.setVisibility(View.GONE);}
        holder.binding.ClickNotificatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("Comment")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Notification")
                            .child(model.getPostedBy())
                            .child(model.getNotificationId())
                            .child("checkOpen")
                            .setValue(true);
                    holder.binding.ClickNotificatio.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", model.getPostId());
                    intent.putExtra("postedBy", model.getPostedBy());
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }


            }
        });
        Boolean checkOpen=model.isCheckOpen();
        if (checkOpen==true){
            holder.binding.ClickNotificatio.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else {}
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        NotificationTabRecyclerViewBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationTabRecyclerViewBinding.bind(itemView);
        }
    }
}
