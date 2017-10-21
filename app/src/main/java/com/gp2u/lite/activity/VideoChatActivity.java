package com.gp2u.lite.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gp2u.lite.R;
import com.gp2u.lite.model.Config;
import com.pixplicity.easyprefs.library.Prefs;

import net.colindodd.toggleimagebutton.ToggleImageButton;

import org.webrtc.SurfaceViewRenderer;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import sg.com.temasys.skylink.sdk.listener.LifeCycleListener;
import sg.com.temasys.skylink.sdk.listener.MediaListener;
import sg.com.temasys.skylink.sdk.listener.RemotePeerListener;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConfig;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConnection;
import sg.com.temasys.skylink.sdk.rtc.UserInfo;

public class VideoChatActivity extends AppCompatActivity implements LifeCycleListener, MediaListener, RemotePeerListener{

    private static String TAG = VideoChatActivity.class.getName();

    SkylinkConnection skylinkConnection;
    String roomName;
    String userName;

    //public  String[] peerList;
    public static final float[][] RECT_WHEN_PEER1 = {{0, 0, 1, 1} ,{0, 1, 0, 0} ,{0, 1, 0, 0},{0, 1, 0, 0}};
    public static final float[][] RECT_WHEN_PEER2 = {{0, 0, 1, 0.5f} ,{0 , 0.5f ,1 ,0.5f} ,{0 , 1 ,0 ,0},{0 , 1 ,0 ,0}};
    public static final float[][] RECT_WHEN_PEER3 = {{0 , 0 ,1 ,0.5f} ,{0 , 0.5f ,0.5f ,0.5f} ,{0.5f , 0.5f ,0.5f ,0.5f},{0 , 1 ,0 ,0}};
    public static final float[][] RECT_WHEN_PEER4 = {{0 , 0 ,0.5f ,0.5f} ,{0 , 0.5f ,0.5f ,0.5f} ,{0.5f , 0.5f ,0.5f ,0.5f},{0.5f , 0 ,0.5f ,0.5f}};

    MediaPlayer lockPlayer;
    MediaPlayer unlockPlayer;
    MediaPlayer exitPlayer;
    MediaPlayer arrivePlayer;
    MediaPlayer mutePlayer;
    MediaPlayer unmutePlayer;

    @BindView(R.id.lock_button)
    ToggleImageButton lockButton;

    @BindView(R.id.mute_button)
    ToggleImageButton muteButon;

    @BindView(R.id.badge_text)
    TextView badgeTextView;

    @BindView(R.id.local_layout)
    RelativeLayout localLayout;

    @BindView(R.id.remote_layout)
    RelativeLayout remoteLayout;

    @BindView(R.id.peer1_layout)
    RelativeLayout peer1Layout;

    @BindView(R.id.peer2_layout)
    RelativeLayout peer2Layout;

    @BindView(R.id.peer3_layout)
    RelativeLayout peer3Layout;

    @BindView(R.id.peer4_layout)
    RelativeLayout peer4Layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_chat);

        ButterKnife.bind(this);

        roomName = getIntent().getExtras().getString(Config.ROOM_NAME);
        //showUserDialog();
        configToggleButtons();
        initMediaPlayer();
        refreshPeerViews();
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
                        userName = input.toString();

                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        //Log.d(TAG ,"clicked OK!");
                        connectToRoom();

                    }
                })
                .alwaysCallInputCallback()
                .show();
    }

    private void initMediaPlayer()
    {
        lockPlayer = MediaPlayer.create(this ,R.raw.unlock);
        unlockPlayer = MediaPlayer.create(this ,R.raw.unlock);
        exitPlayer = MediaPlayer.create(this ,R.raw.close_door);
        arrivePlayer = MediaPlayer.create(this ,R.raw.ding_dong);
        mutePlayer = MediaPlayer.create(this ,R.raw.click_off);
        unmutePlayer = MediaPlayer.create(this ,R.raw.click_on);

        Toasty.Config.getInstance()
                .setErrorColor(Color.parseColor("#dd0000")) // optional
                .setInfoColor(Color.parseColor("#999999")) // optional
                .apply();
    }

    private SkylinkConfig getSkylinkConfig() {
        SkylinkConfig config = new SkylinkConfig();
        config.setAudioVideoSendConfig(SkylinkConfig.AudioVideoConfig.AUDIO_AND_VIDEO);
        config.setAudioVideoReceiveConfig(SkylinkConfig.AudioVideoConfig.AUDIO_AND_VIDEO);
        config.setAudioVideoSendConfig(SkylinkConfig.AudioVideoConfig.AUDIO_AND_VIDEO);
        config.setAudioVideoReceiveConfig(SkylinkConfig.AudioVideoConfig.AUDIO_AND_VIDEO);
        config.setHasPeerMessaging(true);
        config.setHasFileTransfer(true);
        config.setMirrorLocalView(true);
        config.setTimeout(60);
        return config;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        refreshPeerViews();
    }

    /**
     * Get peerId of a Peer using SkylinkConnection API.
     *
     * @param index 0 for self Peer, 1 onwards for remote Peer(s).
     * @return Desired peerId or null if not available.
     */
    private String getPeerId(int index) {
        if (skylinkConnection == null) {
            return null;
        }
        String[] peerIdList = skylinkConnection.getPeerIdList();
        // Ensure index does not exceed max index on peerIdList.
        if (index <= peerIdList.length - 1) {
            return peerIdList[index];
        } else {
            return null;
        }
    }

    /**
     * Get Video View of a given Peer using SkylinkConnection API.
     *
     * @param peerId null for self Peer.
     * @return Desired Video View or null if not present.
     */
    private SurfaceViewRenderer getVideoView(String peerId) {
        if (skylinkConnection == null) {
            return null;
        }
        return skylinkConnection.getVideoView(peerId);
    }

    private void connectToRoom()
    {
        skylinkConnection = SkylinkConnection.getInstance();
        skylinkConnection.init(Config.APP_KEY, getSkylinkConfig(), this.getApplicationContext());
        skylinkConnection.setLifeCycleListener(this);
        skylinkConnection.setMediaListener(this);
        skylinkConnection.setRemotePeerListener(this);
        skylinkConnection.connectToRoom(Config.APP_KEY_SECRET ,roomName ,userName);
    }

    private void configToggleButtons()
    {
        lockButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){

                }
                else {

                }

            }
        });

        muteButon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                skylinkConnection.muteLocalAudio(b);
                if (b){

                    muteButon.setAlpha(1.0f);
                    showToast(getString(R.string.MICROPHONE_MUTE_MESSAGE));
                    mutePlayer.start();
                }
                else
                {
                    muteButon.setAlpha(0.6f);
                    showToast(getString(R.string.MICROPHONE_UNMUTE_MESSAGE));
                    unmutePlayer.start();
                }
            }
        });
    }

    public void onSwitchCamera(View view)
    {
        skylinkConnection.switchCamera();
    }

    public void onCancel(View view)
    {
        if (skylinkConnection != null && skylinkConnection.isConnected())
        {
            skylinkConnection.unlockRoom();
            skylinkConnection.disconnectFromRoom();
        }
        finish();
    }

    public void onDisconnect(View view)
    {
        exitPlayer.start();
        if (skylinkConnection != null && skylinkConnection.isConnected())
        {
            skylinkConnection.unlockRoom();
            skylinkConnection.disconnectFromRoom();
        }

        finish();

    }

    public void onRefresh(View view)
    {
    }

    public void onMessage(View view)
    {

    }

    public void setBadge(int count)
    {
        if (count == 0)
            badgeTextView.setVisibility(View.GONE);
        else
        {
            badgeTextView.setVisibility(View.VISIBLE);
            if (count < 10)
                badgeTextView.setText(String.format(Locale.US, " %d " ,count));
            else
                badgeTextView.setText(String.format(Locale.US, "%d" ,count));
        }
    }

    @Override
    public void onConnect(boolean isSuccess, String s) {

        if (isSuccess){

        }
    }

    @Override
    public void onWarning(int errorCode, String message) {

        showToastWithError(message);

    }

    @Override
    public void onDisconnect(int i, String s) {

    }

    @Override
    public void onReceiveLog(int i, String s) {

    }

    @Override
    public void onLockRoomStatusChange(String s, boolean b) {

    }

    @Override
    public void onLocalMediaCapture(SurfaceViewRenderer surfaceViewRenderer) {

        if (surfaceViewRenderer == null) {
            return;
        }
        localLayout.addView(getVideoView(null));

    }

    @Override
    public void onVideoSizeChange(String s, Point point) {

    }

    @Override
    public void onRemotePeerAudioToggle(String s, boolean b) {

    }

    @Override
    public void onRemotePeerVideoToggle(String s, boolean b) {

    }

    @Override
    public void onRemotePeerMediaReceive(String s, SurfaceViewRenderer surfaceViewRenderer) {

    }

    @Override
    public void onRemotePeerJoin(String s, Object o, boolean b) {

    }

    @Override
    public void onRemotePeerConnectionRefreshed(String s, Object o, boolean b, boolean b1) {

    }

    @Override
    public void onRemotePeerUserDataReceive(String s, Object o) {

    }

    @Override
    public void onOpenDataConnection(String s) {

    }

    @Override
    public void onRemotePeerLeave(String s, String s1, UserInfo userInfo) {

    }

    public void showToast(String message)
    {
        Toast toast = Toasty.info(this, message, Toast.LENGTH_SHORT, false);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void showToastWithError(String message)
    {
        Toast toast = Toasty.error(this, message, Toast.LENGTH_SHORT, false);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void refreshPeerViews()
    {

        boolean isPortrait = false;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            isPortrait = true;

        float[][] RECTS;
        RelativeLayout[] peerLayouts = {peer1Layout ,peer2Layout ,peer3Layout ,peer4Layout};

        String[] peerList = {"0","0","0"};
        switch (peerList.length)
        {
            case 1:
                RECTS = RECT_WHEN_PEER1;
                break;
            case 2:
                RECTS = RECT_WHEN_PEER2;
                break;
            case 3:
                RECTS = RECT_WHEN_PEER3;
                break;
            case 4:
                RECTS = RECT_WHEN_PEER4;
                break;
            default:
                RECTS = RECT_WHEN_PEER1;
        }

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        if (!isPortrait){

            int temp = screenWidth;
            screenWidth = screenHeight;
            screenHeight = temp;
        }

        for (int i = 0 ; i < RECTS.length ; i ++)
        {
            RelativeLayout layout = peerLayouts[i];
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            params.leftMargin = (isPortrait) ? (int)(screenWidth * RECTS[i][0]) : (int)(screenHeight * RECTS[i][1]);
            params.topMargin = (isPortrait) ?  (int)(screenHeight * RECTS[i][1]) : (int)(screenWidth * RECTS[i][0]);
            params.width = (isPortrait) ? (int)(screenWidth * RECTS[i][2]) : (int)(screenHeight * RECTS[i][3]);
            params.height = (isPortrait) ? (int)(screenHeight * RECTS[i][3]) : (int)(screenWidth * RECTS[i][2]);
            layout.setLayoutParams(params);
        }
    }
}