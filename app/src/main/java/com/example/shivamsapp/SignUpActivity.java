package com.example.shivamsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.shivamsapp.Models.Users;
import com.example.shivamsapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    String[] items = {"Accountant","Actor","Actress","Architect","Artist", "Astronomer","Bartenders",
            "Baker","Bricklayer"," Driver","Businessman","Dancer", "Driver", "Butcher", "Carpenter",
            "Chef","Cook","Cleaner", "Dentist",
            "Designer", "Doctor","Electrician","Engineer", "Factory worker","Farmer",
            "Fire fighter","Fisherman","Florist", "Gardener", "Hairdresser", "Journalist", "Judge",
            "Lawyer", "Lecturer", "Librarian","Mechanic","Musician", "Model","Newsreader", "Nurse","Painter"
            ,"Pharmacist", "Photographer" ,"Pilot", "Plumber" ,"Politician" ,"Police","Postman",
            "Receptionist", "Scientist", "Secretary", "Shopkeeper","Singer","Student", "assistant", "Soldier", "Tailor"
            ,"Taxi driver", "Teacher","Waiter", "Waitress"};
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Registering........");
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.register));

        adapter = new ArrayAdapter<String>(this, R.layout.professions, items);
        binding.EtProfession.setAdapter(adapter);
        binding.EtProfession.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(SignUpActivity.this,item, Toast.LENGTH_SHORT).show();
            }
        });

        //remove toolbar
        getSupportActionBar().hide();

        binding.goToRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);

                startActivity(intent);
            }
        });
        //signing up
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.EtEmail.getText().toString().isEmpty() && !binding.EtPassord.getText().toString().isEmpty() && !binding.EtName.getText().toString().isEmpty() && !binding.EtProfession.getText().toString().isEmpty()) {
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(binding.EtEmail.getText().toString(), binding.EtPassord.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                //store data in realtime database
                                Users users = new Users(binding.EtName.getText().toString(), binding.EtProfession.getText().toString(), binding.EtEmail.getText().toString(), binding.EtPassord.getText().toString());
                                String id = task.getResult().getUser().getUid();
                                database.getReference().child("Users").child(id).setValue(users);
                                Toast.makeText(SignUpActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    new AlertDialog.Builder(SignUpActivity.this).setIcon(R.drawable.warning)
                            .setTitle("Warning!!!").setMessage("Please Fill Above Details").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        });
    }

    //Double press back to exit app
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}