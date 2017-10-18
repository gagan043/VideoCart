package com.gp2u.lite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gp2u.lite.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomEntryActivity extends AppCompatActivity {

    private static String TAG = "RoomEntryActivity";
    @BindView(R.id.room_editText) EditText roomEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_room_entry);

        ButterKnife.bind(this);
    }

    public void onQRScan(View view)
    {

        Intent intent = new Intent(this ,QRCodeActivity.class);
        startActivityForResult(intent ,1);
    }

    public void onEnter(View view)
    {
        Intent intent = new Intent(this ,VideoChatActivity.class);
        intent.putExtra("roomname" ,((roomEdit.getText().length() == 0) ? "default" : roomEdit.getText().toString()));
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode ,int resultCode ,Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            Log.d(TAG ,data.getData().toString());
            parseUrl(data.getData().toString());
        }
    }

    private void parseUrl(String url)
    {
        if (url.contains("https://gp2u.com.au/video?room="))
        {
            Intent intent = new Intent(this ,VideoChatActivity.class);
            String replaceStr = url.replace("https://gp2u.com.au/video?room=" ,"");
            intent.putExtra("roomname" ,replaceStr);
            startActivity(intent);
        }
        else{

            new MaterialDialog.Builder(this)
                    .content(url)
                    .positiveText("OK")
                    .show();
        }
    }

}
