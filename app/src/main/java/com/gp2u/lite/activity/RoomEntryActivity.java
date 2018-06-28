package com.gp2u.lite.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.gp2u.lite.R;
import com.gp2u.lite.control.APICallback;
import com.gp2u.lite.control.APIService;
import com.gp2u.lite.control.AudioRouter;
import com.gp2u.lite.model.Config;
import com.gp2u.lite.model.Global;
import com.pixplicity.easyprefs.library.Prefs;
import com.sjl.foreground.Foreground;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import rx.Subscription;

public class RoomEntryActivity extends AppCompatActivity {

    private static String TAG = RoomEntryActivity.class.getName();

    @BindView(R.id.room_editText) EditText roomEdit;
    @BindView(R.id.appointment_text)
    TextView appointmentTextView;

    @BindView(R.id.appointment_text1)
    TextView appointmentTextView1;

    @BindView(R.id.appointment_text2)
    TextView appointmentTextView2;

    Subscription subscription;
    Timer timer;
    Boolean isFound = false;

    List <String> autoconnectArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_room_entry);
        AudioRouter.startAudioRouting(this);
        addForegroundListener();

        ButterKnife.bind(this);
        String roomname = Prefs.getString(Config.ROOM_NAME ,"");
        if (roomname.isEmpty()) {
            roomname =  UUID.randomUUID().toString().replace("-", "");
        }
        Prefs.putString(Config.ROOM_NAME , roomname);
        roomEdit.setText(roomname);
        entryChangeListener();
        checkRoom();

    }

    @Override
    public void onStart()
    {
        super.onStart();
        //checkRoom();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //cancelTimer();
        hideKeyboard();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) subscription.unsubscribe();
    }

    private void addForegroundListener()
    {
        Foreground.init(getApplication());
        Foreground.Listener listener = new Foreground.Listener() {
            @Override
            public void onBecameForeground() {

            }

            @Override
            public void onBecameBackground() {

                cancelTimer();
            }
        };
        Foreground.get().addListener(listener);
    }

    public void onQRScan(View view)
    {
        Intent intent = new Intent(this ,QRCodeActivity.class);
        startActivityForResult(intent ,1);
    }

    public void onExit(View view)
    {
        APIService.getInstance().logConnection(0 ,"Exit" ,"");

        finishAffinity();
    }

    public void onEnter(View view)
    {
            APIService.getInstance().logConnection(0 ,"Enter" ,"");

            autoconnectArr.remove(roomEdit.getText().toString());
            autoconnectArr.add(roomEdit.getText().toString());
            Intent intent = new Intent(this ,VideoChatActivity.class);
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
        if (url.contains("cc.gp2u.com.au"))
        {
            Uri uri = Uri.parse(url);
            String roomname = "";
            try {
                roomname = uri.getFragment();
                Log.d(TAG ,roomname);
            }catch (NullPointerException e){
                Log.d(TAG ,e.getLocalizedMessage());
                roomname = "";
            }
            Boolean isTest = false;
            try {
                String testStr = uri.getQueryParameter("test");
                if (testStr.length() > 0) isTest = true;

            }catch (NullPointerException e){
                Log.d(TAG ,e.getLocalizedMessage());
                isTest = false;
            }
            Log.d(TAG + "IS_TEST: ", Boolean.toString(isTest));
            Prefs.putBoolean(Config.IS_TEST , isTest);
            if (roomname.isEmpty()){
                try {
                    roomname = uri.getQueryParameter("room");
                    Log.d(TAG ,roomname);
                }catch (NullPointerException e){
                    Log.d(TAG ,e.getLocalizedMessage());
                    roomname = "";
                }
            }
            if (roomname.isEmpty()) {
                roomname =  UUID.randomUUID().toString().replace("-", "");
            }
            Prefs.putString(Config.ROOM_NAME , roomname);
            roomEdit.setText(roomname);
            Global.isHook = true;
            Log.d(TAG + "IS_TEST Global.isHook", Boolean.toString(Global.isHook));

            onEnter(null);

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
        return String.format(Locale.ENGLISH ,"%02d:%02d:%02d" , hours, minutes, seconds);
    }

    private void entryChangeListener()
    {
        roomEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (Global.isHook){

                    new MaterialDialog.Builder(RoomEntryActivity.this)
                            .content(getString(R.string.ROOMCHANGE_MESSAGE))
                            .positiveText(getString(R.string.CHANGE_ROOM))
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    cancelTimer();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    roomEdit.setText(Prefs.getString(Config.ROOM_NAME ,""));

                                }
                            })
                            .show();

                    Global.isHook = false;
                }else
                    cancelTimer();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkRoom();
            }
        });
    }

    private void cancelTimer()
    {
        if (timer != null){
            timer.purge();
            timer.cancel();
            timer = null;
        }

        isFound = false;
        appointmentTextView.setText("");
        appointmentTextView1.setText("");
        appointmentTextView2.setText("");
    }

    private void hideKeyboard()
    {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void checkAutoConnection(JsonObject jsonObject)
    {
        String name = jsonObject.get("for_name").getAsString();
        String with_name = jsonObject.get("with_name").getAsString();
        Long timestamp = jsonObject.get("appointment_date").getAsLong();
        String uuid = jsonObject.get("uuid").getAsString();
        Prefs.putString(Config.UUID ,uuid);
        Date appointDate = new Date(timestamp * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd MMMM h:mm a" ,Locale.ENGLISH);
        String dateStr = format.format(appointDate);
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                String futureStr;
                boolean isFuture = false;
                Date currentDate = new Date();
                Long interval = Math.abs(currentDate.getTime() - appointDate.getTime()) / 1000L;
                futureStr = stringFromTimeInterval(interval);
                if (currentDate.getTime() < appointDate.getTime()) isFuture = true;
                String str = String.format("Hello %s \nYour appointment is with\n %s on \n%s",name ,with_name ,dateStr);
                SpannableString ss = new SpannableString(futureStr);
                if (isFuture){
                    ss.setSpan(new ForegroundColorSpan(Color.GREEN) ,0 ,futureStr.length() ,0);
                }else {
                    ss.setSpan(new ForegroundColorSpan(Color.RED) ,0 ,futureStr.length() ,0);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appointmentTextView1.setText(ss);
                        appointmentTextView.setText(str);
                        appointmentTextView2.setText(getString(R.string.PRESS_PHONE));
                    }
                });

                if ((isFuture && interval < 300) || (!isFuture && Math.abs(interval - 300) < 1)){

                    boolean autoconnect = autoconnectArr.contains(roomEdit.getText().toString());
                    if (!autoconnect){
                        onEnter(null);
                    }
                }

            }
        } ,0 ,1000);
    }

    private void checkRoom()
    {
        String room = roomEdit.getText().toString();
        if (room.isEmpty()) {
            showToastWithError(getString(R.string.ROOM_NAME_CANNOT_BE_BLANK));
        }
        else {
            /*
            ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text(Config.CHECKING_ROOM_INFO)
                    .fadeColor(Color.DKGRAY).build();
            dialog.show();
            */
            Prefs.putString(Config.ROOM_NAME ,roomEdit.getText().toString());
            Log.d("ROOM_NAME", roomEdit.getText().toString());
            subscription = APIService.getInstance().getUserName(roomEdit.getText().toString());
            APIService.getInstance().setOnCallback(new APICallback() {
                @Override
                public void doNext(JsonObject jsonObject) {
                    Prefs.putString(Config.ROOM_NAME ,roomEdit.getText().toString());
                    if (jsonObject.get("found").getAsInt() == 1){
                        isFound = true;
                        Prefs.putString(Config.USER_NAME ,jsonObject.get("for_name").getAsString());
                        Prefs.putString(Config.UUID ,jsonObject.get("uuid").getAsString());
                        checkAutoConnection(jsonObject);
                    }else {
                        isFound = false;
                        appointmentTextView.setText("");
                        appointmentTextView1.setText(getString(R.string.ROOM_NOT_FOUND));
                        appointmentTextView2.setText(getString(R.string.PRESS_PHONE));
                    }
                }

                @Override
                public void doCompleted() {

                }

                @Override
                public void doError(Throwable e) {
                    //dialog.dismiss();
                    showToastWithError(getString(R.string.INTERNET_ERROR));

                }
            });
        }
    }

    public void showToastWithError(String message)
    {
        Toast toast = Toasty.error(this, message, Toast.LENGTH_SHORT, false);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
