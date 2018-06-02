package com.gp2u.lite.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.gp2u.lite.R;
import com.gp2u.lite.activity.VideoChatActivity;
import com.gp2u.lite.model.Config;
import com.gp2u.lite.model.Message;
import com.gp2u.lite.model.User;
import com.gp2u.lite.utils.KeyboardUtil;
import com.gp2u.lite.utils.Utils;
import com.gp2u.lite.view.CustomIncomingMessageViewHolder;
import com.gp2u.lite.view.CustomOutcomingMessageViewHolder;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.com.temasys.skylink.sdk.listener.FileTransferListener;
import sg.com.temasys.skylink.sdk.listener.MessagesListener;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConnection;
import sg.com.temasys.skylink.sdk.rtc.SkylinkException;

import static com.gp2u.lite.model.Message.RECEIVER_FILE_PROGRESS;
import static com.gp2u.lite.model.Message.RECEIVER_FILE_REJECTED;
import static com.gp2u.lite.model.Message.RECEIVER_FILE_REQUEST;
import static com.gp2u.lite.utils.Utils.getDownloadedFilePath;
import static com.gp2u.lite.utils.Utils.getFileExt;
import static com.gp2u.lite.utils.Utils.getPeerIdNick;
import static com.gp2u.lite.utils.Utils.getRandomId;
import static com.gp2u.lite.utils.Utils.isImage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MessageFragment extends Fragment implements MessagesListener ,FileTransferListener{

    @BindView(R.id.messagesList)
    MessagesList messagesList;

    @BindView(R.id.input)
    MessageInput input;

    MessagesListAdapter<Message> messagesAdapter;
    String senderId = "me";
    public String userName;
    public SkylinkConnection skylinkConnection;

    private static String TAG = VideoChatActivity.class.getName();

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
        Message message1 = new Message(Utils.getRandomId() , new User("remote" ,"peer"), Config.WELCOME_MESSAGE);
        messagesAdapter.addToStart(message1, true);
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

    private void initAdapter()
    {
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.with(getActivity()).load(url).into(imageView);
            }
        };

        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(CustomIncomingMessageViewHolder.class, R.layout.item_custom_incoming_text_message)
                .setOutcomingTextConfig(CustomOutcomingMessageViewHolder.class, R.layout.item_custom_outcoming_text_message);
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

        addListeners();
        messagesList.setAdapter(messagesAdapter);
    }

    private void initInput(){

        input.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence charSequence) {

                Message message = new Message(getRandomId() , new User(senderId ,userName),charSequence.toString());
                messagesAdapter.addToStart(message, true);
                try {
                    skylinkConnection.sendP2PMessage(null ,charSequence.toString());
                } catch (SkylinkException e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                return true;
            }
        });

        input.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {

                ImagePicker imagePicker = ImagePicker.create(getActivity())
                        .returnAfterFirst(false) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
                        .folderMode(true) // set folder mode (false by default)
                        .folderTitle("Folder") // folder selection title
                        .imageTitle("Tap to select"); // image selection title
                imagePicker.single();
                imagePicker.limit(1) // max images can be selected (99 by default)
                        .showCamera(false) // show camera or not (true by default)
                        .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                        .imageFullDirectory(Environment.getExternalStorageDirectory().getPath()) // can be full path
                        .start(1000); // start image picker activity with request code
            }
        });
    }

    private void showAlert(String message)
    {
        // show alert if self is not shown
        boolean isShow = ((VideoChatActivity)getActivity()).isShownMessage();
        if (isShow){

            ((VideoChatActivity)getActivity()).unreadMessages = 0;

        }
        else {
            ((VideoChatActivity)getActivity()).unreadMessages++;
            ((VideoChatActivity)getActivity()).showAlert(message);
        }
    }

    @Override
    public void onServerMessageReceive(String remotePeerId, Object message, boolean b) {

        if (message instanceof String) {

            Message message1 = new Message(Utils.getRandomId() , new User(remotePeerId ,"peer"),(String)message);
            messagesAdapter.addToStart(message1, true);

            showAlert((String)message);
        }
    }

    @Override
    public void onP2PMessageReceive(String remotePeerId, Object message, boolean b) {

        if (message instanceof String) {

            Message message1 = new Message(Utils.getRandomId() , new User(remotePeerId ,"peer"),(String)message);
            messagesAdapter.addToStart(message1, true);

            showAlert((String)message);
        }
    }

    @Override
    public void onFileTransferPermissionRequest(String remotePeerId, String fileName, boolean isPrivate) {


        Message message1 = new Message(Utils.getRandomId() , new User(remotePeerId ,"peer"),String.format("Imcoming File Transfer Request!\n File:%s" ,fileName));
        message1.isFile = true;
        message1.status = RECEIVER_FILE_REQUEST;
        message1.peerId = remotePeerId;
        message1.filename = fileName;
        message1.downloadfilename = getDownloadedFilePath(fileName);
        messagesAdapter.addToStart(message1, true);

        showAlert(String.format("Imcoming File Transfer Request!\n File:%s" ,fileName));

    }

    @Override
    public void onFileTransferPermissionResponse(String remotePeerId, String fileName, boolean isPermitted) {

        if (isPermitted){

            ArrayList<Message> messageArrayList = messagesAdapter.getAllMessages();
            ListIterator li = messageArrayList.listIterator(messageArrayList.size());
            boolean keep = true;
            while (li.hasPrevious() && keep){

                Message message = (Message) li.previous();
                if (message.isFile && message.status == Message.SEND_FILE_REQUEST)
                {
                    message.status = Message.SEND_FILE_PROGRESS;
                    messagesAdapter.update(message);
                    keep = false;
                }
            }
        }
    }

    @Override
    public void onFileTransferDrop(String remotePeerId, String fileName, String message, boolean isExplicit) {

        Message message1 = new Message(Utils.getRandomId() , new User(remotePeerId ,"peer"),message);
        messagesAdapter.addToStart(message1, true);
    }

    @Override
    public void onFileSendComplete(String remotePeerId, String fileName) {

        ArrayList<Message> messageArrayList = messagesAdapter.getAllMessages();
        ListIterator li = messageArrayList.listIterator(messageArrayList.size());
        boolean keep = true;
        while (li.hasPrevious() && keep){

            Message message = (Message) li.previous();
            if (message.isFile && message.status == Message.SEND_FILE_PROGRESS)
            {
                message.status = Message.SEND_FILE_COMPLETE;
                messagesAdapter.update(message);
                keep = false;
            }
        }
    }

    @Override
    public void onFileSendProgress(String remotePeerId, String fileName, double percentage) {

        ArrayList<Message> messageArrayList = messagesAdapter.getAllMessages();
        ListIterator li = messageArrayList.listIterator(messageArrayList.size());
        boolean keep = true;
        while (li.hasPrevious() && keep){

            Message message = (Message) li.previous();
            if (message.isFile && message.status == Message.SEND_FILE_PROGRESS)
            {
                message.progress = (float)percentage;
                messagesAdapter.update(message);
                keep = false;
            }
        }
    }

    @Override
    public void onFileReceiveComplete(String remotePeerId, String fileName) {

        ArrayList<Message> messageArrayList = messagesAdapter.getAllMessages();
        ListIterator li = messageArrayList.listIterator(messageArrayList.size());
        boolean keep = true;
        while (li.hasPrevious() && keep){

            Message message = (Message) li.previous();
            if (message.isFile && message.status == RECEIVER_FILE_PROGRESS)
            {
                message.status = Message.RECEIVER_FILE_COMPLETE;
                messagesAdapter.update(message);
                keep = false;
            }
        }
    }

    @Override
    public void onFileReceiveProgress(String remotePeerId, String fileName, double percentage) {

        ArrayList<Message> messageArrayList = messagesAdapter.getAllMessages();
        ListIterator li = messageArrayList.listIterator(messageArrayList.size());
        boolean keep = true;
        while (li.hasPrevious() && keep){

            Message message = (Message) li.previous();
            if (message.isFile && message.status == RECEIVER_FILE_PROGRESS)
            {
                message.progress = (float)percentage;
                messagesAdapter.update(message);
                keep = false;
            }
        }
    }

    private void addListeners()
    {
        messagesAdapter.registerViewClickListener(R.id.accept_btn, new MessagesListAdapter.OnMessageViewClickListener<Message>() {
            @Override
            public void onMessageViewClick(View view, Message message) {

                try {
                    skylinkConnection.sendFileTransferPermissionResponse(message.peerId , message.downloadfilename,true);
                    message.status = RECEIVER_FILE_PROGRESS;
                    messagesAdapter.update(message);

                } catch (SkylinkException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        messagesAdapter.registerViewClickListener(R.id.reject_btn, new MessagesListAdapter.OnMessageViewClickListener<Message>() {
            @Override
            public void onMessageViewClick(View view, Message message) {

                try {
                    skylinkConnection.sendFileTransferPermissionResponse(message.peerId , message.downloadfilename,false);
                    message.status = RECEIVER_FILE_REJECTED;
                    messagesAdapter.update(message);

                    Message message1 = new Message(Utils.getRandomId() , new User(senderId ,"peer"), String.format("Sorry , file transfer rejected by %s" ,getPeerIdNick(message.peerId)));
                    messagesAdapter.addToStart(message1, true);

                } catch (SkylinkException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        messagesAdapter.registerViewClickListener(R.id.view_btn, new MessagesListAdapter.OnMessageViewClickListener<Message>() {
            @Override
            public void onMessageViewClick(View view, Message message) {

                if (isImage(message.filename)){

                    new ImageViewer.Builder<>(getActivity(), new String[]{
                            String.format("file://%s" ,message.downloadfilename)})
                            .setBackgroundColor(Color.parseColor("#dedede"))
                            .setStartPosition(0)
                            .show();
                }
                else {

                    MimeTypeMap myMime = MimeTypeMap.getSingleton();
                    Intent newIntent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(message.downloadfilename);
                    String mimeType = myMime.getMimeTypeFromExtension(getFileExt(message.downloadfilename));
                    newIntent.setDataAndType(Uri.fromFile(file), mimeType);
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        messagesAdapter.registerViewClickListener(R.id.save_btn, new MessagesListAdapter.OnMessageViewClickListener<Message>() {
            @Override
            public void onMessageViewClick(View view, Message message) {

                Log.d(TAG ,"Save Clicked");
            }
        });

        messagesAdapter.registerViewClickListener(R.id.messageText, new MessagesListAdapter.OnMessageViewClickListener<Message>() {
            @Override
            public void onMessageViewClick(View view, Message message) {

                String[] urls = extractLinks(message.getText());
                if (urls.length > 0){

                    VideoChatActivity activity = (VideoChatActivity) getActivity();
                    activity.openWebView(urls[0]);
                }

            }
        });
    }

    public static String[] extractLinks(String text) {
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        List<String> links = new ArrayList<>();
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            String url = m.group();
            Log.d(TAG, "URL extracted: " + url);
            links.add(url);
        }

        return links.toArray(new String[links.size()]);
    }

    public void sendImage(Image image)
    {

        try {
            skylinkConnection.sendFileTransferPermissionRequest(null ,image.getName() ,image.getPath());

            Message message1 = new Message(Utils.getRandomId() , new User(senderId ,"me"),String.format("You're sending file:%s" ,image.getName()));
            message1.isFile = true;
            message1.status = Message.SEND_FILE_REQUEST;
            messagesAdapter.addToStart(message1, true);

        } catch (SkylinkException e) {
            String exMsg = e.getMessage();
            Message message = new Message(Utils.getRandomId(), new User(senderId, "me"), exMsg);
            messagesAdapter.addToStart(message, true);
        }
    }

}
