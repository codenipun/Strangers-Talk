package com.codenipun.randomvideocalling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codenipun.randomvideocalling.databinding.ActivityStartingBinding;
import com.google.firebase.auth.FirebaseAuth;

public class startingActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityStartingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();


        binding.getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auth.getCurrentUser()==null) startActivity(new Intent(startingActivity.this, loginActivity.class));
                else startActivity(new Intent(startingActivity.this, MainActivity.class));
                finishAffinity();
            }
        });
    }
}