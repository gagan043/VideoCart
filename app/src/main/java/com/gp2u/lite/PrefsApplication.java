package com.gp2u.lite;

import android.app.Application;
import android.content.ContextWrapper;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by mac on 18/10/2017.
 */

public class PrefsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        Fresco.initialize(this);

    }
}
