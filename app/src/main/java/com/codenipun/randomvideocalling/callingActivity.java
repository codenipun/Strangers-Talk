package com.codenipun.randomvideocalling;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codenipun.randomvideocalling.Models.UserModel;
import com.codenipun.randomvideocalling.Models.javaInterfaces;

import com.codenipun.randomvideocalling.databinding.ActivityCallingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

public class callingActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityCallingBinding binding;
    String uniqueId = "";

    DatabaseReference firebaseRef;
    String username = "", friendsUsername = "";

    boolean isPeerConnected = false; // by default it is not connected;

    boolean isAudio = true; // to check weather audio is enabled or not

    boolean isVideo = true; // to check weather video is enabled or not

    String createdBy, incoming;

    boolean pageExit = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

//        Glide.with(this).load(auth.getCurrentUser().getPhotoUrl()).into(binding.peerImage);

        uniqueId = auth.getUid();   // Another way to get unique id is to make unique id function which return UUID which works absolutely same

        firebaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        username = getIntent().getStringExtra("username");

        incoming = getIntent().getStringExtra("incoming");

        createdBy = getIntent().getStringExtra("createdBy");

//        friendsUsername = "";
//        if(incoming.equalsIgnoreCase(friendsUsername)){
//            friendsUsername = incoming;
//        }

        friendsUsername = incoming;


        setUpWebView();

        // lets setup our video button state
        binding.videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVideo = !isVideo;
                callJavascriptFunction("javascript:toggleVideo(\""+isVideo+"\")");
                if(isVideo){
                    binding.videoBtn.setImageResource(R.drawable.btn_video_normal);
                } else {
                    binding.videoBtn.setImageResource(R.drawable.btn_video_muted);
                }
            }
        });

        // lets setup mic and its button state
        binding.micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAudio = !isAudio;
                callJavascriptFunction("javascript:toggleAudio(\""+isAudio+"\")");
                if(isAudio){
                    binding.micBtn.setImageResource(R.drawable.btn_unmute_normal);
                } else {
                    binding.micBtn.setImageResource(R.drawable.btn_mute_normal);
                }
            }
        });

        binding.callEndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void setUpWebView(){
        binding.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }
        });


        //enabling javascript and disabling media gesture of webView
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webView.addJavascriptInterface(new javaInterfaces(this),"Android");

        loadVideoCall();
    }
    public void loadVideoCall(){
        // here we will load the file call.html
        String filepath = "file:android_asset/call.html";

        //now this filepath in webView
        binding.webView.loadUrl(filepath);

        // now we need to make another webView client to initialise the peers
        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                initializePeers();

            }
        });
    }
    void initializePeers(){

        callJavascriptFunction("javascript:init(\"" + uniqueId + "\")");

        // now we need to add a check here to identify weather the room has been created by this user or his friend
        // if he has created the room then must update the connection id otherwise his friend will update it
        // because we need to connect both the peer with the same connection id only then our peer connection created
        // so simple like that
        if(createdBy.equalsIgnoreCase(username)){
            if(pageExit){
                return;
            }
            firebaseRef.child(username)
                       .child("connId")
                       .setValue(uniqueId);
            firebaseRef.child(username)
                       .child("isAvailable")
                       .setValue(true);

                // Lets set the details of connected user
                setConnectedUsersDetails();

            binding.blurBackground.setVisibility(View.GONE);
            binding.loadingAnimation.setVisibility(View.GONE);
            binding.controls.setVisibility(View.VISIBLE);
        }else{
            // friend is updating the connection id

            // we are making to delayed by 2 sec so that thing will update and we don't have to face errors
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    friendsUsername = createdBy;

                    setConnectedUsersDetails();

                    FirebaseDatabase.getInstance().getReference()
                            .child("Profiles")
                            .child(friendsUsername)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    UserModel user = snapshot.getValue(UserModel.class);
                                    Glide.with(callingActivity.this).load(user.getProfile()).into(binding.peerImage);
                                    binding.peerName.setText(user.getName());
                                    binding.peerLocation.setText(user.getCity());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    FirebaseDatabase.getInstance().getReference()
                           .child("Users")
                           .child(friendsUsername)
                           .child("connId").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getValue() != null){
                                    sendCallRequest();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }, 3000);

        }

    }
    public void onPeerConnected(){
        isPeerConnected = true;
    }

    void sendCallRequest(){
        if(!isPeerConnected){
            Toast.makeText(this, "You are not connected, please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }else{
            listenConnId();
        }
    }
    void listenConnId(){
        firebaseRef.child(friendsUsername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()==null){
                    return;
                }else{

                    binding.blurBackground.setVisibility(View.GONE);
                    binding.loadingAnimation.setVisibility(View.GONE);
                    binding.controls.setVisibility(View.VISIBLE);

                    String connId = snapshot.getValue(String.class);

                    callJavascriptFunction("javascript:startCall(\""+connId+"\")");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // For calling javascript function in android we need to make a separate function
    void callJavascriptFunction(String function){
        binding.webView.post(new Runnable() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                binding.webView.evaluateJavascript(function, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageExit = true;
        firebaseRef.child(createdBy).setValue(null);
        finish();
    }


    void setConnectedUsersDetails(){
        FirebaseDatabase.getInstance().getReference()
                .child("Profiles")
                .child(friendsUsername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);

                        Glide.with(callingActivity.this).load(user.getProfile()).into(binding.peerImage);

                        binding.peerName.setText(user.getName());
                        binding.peerLocation.setText(user.getCity());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
    }
}