package com.admin.videocart.activity;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.admin.videocart.R;
import com.admin.videocart.utils.PreferenceUtilis;

public class BuyerProfileActivity extends Activity implements View.OnClickListener
{

    RelativeLayout rel_chngepsd;
TextView txt_name;
    PreferenceUtilis preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_profile);
        init();
        setListener();
        addDataInView();
    }

    public void init()
    {
        preference=new PreferenceUtilis(this);
        rel_chngepsd=(RelativeLayout)findViewById(R.id.rel_chngepsd);
        txt_name=(TextView)findViewById(R.id.txt_name);
    }
    public void addDataInView() {
        txt_name.setText(preference.getName());

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
