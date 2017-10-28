package com.gp2u.lite.utils;

import android.os.Environment;

import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

import sg.com.temasys.skylink.sdk.rtc.SkylinkConnection;

/**
 * Created by mac on 28/10/2017.
 */

public class Utils {

    public static String getDownloadedFilePath(String filename) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return path.getAbsolutePath() + File.separator + filename;
    }

    public static String getRandomId() {
        return Long.toString(UUID.randomUUID().getLeastSignificantBits());
    }

    public static  String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
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
        String peerIdNick = getUserDataString(peerId);
        return peerIdNick;
    }

    public static String getUserDataString(String peerId) {
        JSONObject userDataObject = (JSONObject)SkylinkConnection.getInstance().getUserData(peerId);
        String userDataString = "";
        if (userDataObject != null) {
            userDataString = userDataObject.optString("name");
        }
        return userDataString;
    }

}
