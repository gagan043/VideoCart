package com.gp2u.lite.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gp2u.lite.R;
import com.gp2u.lite.model.Config;
import com.pixplicity.easyprefs.library.Prefs;

public class VideoChatActivity extends AppCompatActivity {

    private static String TAG = VideoChatActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_chat);

        Log.d(TAG ,getIntent().getExtras().getString(Config.ROOM_NAME));
        showUserDialog();
    }

    public void showUserDialog()
    {
        new MaterialDialog.Builder(this)
                .title("User Name")
                .input("Please Set Your Name", Prefs.getString(Config.USER_NAME ,""), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something

                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled((input.toString().length() != 0));
                        Prefs.putString(Config.USER_NAME ,input.toString());

                    }
                })
                .alwaysCallInputCallback()
                .show();
    }

}