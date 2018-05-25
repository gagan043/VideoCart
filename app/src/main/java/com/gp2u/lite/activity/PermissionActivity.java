package com.gp2u.lite.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.gp2u.lite.R;
import com.gp2u.lite.model.Config;
import com.gp2u.lite.model.Global;
import com.pixplicity.easyprefs.library.Prefs;

public class PermissionActivity extends AppCompatActivity {


    public static String TAG = "PermissionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_permission);

    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Prefs.getBoolean(Config.bPermissionAsked,false)) {
            showNext();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Prefs.putBoolean(Config.bPermissionAsked ,true);
        showNext();
    }

    public void onAllow(View view)
    {
        ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.CAMERA ,Manifest.permission.RECORD_AUDIO} ,111);

    }

    public void onSkip(View view)
    {

        Prefs.putBoolean(Config.bPermissionAsked ,true);
        showNext();
    }

    private void gotoRoomEntry()
    {
        Intent intent = new Intent(this ,RoomEntryActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoChat()
    {
        Intent intent = new Intent(this ,VideoChatActivity.class);
        startActivity(intent);
        finish();
    }

    private void showNext()
    {
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())){

            Uri uri = intent.getData();
            String roomname = "";
            try {
                roomname = uri.getFragment();
                Log.d(TAG ,roomname);
            }catch (NullPointerException e){
                Log.d(TAG ,e.getLocalizedMessage());
                roomname = "";
            }
            if (roomname.equals("")){
                try {
                    roomname = uri.getQueryParameter("room");
                    Log.d(TAG ,roomname);
                }catch (NullPointerException e){
                    Log.d(TAG ,e.getLocalizedMessage());
                    roomname = "";
                }
            }

            Global.isHook = true;
            Prefs.putString(Config.ROOM_NAME ,(roomname.length() == 0) ? "default" : roomname);
            gotoChat();

        }else
            gotoRoomEntry();
    }
}
