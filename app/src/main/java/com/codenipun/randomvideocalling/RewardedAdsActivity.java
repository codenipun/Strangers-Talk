package com.codenipun.randomvideocalling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codenipun.randomvideocalling.databinding.ActivityRewardedAdsBinding;

public class RewardedAdsActivity extends AppCompatActivity {
    ActivityRewardedAdsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRewardedAdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}