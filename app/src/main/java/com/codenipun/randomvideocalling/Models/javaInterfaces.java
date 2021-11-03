package com.codenipun.randomvideocalling.Models;

import android.webkit.JavascriptInterface;

import com.codenipun.randomvideocalling.callingActivity;

public class javaInterfaces {
    callingActivity callActivity;

    //constructor
    public javaInterfaces(callingActivity callActivity){
        this.callActivity = callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected(){
        callActivity.onPeerConnected();
    }
}
