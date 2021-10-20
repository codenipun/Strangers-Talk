package com.codenipun.randomvideocalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.codenipun.randomvideocalling.databinding.ActivityConnectingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class connectingActivity extends AppCompatActivity {
    ActivityConnectingBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;

    boolean isOkay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnectingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Now we need to create random room on the firebase, so that whenever someone request then they will be able to add in it
        // We need to search over firebase weather some user already requesting , if he was requesting then will join him otherwise we will
        // create our own room

        // whenever we need to search over firbase we will first search the base of it child by order by child
        String username = auth.getUid();

        database.getReference()
                .child("Users")
                .orderByChild("Status")
                .equalTo(0)
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){
                            isOkay = true;

                            // Room is Available
                            for(DataSnapshot childSnap : snapshot.getChildren()){
                                database.getReference()
                                        .child("Users")
                                        .child(childSnap.getKey())
                                        .child("incoming")
                                        .setValue(username);
                                database.getReference()
                                        .child("Users")
                                        .child(childSnap.getKey())
                                        .child("Status")
                                        .setValue(1);

                                String incoming = childSnap.child("incoming").getValue(String.class);
                                String createdBy = childSnap.child("createdBy").getValue(String.class);
                                Boolean isAvailable = childSnap.child("isAvailable").getValue(Boolean.class);

                                Intent intent = new Intent(connectingActivity.this, callingActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("incoming", incoming);
                                intent.putExtra("isAvailable", isAvailable);
                                intent.putExtra("createdBy", createdBy);

                                startActivity(intent);
                                finish();
                            }
                            Log.e("err", "room is available");
                        }else{
                            // Room is not Available and we need to create one for others to join
                            // Now to store data in firebase we have hashmap for that

                            HashMap<String , Object> room = new HashMap<>();
                            room.put("incoming", username);
                            room.put("createdBy", username);
                            room.put("isAvailable", true);
                            room.put("Status", 0);

                            database.getReference()
                                    .child("Users")
                                    .child(username)
                                    .setValue(room)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference()
                                            .child("Users")
                                            .child(username).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            if(snapshot.child("Status").exists()){
                                                if(snapshot.child("Status").getValue(Integer.class)==1){
                                                    if(isOkay){
                                                        return;
                                                    }

                                                    isOkay = true;
                                                    String incoming = snapshot.child("incoming").getValue(String.class);
                                                    String createdBy = snapshot.child("createdBy").getValue(String.class);
                                                    Boolean isAvailable = snapshot.child("isAvailable").getValue(Boolean.class);

                                                    Intent intent = new Intent(connectingActivity.this, callingActivity.class);
                                                    intent.putExtra("username", username);
                                                    intent.putExtra("incoming", incoming);
                                                    intent.putExtra("isAvailable", isAvailable);
                                                    intent.putExtra("createdBy", createdBy);

                                                    startActivity(intent);
                                                    finish();

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }
}