package com.admin.videocart.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class Fonts {
    public static void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenir_book.ttf"));
            }
            else if (v instanceof EditText) {
                ((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenir_book.ttf"));
            }
        } catch (Exception e) {
        }
    }

    public static void overrideFonts1(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenir_medium.ttf"));
            }
            else if (v instanceof EditText) {
                ((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenir_medium.ttf"));
            }
        } catch (Exception e) {
        }
    }
    public static void overrideFontHeavy(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenir_heavy.ttf"));
            }
            else if (v instanceof EditText) {
                ((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenir_heavy.ttf"));
            }
        } catch (Exception e) {
        }
    }
}
