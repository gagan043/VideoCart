package com.gp2u.lite.view;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gp2u.lite.R;
import com.gp2u.lite.model.Global;
import com.gp2u.lite.model.Message;
import com.stfalcon.chatkit.messages.MessageHolders;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.gp2u.lite.model.Message.RECEIVER_FILE_ACCEPTED;
import static com.gp2u.lite.model.Message.RECEIVER_FILE_COMPLETE;
import static com.gp2u.lite.model.Message.RECEIVER_FILE_PROGRESS;
import static com.gp2u.lite.model.Message.RECEIVER_FILE_REJECTED;
import static com.gp2u.lite.model.Message.RECEIVER_FILE_REQUEST;

/**
 * Created by mac on 27/10/2017.
 */

public class CustomIncomingMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<Message>{


    private View preView;
    private View nextView;
    private TextView peerUsername;
    private ProgressBar downloadProgressBar;
    private FancyButton acceptBtn;
    private FancyButton rejectBtn;
    private FancyButton saveBtn;
    private FancyButton viewBtn;

    public CustomIncomingMessageViewHolder(View itemView) {
        super(itemView);

        preView = itemView.findViewById(R.id.pre_layout);
        nextView = itemView.findViewById(R.id.next_layout);
        peerUsername = itemView.findViewById(R.id.peerUsername);
        downloadProgressBar = itemView.findViewById(R.id.progress_bar);
        acceptBtn = itemView.findViewById(R.id.accept_btn);
        rejectBtn = itemView.findViewById(R.id.reject_btn);
        viewBtn = itemView.findViewById(R.id.view_btn);
        saveBtn = itemView.findViewById(R.id.save_btn);
    }

    @Override
    public void onBind(final Message message) {
        super.onBind(message);

        preView.setVisibility(View.GONE);
        nextView.setVisibility(View.GONE);
        downloadProgressBar.setVisibility(View.GONE);
        // we store the peerId => username mapping in a Global because it's sensible...
        peerUsername.setText(Global.peerInfo.getProperty(message.getUser().getId()));

        if (!message.isFile) return;


        switch (message.status){

            case RECEIVER_FILE_REQUEST:
            {
                preView.setVisibility(View.VISIBLE);
            }
                break;
            case RECEIVER_FILE_ACCEPTED:
            {

            }
                break;
            case RECEIVER_FILE_REJECTED:
            {

            }
                break;
            case RECEIVER_FILE_PROGRESS:
            {
                downloadProgressBar.setVisibility(View.VISIBLE);
                downloadProgressBar.setProgress((int)(message.progress));
            }
                break;
            case RECEIVER_FILE_COMPLETE:
            {
                nextView.setVisibility(View.VISIBLE);
            }
                break;
        }
    }

}
