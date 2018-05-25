package com.gp2u.lite.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gp2u.lite.R;
import com.gp2u.lite.control.APICallback;
import com.gp2u.lite.control.APIService;
import com.gp2u.lite.model.Config;
import com.pixplicity.easyprefs.library.Prefs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

public class RoomEntryActivity extends AppCompatActivity {

    private static String TAG = RoomEntryActivity.class.getName();

    @BindView(R.id.room_editText) EditText roomEdit;
    @BindView(R.id.appointment_text)
    TextView appointmentTextView;

    Subscription subscription;
    Timer timer;
    Boolean isFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_room_entry);

        ButterKnife.bind(this);
        roomEdit.setText(Prefs.getString(Config.ROOM_NAME ,""));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (timer != null){
            timer.purge();
            timer.cancel();
            timer = null;
        }

        isFound = false;
        appointmentTextView.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) subscription.unsubscribe();
    }

    public void onQRScan(View view)
    {
        Intent intent = new Intent(this ,QRCodeActivity.class);
        startActivityForResult(intent ,1);
    }

    public void onExit(View view)
    {
        finishAffinity();
    }

    public void onEnter(View view)
    {
        if (isFound){

            Intent intent = new Intent(this ,VideoChatActivity.class);
            startActivity(intent);
            isFound = false;
            return;
        }
        Prefs.putString(Config.ROOM_NAME ,roomEdit.getText().toString());
        subscription = APIService.getInstance().getUserName(roomEdit.getText().toString());
        APIService.getInstance().setOnCallback(new APICallback() {
            @Override
            public void doNext(JsonObject jsonObject) {

                int found = jsonObject.get("found").getAsInt();
                if (found == 1){

                    String firstname = jsonObject.get("firstname").getAsString();
                    String lastname = jsonObject.get("lastname").getAsString();
                    Long timestamp = jsonObject.get("appointment_date").getAsLong();
                    String uuid = jsonObject.get("uuid").getAsString();
                    Prefs.putString(Config.UUID ,uuid);
                    Prefs.putString(Config.USER_NAME ,firstname + " " + lastname);
                    Date appointDate = new Date(timestamp * 1000L);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss" ,Locale.ENGLISH);
                    String dateStr = format.format(appointDate);
                    if (timer != null){
                        timer.cancel();
                        timer.purge();
                    }

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            String futureStr;
                            boolean isFuture = false;
                            Date currentDate = new Date();
                            futureStr = stringFromTimeInterval(Math.abs(currentDate.getTime() - appointDate.getTime()) / 1000L);
                            if (currentDate.getTime() < appointDate.getTime()) isFuture = true;
                            String str = String.format("Hello %s %s \nYour appointment is at %s \n%s \nClick the green phone button below to connect",firstname ,lastname ,dateStr ,futureStr);
                            SpannableString ss = new SpannableString(str);
                            int startIndex = str.indexOf(futureStr);
                            int endIndex= startIndex + futureStr.length();
                            ss.setSpan(new RelativeSizeSpan(1.5f) ,startIndex ,endIndex ,0);
                            if (isFuture){
                                ss.setSpan(new ForegroundColorSpan(Color.GREEN) ,startIndex ,endIndex ,0);
                            }else {
                                ss.setSpan(new ForegroundColorSpan(Color.RED) ,startIndex ,endIndex ,0);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    appointmentTextView.setText(ss);
                                }
                            });
                        }
                    } ,0 ,1000);
                    isFound = true;
                }else {
                    appointmentTextView.setText("Not found room");
                }
            }

            @Override
            public void doNext(JsonArray jsonObject) {

            }

            @Override
            public void doNext(String str) {

            }

            @Override
            public void doCompleted() {

            }

            @Override
            public void doError(Throwable e) {

            }
        });
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
            intent.putExtra(Config.ROOM_NAME,replaceStr);
            startActivity(intent);
        }
        else{

            new MaterialDialog.Builder(this)
                    .content(url)
                    .positiveText("OK")
                    .show();
        }
    }

    private String stringFromTimeInterval(Long interval)
    {
        long seconds = interval % 60;
        long minutes = (interval / 60) % 60;
        long hours = (interval / 3600);
        return String.format(Locale.ENGLISH ,"%2d:%2d:%2d" , hours, minutes, seconds);
    }

}
