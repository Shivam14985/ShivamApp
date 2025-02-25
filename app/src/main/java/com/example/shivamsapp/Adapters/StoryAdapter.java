package com.example.shivamsapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivamsapp.Models.StoryModel;
import com.example.shivamsapp.Models.Users;
import com.example.shivamsapp.Models.UsersStory;
import com.example.shivamsapp.R;
import com.example.shivamsapp.databinding.SampleStoryBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;


public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder> {

    ArrayList<StoryModel> list;
    Context context;

    public StoryAdapter(ArrayList<StoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_story, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        StoryModel storyModel = list.get(position);

        if (storyModel.getStories().size() > 0) {
            UsersStory laststories = storyModel.getStories().get(storyModel.getStories().size() - 1);
            Picasso.get()
                    .load(laststories.getStoryImage())
                    .into(holder.binding.Story);
            holder.binding.circularStatusView.setPortionsCount(storyModel.getStories().size());

            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(storyModel.getStoryBy())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users users = snapshot.getValue(Users.class);
                            Picasso.get().load(users.getProfile())
                                    .placeholder(R.drawable.profile_user)
                                    .into(holder.binding.profileImage);

                            String time = TimeAgo.using(storyModel.getStotoryAt());
                            if (storyModel.getStoryBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                holder.binding.Name.setText("My Story");

                            } else {
                                holder.binding.Name.setText(users.getName());
                            }
                            holder.binding.timeStory.setText(time);

                            //To View Story
                            holder.binding.Story.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ArrayList<MyStory> myStories = new ArrayList<>();

                                    for (UsersStory story : storyModel.getStories()) {
                                        myStories.add(new MyStory(
                                                story.getStoryImage()
                                        ));
                                    }
                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories) // Required
                                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                            .setTitleText(users.getName()) // Default is Hidden
//                                        .setSubtitleText() // Default is Hidden
                                            .setTitleLogoUrl(users.getProfile()) // Default is Hidden
                                            .setStoryClickListeners(new StoryClickListeners() {
                                                @Override
                                                public void onDescriptionClickListener(int position) {
                                                    //your action
                                                }

                                                @Override
                                                public void onTitleIconClickListener(int position) {
                                                    //your action
                                                }
                                            }) // Optional Listeners
                                            .build() // Must be called before calling show method
                                            .show();

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        SampleStoryBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleStoryBinding.bind(itemView);

        }
    }
}
