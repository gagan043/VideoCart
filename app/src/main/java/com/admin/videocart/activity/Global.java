package com.admin.videocart.activity;

import android.app.Application;
import android.util.Log;

import com.admin.videocart.R;
import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;


import io.fabric.sdk.android.Fabric;
import org.brickred.socialauth.android.SocialAuthAdapter;

public class Global extends Application {
    public SocialAuthAdapter getSocialAuthAdpater() {
        return socialAuthAdpater;
    }

    public void setSocialAuthAdpater(SocialAuthAdapter socialAuthAdpater) {
        this.socialAuthAdpater = socialAuthAdpater;
    }

    SocialAuthAdapter socialAuthAdpater;
    @Override
    public void onCreate() {
        super.onCreate();


        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET));
        Fabric.with(this, new TwitterCore(authConfig), new Crashlytics());

    }
}
