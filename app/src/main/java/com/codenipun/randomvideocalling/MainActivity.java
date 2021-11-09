package com.codenipun.randomvideocalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codenipun.randomvideocalling.Models.UserModel;
import com.codenipun.randomvideocalling.R;
import com.codenipun.randomvideocalling.databinding.ActivityMainBinding;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    long coins = 0;
    
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    ActivityMainBinding binding;

    String [] permission = new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    int requestCode = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.group.setVisibility(View.INVISIBLE);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();


        //For google AdMob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        firebaseDatabase.getReference().child("Profiles").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                binding.progressBar.setVisibility(View.GONE);
                binding.group.setVisibility(View.VISIBLE);
                UserModel userModel = snapshot.getValue(UserModel.class);

                coins = userModel.getCoins();

                binding.coins.setText(String.valueOf(coins));
                Glide.with(MainActivity.this).load(userModel.getProfile()).into(binding.img);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
//        Log.d("value of coins", "value ", coins)
        binding.findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionGranted()) {
                    if (coins > 10) {
                        // update coins on every video call
                        coins -= 10;
                        firebaseDatabase.getReference()
                                .child("Profiles")
                                .child(currentUser.getUid())
                                .child("coins")
                                .setValue(coins);
                        startActivity(new Intent(MainActivity.this, connectingActivity.class));
                        // Toast.makeText(MainActivity.this, "Finding Match.....", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Insufficient coins", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    askPermission();
                }
            }
        });
        binding.wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RewardedAdsActivity.class));
            }
        });
    }


    void askPermission(){
        ActivityCompat.requestPermissions(this, permission, requestCode);
    }

    private boolean isPermissionGranted(){
        for(String perm : permission){
            if(ActivityCompat.checkSelfPermission(this, perm)!=PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}