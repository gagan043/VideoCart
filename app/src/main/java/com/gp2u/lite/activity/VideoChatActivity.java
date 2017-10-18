package com.gp2u.lite.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.gp2u.lite.R;

public class VideoChatActivity extends AppCompatActivity {

    private static String TAG = "VideoChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_chat);

        Log.d(TAG ,getIntent().getExtras().getString("roomname"));
    }

}