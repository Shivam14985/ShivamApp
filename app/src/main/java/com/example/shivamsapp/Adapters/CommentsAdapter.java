package com.example.shivamsapp.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivamsapp.Models.Comments;
import com.example.shivamsapp.Models.Users;
import com.example.shivamsapp.R;
import com.example.shivamsapp.databinding.CommentssampleBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.viewHolder> {

    Context context;
    ArrayList<Comments> list;
    public CommentsAdapter(Context context, ArrayList<Comments> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.commentssample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Comments comments = list.get(position);

        //setting time
        String text = TimeAgo.using(comments.getCommentedAt());

//        holder.binding.CommensTExte.setText(comments.getCommentedText());
        holder.binding.timeNoti.setText(text);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(comments.getCommentedBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        Picasso.get()
                                .load(users.getProfile())
                                .placeholder(R.drawable.profile_user)
                                .into(holder.binding.profile);
                        holder.binding.CommensTExte.setText(Html.fromHtml("<b>" + users.getName()+"</b>"+  comments.getCommentedText()));


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        CommentssampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CommentssampleBinding.bind(itemView);
        }
    }
}
