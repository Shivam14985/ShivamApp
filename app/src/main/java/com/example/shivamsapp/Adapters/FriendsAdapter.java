package com.example.shivamsapp.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivamsapp.Models.FriendsModel;
import com.example.shivamsapp.Models.Users;
import com.example.shivamsapp.R;
import com.example.shivamsapp.databinding.FriendsSampleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.viewHolder>{

ArrayList<FriendsModel>list;
Context context;
Dialog progressDialog;

    public FriendsAdapter(ArrayList<FriendsModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.friends_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FriendsModel model=list.get(position);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getFolloweBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users=snapshot.getValue(Users.class);
                        Picasso.get()
                                .load(users.getProfile())
                                .placeholder(R.drawable.profile_user)
                                .into(holder.binding.friendsProfile);
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
        FriendsSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
        binding=FriendsSampleBinding.bind(itemView);
        }
    }
}
