package com.admin.videocart.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.videocart.R;

public class BuyerSettingActivity extends Activity implements View.OnClickListener
{
    RelativeLayout rel_order,rel_wishlist,rel_share,rel_feed,rel_address;
    TextView txt_myaccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_setting);
        init();
        setListeners();
    }

    public void init()
    {
        rel_order=(RelativeLayout)findViewById(R.id.rel_order);
        rel_wishlist=(RelativeLayout)findViewById(R.id.rel_wish);
        txt_myaccount=(TextView)findViewById(R.id.txt_account);
        rel_share=(RelativeLayout)findViewById(R.id.rel_share);
        rel_feed=(RelativeLayout)findViewById(R.id.rel_feed);
        rel_address=(RelativeLayout)findViewById(R.id.rel_address);
        txt_myaccount=(TextView)findViewById(R.id.txt_myaccount);
    }

    public void setListeners()
    {
       rel_order.setOnClickListener(this);
       rel_wishlist.setOnClickListener(this);
       txt_myaccount.setOnClickListener(this);
       rel_share.setOnClickListener(this);
       rel_feed.setOnClickListener(this);
       rel_address.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.rel_order:
                Intent intentorder=new Intent(getApplicationContext(),BuyerOrderActivity.class);
                startActivity(intentorder);
                break;
            case R.id.rel_wish:
                Intent intentwish=new Intent(getApplicationContext(),BuyerWishlistActivity.class);
                startActivity(intentwish);
                break;
            case R.id.txt_myaccount:
                Intent intentprofile=new Intent(getApplicationContext(),BuyerProfileActivity.class);
                startActivity(intentprofile);
                break;
            case R.id.rel_share:
                shareDialog();
                break;
            case R.id.rel_feed:
                Intent intentfeed=new Intent(getApplicationContext(),BuyerSendFeedbackActivity.class);
                startActivity(intentfeed);
                break;
            case R.id.rel_address:
                Intent intentaddress=new Intent(getApplicationContext(),AddAddressActivity.class);
                startActivity(intentaddress);
                break;

        }
    }

    public void shareDialog()
    {
        final Dialog openDialog = new Dialog(BuyerSettingActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        openDialog.setContentView(R.layout.buyer_share_dialog);
        ImageView img_cross=(ImageView)openDialog.findViewById(R.id.img_cross);
        //Button btn_subscribe=(Button)openDialog.findViewById(R.id.btn_subscribe);
        Button btn_share = (Button) openDialog.findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Toast.makeText(getApplicationContext(),"share",Toast.LENGTH_SHORT).show();
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
