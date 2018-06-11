package com.gp2u.lite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.gp2u.lite.R;
import com.gp2u.lite.control.AudioRouter;
import com.gp2u.lite.utils.CircleAnimation;
import com.gp2u.lite.view.CCView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 5000;

    @BindView(R.id.ccView)
    CCView ccView;

    @BindView(R.id.cc_imageview)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);
        AudioRouter.startAudioRouting(this);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this,PermissionActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                AlphaAnimation alphaAnimation1 = new AlphaAnimation(1.0f ,0.0f);
                alphaAnimation1.setDuration(100);
                alphaAnimation1.setFillAfter(true);
                imageView.startAnimation(alphaAnimation1);
            }
        }, 4400);

        CircleAnimation animation = new CircleAnimation(ccView, 300);
        animation.setDuration(1000);
        ccView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // start animation cross
                ccView.crossAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f ,1.0f);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setStartOffset(1500);
        alphaAnimation.setFillAfter(true);
        imageView.startAnimation(alphaAnimation);
    }
}
