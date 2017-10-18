package com.gp2u.lite.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.gp2u.lite.R;
import com.pixplicity.easyprefs.library.Prefs;

public class PermissionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_permission);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Prefs.getBoolean("bPermissionAsked" ,false)) {

            gotoRoomEntry();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Prefs.putBoolean("bPermissionAsked" ,true);
        gotoRoomEntry();

    }

    public void onAllow(View view)
    {
        ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.CAMERA ,Manifest.permission.RECORD_AUDIO} ,111);

    }

    public void onSkip(View view)
    {

        Prefs.putBoolean("bPermissionAsked" ,true);
        gotoRoomEntry();

    }

    private void gotoRoomEntry()
    {
        Intent intent = new Intent(this ,RoomEntryActivity.class);
        startActivity(intent);
        finish();
    }
}
