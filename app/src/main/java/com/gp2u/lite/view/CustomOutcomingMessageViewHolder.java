package com.gp2u.lite.view;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gp2u.lite.R;
import com.gp2u.lite.model.Global;
import com.gp2u.lite.model.Message;
import com.stfalcon.chatkit.messages.MessageHolders;

import static com.gp2u.lite.model.Message.SEND_FILE_COMPLETE;
import static com.gp2u.lite.model.Message.SEND_FILE_PROGRESS;

/**
 * Created by mac on 27/10/2017.
 */

public class CustomOutcomingMessageViewHolder extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    private TextView statusText;
    private TextView messageUsername;
    private ProgressBar uploadProgressBar;

    public CustomOutcomingMessageViewHolder(View itemView) {
        super(itemView);

        uploadProgressBar = itemView.findViewById(R.id.progress_bar);
        statusText = itemView.findViewById(R.id.status_text);
        messageUsername = itemView.findViewById(R.id.messageUsername);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

        statusText.setVisibility(View.GONE);
        uploadProgressBar.setVisibility(View.GONE);
        messageUsername.setText(Global.userName);

        if (!message.isFile) return;

        switch (message.status){

            case SEND_FILE_PROGRESS:
            {
                uploadProgressBar.setVisibility(View.VISIBLE);
                uploadProgressBar.setProgress((int)(message.progress));
            }
            break;
            case SEND_FILE_COMPLETE:
            {
                statusText.setVisibility(View.VISIBLE);
            }
            break;
        }
    }
}
