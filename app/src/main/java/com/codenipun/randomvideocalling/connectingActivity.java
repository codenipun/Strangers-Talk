package com.codenipun.randomvideocalling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codenipun.randomvideocalling.databinding.ActivityConnectingBinding;

public class connectingActivity extends AppCompatActivity {
    ActivityConnectingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnectingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}