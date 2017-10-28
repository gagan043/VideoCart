package com.gp2u.lite.model;

import android.support.annotation.IntDef;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/*
 * Created by troy379 on 04.04.17.
 */

public class Message implements IMessage,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType /*and this one is for custom content type (in this case - voice message)*/ {

    private String id;
    private String text;
    private Date createdAt;
    private User user;
    private Image image;

    public String filename;
    public String downloadfilename;
    public String peerId;
    public float progress;
    public boolean isFile;

    public static final int RECEIVER_FILE_REQUEST = 0;
    public static final int RECEIVER_FILE_ACCEPTED = 1;
    public static final int RECEIVER_FILE_REJECTED = 2;
    public static final int RECEIVER_FILE_PROGRESS = 3;
    public static final int RECEIVER_FILE_COMPLETE = 4;
    public static final int SEND_FILE_REQUEST = 5;
    public static final int SEND_FILE_PROGRESS = 6;
    public static final int SEND_FILE_COMPLETE = 7;

    @IntDef({RECEIVER_FILE_REQUEST ,RECEIVER_FILE_ACCEPTED ,RECEIVER_FILE_REJECTED ,RECEIVER_FILE_PROGRESS ,RECEIVER_FILE_COMPLETE ,SEND_FILE_REQUEST ,SEND_FILE_PROGRESS ,SEND_FILE_COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FileStatus{}

    public  @FileStatus int status;

    public Message(String id, User user, String text) {
        this(id, user, text, new Date());
    }

    public Message(String id, User user, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

}
