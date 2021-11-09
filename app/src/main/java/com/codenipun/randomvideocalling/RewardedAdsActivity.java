package com.codenipun.randomvideocalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codenipun.randomvideocalling.databinding.ActivityRewardedAdsBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RewardedAdsActivity extends AppCompatActivity {
    private RewardedAd mRewardedAd;
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

        loadAds();

        binding.video1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAd(10);
            }
        });
        binding.video2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAd(50);
            }
        });
        binding.video3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAd(100);
            }
        });
        binding.video4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAd(200);
            }
        });
        binding.video5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAd(500);
            }
        });
    }

    void loadAds(){
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                    }
                });
    }

    void showAd(int n){
        if (mRewardedAd != null) {
            Activity activityContext = RewardedAdsActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    coins+=n;

                    loadAds();

                    firebaseDatabase.getReference().child("Profiles").child(currentUid).child("coins")
                            .setValue(coins);

                    if(n==10) binding.video1Btn.setImageResource(R.drawable.ic_check);
                    if(n==50) binding.video2Btn.setImageResource(R.drawable.ic_check);
                    if(n==100) binding.video3Btn.setImageResource(R.drawable.ic_check);
                    if(n==200) binding.video4Btn.setImageResource(R.drawable.ic_check);
                    if(n==500) binding.video5Btn.setImageResource(R.drawable.ic_check);
                }
            });
        } else {
        }
    }
}
