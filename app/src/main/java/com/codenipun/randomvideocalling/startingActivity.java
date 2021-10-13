package com.codenipun.randomvideocalling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codenipun.randomvideocalling.databinding.ActivityStartingBinding;

public class startingActivity extends AppCompatActivity {
    ActivityStartingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(startingActivity.this, loginActivity.class));
                finishAffinity();
            }
        });
    }
}