package com.admin.videocart.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.admin.videocart.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BuyerTabActivity extends TabActivity implements TabLayout.OnTabSelectedListener{

    TabHost tabHost;

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_tab);
        //  i = getIntent();
        tabHost = getTabHost();
        setTabs();

        for (int j = 0; j < tabHost.getTabWidget().getChildCount(); j++)
        {
            ViewGroup vg = (ViewGroup) tabHost.getTabWidget().getChildAt(j);

            ImageView img = (ImageView) vg.getChildAt(0);
            TextView txt=(TextView)vg.getChildAt(1);
            if(j==0)
            {
                txt.setTextColor(getResources().getColor(R.color.appcolor));
            }
            else
            {
                txt.setTextColor(getResources().getColor(R.color.gray));
            }
        }

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {

                int index = tabHost.getCurrentTab();
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    ViewGroup vg = (ViewGroup) tabHost.getTabWidget().getChildAt(i);
                    //vg.setBackgroundColor(Color.TRANSPARENT);
                    ImageView img = (ImageView) vg.getChildAt(0);
                    TextView txt=(TextView)vg.getChildAt(1);
                    if(index==i)
                    {
                        txt.setTextColor(getResources().getColor(R.color.appcolor));
                    }
                    else
                    {
                        txt.setTextColor(getResources().getColor(R.color.gray));
                    }
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    private void setTabs()
    {
        addTab(R.drawable.myproducts, BuyerProfileActivity.class,"HOME");
        addTab(R.drawable.changepwd, BuyerShopActivity.class,"SHOP");
        addTab(R.drawable.legal, BuyerOrderActivity.class,"CART");
        addTab(R.drawable.rate, BuyerSettingActivity.class,"SETTINGS");
    }

    private void addTab(int drawableId, Class<?> c,String s) {


        Intent intent = new Intent(this, c);

        TabHost.TabSpec spec = tabHost.newTabSpec(s);

        View v = LayoutInflater.from(this).inflate(R.layout.custom_tab, getTabWidget(), false);

        TextView title = (TextView) v.findViewById(R.id.title);

        title.setText(s);

        ImageView icon = (ImageView) v.findViewById(R.id.icon);

        icon.setImageResource(drawableId);
        spec.setIndicator(v);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }

    public void setTabValue()
    {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        // int i=tab.getPosition();

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
