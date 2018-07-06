package com.admin.videocart.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.videocart.R;
import com.admin.videocart.utils.CommonUtils;
import com.admin.videocart.utils.PreferenceUtilis;

public class BuyerSettingActivity extends Activity implements View.OnClickListener {
    RelativeLayout rel_order, rel_wishlist, rel_share, rel_feed, rel_address, rel_logout,rel_rate;
    TextView txt_myaccount, txt_name, txt_skill;
    PreferenceUtilis preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_buyer_setting);
        init();
        setListeners();
        addDataInView();
    }

    public void init() {
        preference = new PreferenceUtilis(this);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_skill = (TextView) findViewById(R.id.txt_skill);
        rel_order = (RelativeLayout) findViewById(R.id.rel_order);
        rel_wishlist = (RelativeLayout) findViewById(R.id.rel_wish);
        txt_myaccount = (TextView) findViewById(R.id.txt_account);
        rel_share = (RelativeLayout) findViewById(R.id.rel_share);
        rel_feed = (RelativeLayout) findViewById(R.id.rel_feed);
        rel_address = (RelativeLayout) findViewById(R.id.rel_address);
        txt_myaccount = (TextView) findViewById(R.id.txt_myaccount);
        rel_logout = (RelativeLayout) findViewById(R.id.rel_logout);
        rel_rate=(RelativeLayout) findViewById(R.id.rel_rate);
    }

    public void addDataInView() {
        txt_name.setText(preference.getName());
        txt_skill.setText(preference.getEmail());

    }

    public void setListeners() {
        rel_logout.setOnClickListener(this);
        rel_order.setOnClickListener(this);
        rel_wishlist.setOnClickListener(this);
        txt_myaccount.setOnClickListener(this);
        rel_share.setOnClickListener(this);
        rel_feed.setOnClickListener(this);
        rel_address.setOnClickListener(this);
        rel_rate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_order:
                Intent intentorder = new Intent(getApplicationContext(), BuyerOrderActivity.class);
                startActivity(intentorder);
                break;
            case R.id.rel_wish:
                Intent intentwish = new Intent(getApplicationContext(), BuyerWishlistActivity.class);
                startActivity(intentwish);
                break;
            case R.id.txt_myaccount:
                Intent intentprofile = new Intent(getApplicationContext(), BuyerProfileActivity.class);
                startActivity(intentprofile);
                break;
            case R.id.rel_share:
                shareDialog();
                break;
            case R.id.rel_feed:
                Intent intentfeed = new Intent(getApplicationContext(), BuyerSendFeedbackActivity.class);
                startActivity(intentfeed);
                break;
            case R.id.rel_address:
                Intent intentaddress = new Intent(getApplicationContext(), AddAddressActivity.class);
                startActivity(intentaddress);
                break;
            case R.id.rel_logout:
                if(preference.getLoginType().equalsIgnoreCase("facebook")){
                    LoginManager.getInstance().logOut();
                }
                preference.clearCache();
                openActivity(WelcomeActivity.class);
                break;
            case R.id.rel_rate:
                final Uri marketUri = Uri.parse("market://details?id=com.twitter.android");
                startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
                break;
        }
    }

    public void openActivity(Class c) {
        if (CommonUtils.getConnectivityStatus(this)) {
            Intent i = new Intent(this, c);
            startActivity(i);
            finish();
        } else {
            CommonUtils.openInternetDialog(this);
        }
    }

    public void shareDialog() {
        final Dialog openDialog = new Dialog(BuyerSettingActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        openDialog.setContentView(R.layout.buyer_share_dialog);
        ImageView img_cross = (ImageView) openDialog.findViewById(R.id.img_cross);
        //Button btn_subscribe=(Button)openDialog.findViewById(R.id.btn_subscribe);
        Button btn_share = (Button) openDialog.findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "share", Toast.LENGTH_SHORT).show();
                //openDialog.dismiss();
            }
        });
        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.dismiss();
            }
        });
        openDialog.show();
    }
}
