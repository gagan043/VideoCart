package com.gp2u.lite.fragment;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.gp2u.lite.R;
import com.gp2u.lite.activity.VideoChatActivity;
import com.gp2u.lite.model.Message;
import com.gp2u.lite.model.User;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.com.temasys.skylink.sdk.listener.MessagesListener;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConnection;
import sg.com.temasys.skylink.sdk.rtc.SkylinkException;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MessageFragment extends Fragment implements MessagesListener{

    @BindView(R.id.cancel_button)
    Button cancelBtn;

    @BindView(R.id.messagesList)
    MessagesList messagesList;

    @BindView(R.id.input)
    MessageInput input;

    MessagesListAdapter<Message> messagesAdapter;
    String senderId = "me";
    public String userName;
    public SkylinkConnection skylinkConnection;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this ,view);
        initAdapter();
        initInput();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart(){

        super.onStart();

    }

    public void toggleShow(boolean isShow){

        if (isShow)
            getView().setVisibility(View.VISIBLE);
        else
            getView().setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.cancel_button) void onCancel(){

        toggleShow(false);
    }

    private void initAdapter()
    {
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.with(getActivity()).load(url).into(imageView);
            }
        };

        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextLayout(R.layout.item_custom_incoming_text_message)
                .setOutcomingTextLayout(R.layout.item_custom_outcoming_text_message);
        messagesAdapter = new MessagesListAdapter<>(senderId, holdersConfig ,imageLoader);
        messagesAdapter.setDateHeadersFormatter(new DateFormatter.Formatter() {
            @Override
            public String format(Date date) {
                if (DateFormatter.isToday(date)) {
                    return "Today";
                } else {
                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
                }
            }
        });
        messagesList.setAdapter(messagesAdapter);
    }

    private void initInput(){

        input.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence charSequence) {

                Message message = new Message(null , new User(senderId ,userName),charSequence.toString());
                messagesAdapter.addToStart(message, true);
                try {
                    skylinkConnection.sendP2PMessage(null ,charSequence.toString());
                } catch (SkylinkException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                return true;
            }
        });

        input.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {

            }
        });
    }

    public static String getPeerIdNick(String peerId) {
        String peerIdShow = peerId;
        if (peerId == null) {
            SkylinkConnection skylinkConnection = SkylinkConnection.getInstance();
            if (skylinkConnection != null) {
                peerIdShow = skylinkConnection.getPeerId();
            }
            if (peerIdShow == null) {
                peerIdShow = "Self";
            }
        }
        String peerIdNick = peerIdShow + " (" + getUserDataString(peerId) + ")";
        return peerIdNick;
    }

    public static String getUserDataString(String peerId) {
        Object userDataObject = SkylinkConnection.getInstance().getUserData(peerId);
        String userDataString = "";
        if (userDataObject != null) {
            userDataString = userDataObject.toString();
        }
        return userDataString;
    }

    @Override
    public void onServerMessageReceive(String remotePeerId, Object message, boolean b) {

        if (message instanceof String) {

            Message message1 = new Message(null , new User(remotePeerId ,getPeerIdNick(remotePeerId)),(String)message);
            messagesAdapter.addToStart(message1, true);

            showAlert((String)message);
        }

    }

    @Override
    public void onP2PMessageReceive(String remotePeerId, Object message, boolean b) {

        if (message instanceof String) {

            Message message1 = new Message(null , new User(remotePeerId ,getPeerIdNick(remotePeerId)),(String)message);
            messagesAdapter.addToStart(message1, true);

            showAlert((String)message);
        }

    }

    private void showAlert(String message)
    {

        // show alert if self is not shown
        if (isVisible()){

            ((VideoChatActivity)getActivity()).unreadMessages = 0;

        }
        else {

            ((VideoChatActivity)getActivity()).unreadMessages++;
            ((VideoChatActivity)getActivity()).showAlert(message);
        }
    }
}
