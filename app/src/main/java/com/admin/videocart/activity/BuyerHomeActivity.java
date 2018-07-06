package com.admin.videocart.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.admin.videocart.R;
import com.admin.videocart.adapter.BuyerBroadcastAdapter;
import com.admin.videocart.holder.BuyerBroadcastModel;

import java.util.ArrayList;

public class BuyerHomeActivity extends Activity {

    RecyclerView recyclerView;
    BuyerBroadcastAdapter buyerBroadcastAdapter;
    ArrayList<BuyerBroadcastModel> broadlist=new ArrayList<>();
    String name []={"Fashion Sale Like Never Before","Watches Up to 50% off","Fashion Sale Like Never Before","Watches Up to 50% off"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_home);
    }
}
