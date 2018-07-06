package com.admin.videocart.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.admin.videocart.R;

public class BuyerProfileActivity extends Activity implements View.OnClickListener
{

    RelativeLayout rel_chngepsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_profile);
        init();
        setListener();
    }

    public void init()
    {
        rel_chngepsd=(RelativeLayout)findViewById(R.id.rel_chngepsd);
    }

    public void setListener()
    {
        rel_chngepsd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.rel_chngepsd:
                Intent intentpswd=new Intent(getApplicationContext(),ChangePswdActivity.class);
                startActivity(intentpswd);
                break;
        }
    }
}
