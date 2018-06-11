package com.gp2u.lite.utils;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.gp2u.lite.view.CCView;

public class CircleAnimation extends Animation{

    private CCView arcView;

    private float oldAngle;
    private float newAngle;

    public CircleAnimation(CCView arcView, int newAngle) {
        this.oldAngle = arcView.getArcAngle();
        this.newAngle = newAngle;
        this.arcView = arcView;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = 0 + ((newAngle - oldAngle) * interpolatedTime);

        arcView.setArcAngle(angle);
        arcView.requestLayout();
    }
}
