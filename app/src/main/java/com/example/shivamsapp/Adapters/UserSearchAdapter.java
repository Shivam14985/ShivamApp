package com.example.shivamsapp.Adapters;

import static com.example.shivamsapp.R.color.black;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shivamsapp.Models.FriendsModel;
import com.example.shivamsapp.Models.NotificationTabModel;
import com.example.shivamsapp.Models.Users;
import com.example.shivamsapp.R;
import com.example.shivamsapp.databinding.FragmentSearchBinding;
import com.example.shivamsapp.databinding.SerachUserSampleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.viewHolder>{
    Context context;
    ArrayList<Users>list;

    public UserSearchAdapter(Context context, ArrayList<Users> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.serach_user_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Users users=list.get(position);
        Picasso.get().load(users.getProfile())
                .placeholder(R.drawable.profile_user)
                .into(holder.binding.profile);
        holder.binding.Name.setText(users.getName());
        holder.binding.profession.setText(users.getProfession());

        FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(users.getUserUid())
                                .child("Followers")
                                        .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                     if (snapshot.exists()){
                         holder.binding.Followbtn.setText("Follwing");
                         holder.binding.Followbtn.setEnabled(false);
                         holder.binding.Followbtn.setTextColor(context.getResources().getColor(R.color.black));

                     }
                     else {
                         holder.binding.Followbtn.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 FriendsModel friendsModel=new FriendsModel();
                                 friendsModel.setFolloweBy(FirebaseAuth.getInstance().getUid());
                                 friendsModel.setFollowedAt(new Date().getTime());
                                 list.clear();

                                 FirebaseDatabase.getInstance().getReference()
                                         .child("Users")
                                         .child(users.getUserUid())
                                         .child("Followers")
                                         .child(FirebaseAuth.getInstance().getUid())
                                         .setValue(friendsModel)
                                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void unused) {
                                                 list.clear();
                                                 FirebaseDatabase.getInstance().getReference().child("Users")
                                                         .child(users.getUserUid())
                                                         .child("followerCount")
                                                         .setValue(users.getFollowerCount()+1)
                                                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                             @Override
                                                             public void onSuccess(Void unused) {
                                                                 holder.binding.Followbtn.setText("Follwing");
                                                                 holder.binding.Followbtn.setTextColor(context.getResources().getColor(R.color.black));
                                                                 holder.binding.Followbtn.setEnabled(false);
                                                                 Toast.makeText(context, "Following"+""+users.getName(), Toast.LENGTH_SHORT).show();

                                                                 NotificationTabModel notificationTabModel=new NotificationTabModel();
                                                                 notificationTabModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                 notificationTabModel.setNotificationAt(new Date().getTime());
                                                                 notificationTabModel.setType("Follow");
                                                                 FirebaseDatabase.getInstance().getReference()
                                                                         .child("Notification")
                                                                         .child(users.getUserUid())
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

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        SerachUserSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding=SerachUserSampleBinding.bind(itemView);
        }
    }
}