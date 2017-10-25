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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gp2u.lite.R;
import com.gp2u.lite.control.AudioRouter;
import com.gp2u.lite.fragment.MessageFragment;
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
import sg.com.temasys.skylink.sdk.listener.OsListener;
import sg.com.temasys.skylink.sdk.listener.RemotePeerListener;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConfig;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConnection;
import sg.com.temasys.skylink.sdk.rtc.UserInfo;

public class VideoChatActivity extends AppCompatActivity implements LifeCycleListener, MediaListener, RemotePeerListener ,OsListener{

    private static String TAG = VideoChatActivity.class.getName();

    SkylinkConnection skylinkConnection;
    String roomName;
    String userName;
    boolean bFromRemote;

    private static String[] peerList = new String[4];
    private RelativeLayout[] videoViewLayouts;

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

    MessageFragment messageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_chat);

        ButterKnife.bind(this);

        messageFragment = (MessageFragment)getSupportFragmentManager().findFragmentById(R.id.message_fragment);
        messageFragment.toggleShow(false);

        roomName = getIntent().getExtras().getString(Config.ROOM_NAME);
        showUserDialog();
        configToggleButtons();
        initMediaPlayer();
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

        videoViewLayouts = new RelativeLayout[]{peer1Layout, peer2Layout, peer3Layout ,peer4Layout};
        Toasty.Config.getInstance()
                .setErrorColor(Color.parseColor("#dd0000")) // optional
                .setInfoColor(Color.parseColor("#999999")) // optional
                .apply();
    }

    private SkylinkConfig getSkylinkConfig() {
        SkylinkConfig config = new SkylinkConfig();
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        emptyLayout();
        AudioRouter.stopAudioRouting(this.getApplicationContext());

    }

    @Override
    public void onBackPressed(){

        if (skylinkConnection != null && skylinkConnection.isConnected())
        {
            skylinkConnection.unlockRoom();
            skylinkConnection.disconnectFromRoom();
        }
        finish();
    }

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
        //skylinkConnection.setOsListener(this);
        boolean connectFailed;
        connectFailed = !skylinkConnection.connectToRoom(Config.APP_KEY_SECRET ,roomName ,userName);
        if (connectFailed) {
            String error = "Unable to connect to Room! Rotate device to try again later.";
            showToast(error);
            return;
        }
        AudioRouter.startAudioRouting(this);

    }

    private void configToggleButtons()
    {
        lockButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (bFromRemote) return;
                if (b){

                    lockPlayer.start();
                    skylinkConnection.lockRoom();
                    showToast(getString(R.string.ROOM_LOCK_MESSAGE));
                }
                else {

                    unlockPlayer.start();
                    skylinkConnection.unlockRoom();
                    showToast(getString(R.string.ROOM_UNLOCK_MESSAGE));
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
        skylinkConnection.refreshConnection(null, false);
    }

    public void onMessage(View view)
    {
        messageFragment.toggleShow(true);
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
    public void onLockRoomStatusChange(String remotePeerId, boolean lockStatus) {

        bFromRemote = true;
        lockButton.setChecked(lockStatus);
        bFromRemote = false;

        if (lockStatus)
        {
            lockPlayer.start();
            showToast(getString(R.string.ROOM_LOCK_REMOTE));
        }
        else{

            unlockPlayer.start();
            showToast(getString(R.string.ROOM_UNLOCK_REMOTE));
        }
    }

    @Override
    public void onLocalMediaCapture(SurfaceViewRenderer surfaceViewRenderer) {

        if (surfaceViewRenderer == null) {
            return;
        }
        addFullSubView(localLayout ,getVideoView(null));
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
    public void onRemotePeerMediaReceive(String remotePeerId, SurfaceViewRenderer surfaceViewRenderer) {

        addRemoteView(remotePeerId);
        refreshPeerViews();
    }

    @Override
    public void onRemotePeerJoin(String remotePeerId, Object userData, boolean hasDataChannel) {

        addRemotePeer(remotePeerId);
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
    public void onRemotePeerLeave(String remotePeerId, String message, UserInfo userInfo) {

        showToast(getString(R.string.REMOTE_PEER_DISCONNECTED));
        exitPlayer.start();
        skylinkConnection.unlockRoom();

        removeRemotePeer(remotePeerId);
        refreshPeerViews();
    }

    @Override
    public void onPermissionRequired(String[] strings, int i, int i1) {

    }

    @Override
    public void onPermissionGranted(String[] strings, int i, int i1) {

    }

    @Override
    public void onPermissionDenied(String[] strings, int i, int i1) {

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

        int peerCount = 0;
        for (int i = 0; i < peerList.length ; i ++){

            if (peerList[i] != null) peerCount++;
        }
        switch (peerCount)
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

    private int addRemotePeer(String peerId) {
        if (peerId == null) {
            return -1;
        }
        for (int peerIndex = 0; peerIndex < peerList.length; ++peerIndex) {
            if (peerList[peerIndex] == null) {
                peerList[peerIndex] = peerId;
                // Add to other Peer maps
                return peerIndex;
            }
        }
        return -1;
    }

    private void removeRemotePeer(String peerId) {
        int index = getPeerIndex(peerId);
        if (index < 0 || index > videoViewLayouts.length) {
            return;
        }
        removePeerView(peerId);
        peerList[index] = null;
        shiftUpRemotePeers();
    }

    private void addRemoteView(String remotePeerId) {
        SurfaceViewRenderer videoView = getVideoView(remotePeerId);
        if (videoView == null) {
            return;
        }

        // Remove any existing Peer View.
        // This may sometimes be the case, for e.g. in screen sharing.
        removePeerView(remotePeerId);

        int index = getPeerIndex(remotePeerId);
        if (index < 0 || index > videoViewLayouts.length) {
            return;
        }

        addFullSubView(videoViewLayouts[index] ,videoView);

    }

    private int removePeerView(String peerId) {
        int indexToRemove = getPeerIndex(peerId);
        // Safety check
        if (indexToRemove < 0 || indexToRemove > peerList.length) {
            return -1;
        }
        // Remove view
        videoViewLayouts[indexToRemove].removeAllViews();
        return indexToRemove;
    }

    private void shiftUpRemotePeers() {
        int indexEmpty = -1;
        // Remove view from layout.
        for (int i = 0; i < videoViewLayouts.length; ++i) {
            if (peerList[i] == null) {
                indexEmpty = i;
                continue;
            }
            // Switch Peer to empty spot if there is any.
            if (indexEmpty > -1) {
                // Shift peerList.
                String peerId = peerList[i];
                peerList[i] = null;
                peerList[indexEmpty] = peerId;
                // Shift UI.
                RelativeLayout peerFrameLayout = videoViewLayouts[i];
                // Put this view in the layout before it.
                SurfaceViewRenderer view = (SurfaceViewRenderer) peerFrameLayout.getChildAt(0);
                if (view != null) {
                    peerFrameLayout.removeAllViews();
                    addFullSubView(videoViewLayouts[indexEmpty] ,view);

                }
                ++indexEmpty;
            }
        }
    }

    private int getPeerIndex(String peerId) {
        if (peerId == null) {
            return -1;
        }
        for (int index = 0; index < peerList.length; ++index) {
            if (peerId.equals(peerList[index])) {
                return index;
            }
        }
        return -1;
    }

    public  void removeViewFromParent(SurfaceViewRenderer videoView) {
        if (videoView != null) {
            Object viewParent = videoView.getParent();
            if (viewParent != null) {
                // If parent is a ViewGroup, remove from parent.
                if (ViewGroup.class.isInstance(viewParent)) {
                    ((ViewGroup) viewParent).removeView(videoView);
                }
            }
        }
    }

    private void emptyLayout() {

        for (int i = 0 ; i < peerList.length ; i ++){

            if (peerList[i] != null)
                removeViewFromParent(getVideoView(peerList[i]));

        }
    }

    private void addFullSubView(RelativeLayout layout , View view)
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layout.addView(view ,params);
    }
}