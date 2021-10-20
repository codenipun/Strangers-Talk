package com.codenipun.randomvideocalling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codenipun.randomvideocalling.Models.javaInterfaces;
import com.codenipun.randomvideocalling.databinding.ActivityCallingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        uniqueId = auth.getUid();   // Another way to get unique id is to make unique id function which return UUID which works absolutely same

        firebaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        username = getIntent().getStringExtra(username);

        incoming = getIntent().getStringExtra(incoming);

        createdBy = getIntent().getStringExtra(createdBy);

        friendsUsername = "";
        if(incoming.equalsIgnoreCase(friendsUsername)){
            friendsUsername = incoming;
        }

        setUpWebView();

        // lets setup our mic button
        binding.micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAudio = !isAudio;
                callJavascriptFunction("javascript:toggleAudio(\""+isAudio+"\"");

                //lets change the image for unmute
                if(isAudio) {
                    binding.micBtn.setImageResource(R.drawable.btn_unmute_normal);
                }else{
                    binding.micBtn.setImageResource(R.drawable.btn_mute_normal);
                }
            }
        });

        // lets setup video and its button
        binding.videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideo = !isVideo;
                callJavascriptFunction("javascript:toggleAudio(\""+isVideo+"\"");

                //lets change the image for unmute
                if(isVideo) {
                    binding.micBtn.setImageResource(R.drawable.btn_video_normal);
                }else{
                    binding.micBtn.setImageResource(R.drawable.btn_video_muted);
                }
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void setUpWebView(){
        binding.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);
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
        String filepath = "file:android_assets/call.html";

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
    public void initializePeers(){
           callJavascriptFunction("javascript:init(\"" + uniqueId + "\"");
    }

    // For calling javascript function in android we need to make a seperate function
    void callJavascriptFunction(String function){
        binding.webView.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                binding.webView.evaluateJavascript(function, null);
            }
        });
    }
}