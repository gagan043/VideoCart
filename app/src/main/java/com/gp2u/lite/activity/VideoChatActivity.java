package com.gp2u.lite.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.esafirm.imagepicker.features.ImagePicker;
import com.gp2u.lite.R;
import com.gp2u.lite.control.APIService;
import com.gp2u.lite.control.AudioRouter;
import com.gp2u.lite.fragment.MessageFragment;
import com.gp2u.lite.model.Config;
import com.gp2u.lite.model.Global;
import com.gp2u.lite.utils.CircleAnimation;
import com.gp2u.lite.utils.KeyboardUtil;
import com.gp2u.lite.view.CCView;
import com.pixplicity.easyprefs.library.Prefs;

import net.colindodd.toggleimagebutton.ToggleImageButton;

import org.webrtc.SurfaceViewRenderer;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import rx.Subscription;
import sg.com.temasys.skylink.sdk.listener.LifeCycleListener;
import sg.com.temasys.skylink.sdk.listener.MediaListener;
import sg.com.temasys.skylink.sdk.listener.OsListener;
import sg.com.temasys.skylink.sdk.listener.RemotePeerListener;
import sg.com.temasys.skylink.sdk.rtc.SkylinkCaptureFormat;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConfig;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConnection;
import sg.com.temasys.skylink.sdk.rtc.UserInfo;

public class VideoChatActivity extends AppCompatActivity implements LifeCycleListener, MediaListener, RemotePeerListener ,OsListener{

    private static String TAG = VideoChatActivity.class.getName();

    Context context = this;
    SkylinkConnection skylinkConnection;
    String roomName;
    String userName;
    public int unreadMessages;
    boolean bFromRemote;
    boolean localDisconnect = false;
    boolean showAlert;
    boolean showRemote = true;
    private static String[] peerList = new String[4];
    private RelativeLayout[] videoViewLayouts;
    private Button[] muteButtons ;

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
    MediaPlayer refreshPlayer;

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

    @BindView(R.id.chat_layout)
    FrameLayout chatLayout;

    @BindView(R.id.control_layout)
    ConstraintLayout controlLayout;

    @BindView(R.id.video_layout)
    RelativeLayout videoLayout;

    @BindView(R.id.video_view)
    VideoView videoView;

    @BindView(R.id.parent_layout)
    ConstraintLayout parentLayout;

    @BindView(R.id.cancel_button)
    RelativeLayout cancelButton;

    @BindView(R.id.peer1_mute)
    Button peer1muteButton;

    @BindView(R.id.peer2_mute)
    Button peer2muteButton;

    @BindView(R.id.peer3_mute)
    Button peer3muteButton;

    @BindView(R.id.peer4_mute)
    Button peer4muteButton;

    @BindView(R.id.localtoggle_button)
    Button localtoggleButton;

    @BindView(R.id.web_view)
    WebView webView;

    MessageFragment messageFragment;

    Subscription subscription;

    @BindView(R.id.background_view)
    RelativeLayout background_view;

    @BindView(R.id.ccView)
    CCView ccView;

    @BindView(R.id.cc_imageview)
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AudioRouter.startAudioRouting(this);
        this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_chat);

        new KeyboardUtil(this, findViewById(R.id.chat_layout));

        ButterKnife.bind(this);

        messageFragment = new MessageFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.chat_layout ,messageFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        showMessage(false);

        unreadMessages = 0;
        showUserDialog();
        configToggleButtons();
        initMediaPlayer();

        cancelButton.setVisibility(View.INVISIBLE);
    }
    public void animatedCenterLogoMethod(){

        CircleAnimation animation = new CircleAnimation(ccView, 300);
        animation.setDuration(100);
        ccView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // start animation cross
                ccView.crossAnimation2();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void onResume()
    {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPause()
    {
        super.onPause();

    }

    public void showConnectedVideo() {

        videoView = (VideoView) findViewById(R.id.video_view);

        Uri video = Uri.parse(Config.VIDEO_URL);
        videoView.setVideoURI(video);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                background_view.setVisibility(View.GONE);
                webView.setVisibility(View.INVISIBLE);
                mp.setLooping(false);
                videoView.start();

            }

        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {

                if(mp.isPlaying()==false){
                    background_view.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                }
            }
        });
    }

    public void hideConnectedVideo() {
        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.stopPlayback();
        videoView.setVisibility(View.INVISIBLE);
    }

    public void showUserDialog()
    {

        roomName =  Prefs.getString(Config.ROOM_NAME ,"");
        if (roomName.isEmpty() ) {
            roomName =  UUID.randomUUID().toString().replace("-", "");
            Prefs.putString(Config.ROOM_NAME , roomName);
        }
        userName = Prefs.getString(Config.USER_NAME ,"");
        Log.d("USERNAME", userName);
        if (userName.isEmpty()) {
            // show prompt user name
            new MaterialDialog.Builder(this)
                    .title("User Name")
                    .input("Please Set Your Name", Prefs.getString(Config.USER_NAME ,""), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled((input.toString().trim().length() != 0));
                            Prefs.putString(Config.USER_NAME ,input.toString());
                            userName = input.toString();
                        }
                    }).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    connectToRoom();
                    messageFragment.userName = userName;
                    messageFragment.skylinkConnection = skylinkConnection;
                }
            }).alwaysCallInputCallback().show();
        }
        else {
            connectToRoom();
            messageFragment.userName = userName;
            messageFragment.skylinkConnection = skylinkConnection;
        }
        Global.userName = userName;
    }

    public void showMessage(boolean isShow)
    {
        if (isShow){

            int peerCount = 0;
            for (int i = 0; i < peerList.length ; i ++){
                if (peerList[i] != null) peerCount++;
            }
            if (peerCount == 0){
                showToastWithError(getString(R.string.OPEN_MESSAGE_ERROR));
                return;
            }
            chatLayout.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
        }
        else {
            chatLayout.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
        }

        refreshPeerViews();
    }

    public boolean isShownMessage()
    {
        if (chatLayout.getVisibility() == View.VISIBLE)
            return true;
        return false;
    }

    private void initMediaPlayer()
    {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume < maxVolume / 2)
            showToastWithError(getString(R.string.PLEASE_TURN_VOLUME_UP));

        lockPlayer = MediaPlayer.create(this ,R.raw.unlock);
        unlockPlayer = MediaPlayer.create(this ,R.raw.unlock);
        exitPlayer = MediaPlayer.create(this ,R.raw.close_door);
        arrivePlayer = MediaPlayer.create(this ,R.raw.ding_dong);
        mutePlayer = MediaPlayer.create(this ,R.raw.click_off);
        unmutePlayer = MediaPlayer.create(this ,R.raw.click_on);
        refreshPlayer = MediaPlayer.create(this,R.raw.refresh);

        videoViewLayouts = new RelativeLayout[]{peer1Layout, peer2Layout, peer3Layout ,peer4Layout};
        muteButtons = new Button[]{peer1muteButton, peer2muteButton, peer3muteButton ,peer4muteButton};
        Toasty.Config.getInstance()
                .setErrorColor(Color.parseColor("#dd0000")) // optional
                .setInfoColor(Color.parseColor("#999999")) // optional
                .apply();

        peer1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chatLayout.getVisibility() == View.VISIBLE){

                   int count = peer1Layout.getChildCount();
                   for (int i = 0 ; i < count ; i ++){

                       View subView = peer1Layout.getChildAt(i);
                       if (!(subView instanceof Button)){
                           if (subView.getVisibility() == View.VISIBLE)
                               subView.setVisibility(View.INVISIBLE);
                           else
                               subView.setVisibility(View.VISIBLE);
                           showRemote = subView.getVisibility() == View.VISIBLE;
                       }else {
                           if (subView.getVisibility() == View.VISIBLE)
                               subView.setVisibility(View.INVISIBLE);
                           else
                               subView.setVisibility(View.VISIBLE);
                       }
                   }
                   if (!showRemote) peer1muteButton.setAlpha(0);
                   else peer1muteButton.setAlpha(1);
                }
            }
        });
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
        muteButtons[0].setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        emptyLayout();
        if (subscription != null) subscription.unsubscribe();

    }

    @Override
    public void onBackPressed(){

        if (chatLayout.getVisibility() == View.VISIBLE){

            showMessage(false);
        }
        else{
            goBack();
        }

    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {

            messageFragment.sendImage((ImagePicker.getImages(data)).get(0));
        }

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
        if (Prefs.getBoolean(Config.IS_TEST, false) || ! Global.isHook )  {
            Log.d("IS_TEST", "Running Video Test Room");
            Log.d("IS_TEST Global.isHook", Boolean.toString(Global.isHook));
            showConnectedVideo();
        }
        else {
            animatedCenterLogoMethod();
        }
        skylinkConnection = SkylinkConnection.getInstance();
        skylinkConnection.init(Config.APP_KEY, getSkylinkConfig(), this.getApplicationContext());
        skylinkConnection.setLifeCycleListener(this);
        skylinkConnection.setMediaListener(this);
        skylinkConnection.setRemotePeerListener(this);
        skylinkConnection.setMessagesListener(messageFragment);
        skylinkConnection.setFileTransferListener(messageFragment);

        boolean connectFailed;
        connectFailed = !skylinkConnection.connectToRoom(Config.APP_KEY_SECRET ,roomName ,userName);
        if (connectFailed) {
            String error = "Unable to connect to Room! Rotate device to try again later.";
            showToast(error);
            return;
        }
        localDisconnect = false;
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
        showToast(getString(R.string.SWAP_CAMERA));
        skylinkConnection.switchCamera();
    }

    public void onCancel(View view)
    {

        // hide keyboard
        View view1 = getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (webView.getVisibility() == View.VISIBLE){

            webView.setVisibility(View.INVISIBLE);
            return;
        }
        if (chatLayout.getVisibility() == View.VISIBLE){
            showMessage(false);
        }
        else{
            goBack();
        }

    }

    public void onDisconnect(View view)
    {
        exitPlayer.start();
        showToast(getString(R.string.LOCAL_PEER_DISCONNECTED));
        emptyLayout();
        localDisconnect = true;
        goBack();
    }

    private void goBack()
    {
        if (skylinkConnection != null && skylinkConnection.getSkylinkState() == SkylinkConnection.SkylinkState.CONNECTED)
        {
            skylinkConnection.unlockRoom();
            skylinkConnection.disconnectFromRoom();
        }

        APIService.getInstance().logConnection(peerCount() ,"Disconnect" ,"");
        APIService.getInstance().logConnection(peerCount() ,"Exit" ,"");
        finish();
    }

    public void onRefresh(View view)
    {
        refreshPlayer.start();
        skylinkConnection.refreshConnection(null, false);
        showToast(getString(R.string.REFRESHING_CONNECTION));
    }

    public void onMessage(View view)
    {
        unreadMessages = 0;
        setBadge();
        showMessage(true);
    }

    public void setBadge()
    {
        if (unreadMessages == 0)
            badgeTextView.setVisibility(View.GONE);
        else
        {
            badgeTextView.setVisibility(View.VISIBLE);
            if (unreadMessages < 10)
                badgeTextView.setText(String.format(Locale.US, " %d " ,unreadMessages));
            else
                badgeTextView.setText(String.format(Locale.US, "%d" ,unreadMessages));
        }
    }

    @Override
    public void onConnect(boolean isSuccess, String s) {

        APIService.getInstance().logConnection(peerCount() ,"Connect" ,s);
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
        localLayout.bringChildToFront(localtoggleButton);
    }

    @Override
    public void onVideoSizeChange(String s, Point point) {

    }

    @Override
    public void onSentVideoResolutionObtained(String var1, int var2, int var3, int var4)
    {

    }

    @Override
    public void onInputVideoResolutionObtained(int var1, int var2, int var3, SkylinkCaptureFormat var4)
    {

    }

    @Override
    public void onReceivedVideoResolutionObtained(String var1, int var2, int var3, int var4)
    {

    }

    @Override
    public void onRemotePeerAudioToggle(String remotePeerId, boolean muted) {

        Log.d("MUTE", "String: " + remotePeerId + " bool:" + muted );
        int buttonIndex = Arrays.asList(peerList).indexOf(remotePeerId);
        if (buttonIndex >= 0)
        if (muted) {
             //showToast("Remote peer:" + remotePeerId + "has muted their microphone");
            muteButtons[buttonIndex].setVisibility(View.VISIBLE);
        }
        else {
             //showToast("Remote peer:" + remotePeerId + "has unmuted their microphone");
            muteButtons[buttonIndex].setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRemotePeerVideoToggle(String s, boolean b) {

    }

    @Override
    public void onRemotePeerMediaReceive(String remotePeerId, SurfaceViewRenderer surfaceViewRenderer) {

        Log.d("PEER_INFO", remotePeerId);
        Log.d("PEER_INFO", skylinkConnection.getUserInfo(remotePeerId).getUserData().toString());
        String strJSON = skylinkConnection.getUserInfo(remotePeerId).getUserData().toString();
        try{
            JSONObject obj = new JSONObject(strJSON);
            String username = obj.getString("name");
            Log.d("PEER_INFO", username);
            Global.peerInfo.setProperty(remotePeerId, username);

        } catch(JSONException e) {}

        int i = addRemotePeer(remotePeerId);
        addRemoteView(remotePeerId);
        refreshPeerViews();
        // this is a hack to hide incorrect rendering of mute button
        // this is SDK issue TODO - get it fixed!
        muteButtons[i].setVisibility(View.INVISIBLE);
        hideConnectedVideo();

        APIService.getInstance().logConnection(peerCount() ,"Peer Joined" ,"");
    }

    @Override
    public void onRemotePeerJoin(String remotePeerId, Object userData, boolean hasDataChannel) {

        arrivePlayer.start();
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
        Log.d("[PEER LEAVING]", "remotePeerId: " + remotePeerId + " message: " +  message +" userInfo: " + userInfo);
        if (! localDisconnect) {
            showToast(getString(R.string.REMOTE_PEER_DISCONNECTED));
        }

        webView.setVisibility(View.INVISIBLE);
        exitPlayer.start();
        skylinkConnection.unlockRoom();

        removeRemotePeer(remotePeerId);
        refreshPeerViews();

        APIService.getInstance().logConnection(peerCount() ,"Peer Left" ,"");

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
        muteButtons = new Button[]{peer1muteButton, peer2muteButton, peer3muteButton ,peer4muteButton};
        int peerCount = 0;
        for (int i = 0; i < peerList.length ; i ++){

            muteButtons[i].setVisibility(View.INVISIBLE);
            if (peerList[i] != null && skylinkConnection != null) {
                UserInfo userInfo = skylinkConnection.getUserInfo(peerList[i]);
                if (userInfo != null){
                    if (userInfo.isAudioMuted()) {
                        Log.d("[MUTE]", +i + " peer: " + peerList[i] + " isAudioMuted: " + userInfo.isAudioMuted());
                        muteButtons[i].setVisibility(View.VISIBLE);
                        muteButtons[i].setAlpha(1);
                    }
                }
                peerCount++;
            }
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

        if (chatLayout.getVisibility() == View.VISIBLE){
            background_view.setVisibility(View.GONE);
            peer1Layout.setVisibility(View.INVISIBLE);
            peer2Layout.setVisibility(View.INVISIBLE);
            peer3Layout.setVisibility(View.INVISIBLE);
            peer4Layout.setVisibility(View.INVISIBLE);

            if (peerCount > 0){

                peer1Layout.setVisibility(View.VISIBLE);
                RelativeLayout layout = peer1Layout;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                int width =  screenWidth / 2;
                int height =  (int)(width * 0.75)  ;
                params.leftMargin = (isPortrait) ?  screenWidth - width : screenHeight - width;
                params.topMargin = 0;
                params.width = width;
                params.height = height;
                layout.setLayoutParams(params);
                // this is a hack to hide incorrect rendering of mute button
                // this is SDK issue TODO - get it fixed!
                muteButtons[0].setVisibility(View.INVISIBLE);
            }

            parentLayout.bringChildToFront(chatLayout);
            parentLayout.bringChildToFront(webView);
            parentLayout.bringChildToFront(cancelButton);
            parentLayout.bringChildToFront(remoteLayout);

        }else {

            peer1Layout.setVisibility(View.VISIBLE);
            int count = peer1Layout.getChildCount();
            for (int i = 0 ; i < count ; i ++){
                View subView = peer1Layout.getChildAt(i);
                if (!(subView instanceof Button)){
                    subView.setVisibility(View.VISIBLE);
                    showRemote = true;
                }
            }
            peer2Layout.setVisibility(View.VISIBLE);
            peer3Layout.setVisibility(View.VISIBLE);
            peer4Layout.setVisibility(View.VISIBLE);

            parentLayout.bringChildToFront(remoteLayout);
            parentLayout.bringChildToFront(localLayout);
            parentLayout.bringChildToFront(controlLayout);
            parentLayout.bringChildToFront(chatLayout);
            parentLayout.bringChildToFront(webView);
            parentLayout.bringChildToFront(cancelButton);
            if (peerCount == 0 ) {
                background_view.setVisibility(View.VISIBLE);
            }
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
            background_view.setVisibility(View.VISIBLE);
            webView.setVisibility(View.INVISIBLE);
            return;
        }
        background_view.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        addFullSubView(videoViewLayouts[index] ,videoView);
        videoViewLayouts[index].bringChildToFront(muteButtons[index]);
    }

    private int removePeerView(String peerId) {
        int indexToRemove = getPeerIndex(peerId);
        // Safety check
        if (indexToRemove < 0 || indexToRemove > peerList.length) {
            return -1;
        }
        // Remove view
        for (int i = 0 ; i < videoViewLayouts[indexToRemove].getChildCount() ; i ++){

            View subView = videoViewLayouts[indexToRemove].getChildAt(i);
            if (subView instanceof Button){
            }else {
                videoViewLayouts[indexToRemove].removeView(subView);
            }
        }
        //videoViewLayouts[indexToRemove].removeAllViews();
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
                    videoViewLayouts[indexEmpty].bringChildToFront(muteButtons[indexEmpty]);
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

    public void showAlert(String message)
    {
        setBadge();
        if (!showAlert){

            showAlert = true;
            new MaterialDialog.Builder(this)
                    .content(message)
                    .negativeText("Open Messages")
                    .positiveText("Dismiss")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            showAlert = false;
                            dialog.dismiss();

                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            showAlert = false;
                            unreadMessages = 0;
                            dialog.dismiss();
                            setBadge();
                            showMessage(true);
                            refreshPeerViews();
                        }
                    })
                    .show();
        }
    }

    public void openWebView(String url){

        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);
        chatLayout.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        refreshPeerViews();
    }

    public void onToggleLocalVideo(View view)
    {
        int count = localLayout.getChildCount();
        for (int i = 0 ; i < count ; i ++){

            View subView = localLayout.getChildAt(i);
            if(!(subView instanceof Button)){
                if (subView.getVisibility() == View.VISIBLE)
                    subView.setVisibility(View.INVISIBLE);
                else
                    subView.setVisibility(View.VISIBLE);
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            Log.d("[WebView] ", " onPageStarted " + url);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // capture "cancel_webview" to exit the webview and chat and drop back to video
            if (url.contains("cancel_webview")) {
                webView.setVisibility(View.INVISIBLE);
                showMessage(false);
                return true;  // finish processing
            }
            // else proceed as normal
            return false;
        }
    }

    private int peerCount()
    {
        int peerCount = 0;
        for (int i = 0; i < peerList.length ; i ++){
            if (peerList[i] != null)
                peerCount++;
        }
        return peerCount;
    }
}