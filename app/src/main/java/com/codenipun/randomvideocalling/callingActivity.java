package com.codenipun.randomvideocalling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codenipun.randomvideocalling.databinding.ActivityCallingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class callingActivity extends AppCompatActivity {
    ActivityCallingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}