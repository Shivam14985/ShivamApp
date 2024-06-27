package com.example.shivamsapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivamsapp.Adapters.StoryAdapter;
import com.example.shivamsapp.Models.StoryModel;
import com.example.shivamsapp.Models.UsersStory;
import com.example.shivamsapp.R;
import com.example.shivamsapp.databinding.FragmentStoryBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class StoryFragment extends Fragment {
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    Uri uri;
    RecyclerView storyRv;
    ArrayList<StoryModel>list;
    private FragmentStoryBinding binding;
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Uploading Story");
//        progressDialog.setIndeterminateDrawable(R.drawable.baseline_history_toggle_off_24);
        progressDialog.setCancelable(false);

        storyRv=root.findViewById(R.id.story_recycler);
        list=new ArrayList<>();

        StoryAdapter adapter=new StoryAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        storyRv.setLayoutManager(linearLayoutManager);
        storyRv.setAdapter(adapter);

        database.getReference().child("Stories")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    list.clear();
                                    for (DataSnapshot storiesSnapshot:snapshot.getChildren())
                                    {
                                        StoryModel storyModel=new StoryModel();
                                        storyModel.setStoryBy(storiesSnapshot.getKey());
                                        storyModel.setStotoryAt(storiesSnapshot.child("StroyBy").getValue(Long.class));

                                        ArrayList<UsersStory>usersStoryArrayList=new ArrayList<>();
                                        for (DataSnapshot snapshot1:storiesSnapshot.child("UersStories").getChildren()){
                                            UsersStory usersStory=snapshot1.getValue(UsersStory.class);
                                            usersStoryArrayList.add(usersStory);
                                        }
                                        storyModel.setStories(usersStoryArrayList);
                                        list.add(storyModel);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        binding.uploaadPst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);

            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri=data.getData();
        progressDialog.show();

        final StorageReference reference=storage.getReference()
                .child("Stories").child(FirebaseAuth.getInstance().getUid())
                .child(new Date().getTime()+"");
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                       StoryModel storyModel=new StoryModel();
                       storyModel.setStotoryAt(new Date().getTime());


                        database.getReference()
                                .child("Stories")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child("StroyBy")
                                .setValue(storyModel.getStotoryAt())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                   UsersStory usersStory=new UsersStory(uri.toString(),storyModel.getStotoryAt());
                                   database.getReference().child("Stories")
                                           .child(FirebaseAuth.getInstance().getUid())
                                           .child("UersStories")
                                           .push()
                                           .setValue(usersStory).addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void unused) {
                                                   progressDialog.dismiss();
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}