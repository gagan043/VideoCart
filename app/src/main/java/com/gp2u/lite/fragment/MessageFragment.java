package com.gp2u.lite.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.gp2u.lite.R;
import com.gp2u.lite.model.Message;
import com.gp2u.lite.model.fixtures.MessagesFixtures;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MessageFragment extends Fragment implements MessagesListAdapter.OnLoadMoreListener{

    @BindView(R.id.cancel_button)
    Button cancelBtn;

    @BindView(R.id.messagesList)
    MessagesList messagesList;

    @BindView(R.id.input)
    MessageInput input;

    MessagesListAdapter<Message> messagesAdapter;
    private Date lastLoadedDate;

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

        messagesAdapter.addToStart(MessagesFixtures.getTextMessage(), true);
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
        String senderId = "0";
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.with(getActivity()).load(url).into(imageView);
            }
        };
        messagesAdapter = new MessagesListAdapter<>(senderId, imageLoader);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {

                    }
                });
        messagesList.setAdapter(messagesAdapter);
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (totalItemsCount < 20) {
            loadMessages();
        }
    }

    protected void loadMessages() {
        new Handler().postDelayed(new Runnable() { //imitation of internet connection
            @Override
            public void run() {
                ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
                lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
                messagesAdapter.addToEnd(messages, false);
            }
        }, 1000);
    }
}
