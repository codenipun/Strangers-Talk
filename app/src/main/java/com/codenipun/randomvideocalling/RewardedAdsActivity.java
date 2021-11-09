package com.codenipun.randomvideocalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.codenipun.randomvideocalling.databinding.ActivityRewardedAdsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RewardedAdsActivity extends AppCompatActivity {
    ActivityRewardedAdsBinding binding;
    FirebaseDatabase firebaseDatabase;
    String currentUid;
    int coins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRewardedAdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUid = FirebaseAuth.getInstance().getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseDatabase.getReference().child("Profiles").child(currentUid).child("coins")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               coins = snapshot.getValue(Integer.class);
               binding.currentCoins.setText(String.valueOf(coins));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
}