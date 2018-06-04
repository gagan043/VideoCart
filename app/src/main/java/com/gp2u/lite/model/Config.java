package com.gp2u.lite.model;

import com.gp2u.lite.BuildConfig;

/**
 * Created by mac on 19/10/2017.
 */

public class Config {

    public static String APP_KEY = "533772a2-0be8-4215-9c10-cdf7028744ad";
    public static String APP_KEY_SECRET = "0sfba0o377m0a";
    public static String APP_KEY_DESCRIPTION = "Android";
    public static boolean IS_APP_KEY_SMR = false;

    public static final String ROOM_NAME = "RoomName";
    public static final String USER_NAME = "UserName";
    public static final String UUID = "uuid";
    public static final String bPermissionAsked = "bPermissionAsked";

    private static final String         SUBDOMAIN = BuildConfig.subdomain;
    public static final String          WEBRTC_DOMAIN  = SUBDOMAIN + "gp2u.com.au";
    public static final String          MAIN_DOMAIN = "https://" + WEBRTC_DOMAIN;

    public static final String ROOM_NOT_FOUND  =  "Room not found\nPress green phone to connect anyway";
    public static final String ROOM_NOT_FOUND1 =  "Show room not found";
    public static final String WELCOME_MESSAGE = "Welcome!\nClick the remote\nuser to hide.\nClick top right\nagain to show.";
    public static final String OPEN_MESSAGE_ERROR = "There is nobody connected to chat to!";
    public static final String CHECKING_ROOM_INFO =  "Checking info";

    public static final String INTERNET_ERROR = "No Internet Connection";
}
