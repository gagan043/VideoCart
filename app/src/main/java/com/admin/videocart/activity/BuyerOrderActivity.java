package com.admin.videocart.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.admin.videocart.R;
import com.admin.videocart.adapter.BuyerBroadcastAdapter;
import com.admin.videocart.adapter.BuyerOrderAdapter;
import com.admin.videocart.holder.BuyerBroadcastModel;
import com.admin.videocart.holder.BuyerOrderModel;

import java.util.ArrayList;

public class BuyerOrderActivity extends Activity {

    RecyclerView recyclerView;
    BuyerOrderAdapter buyerOrderAdapter;
    ArrayList<BuyerOrderModel> orderlist=new ArrayList<>();
    String name []={"Wide Leg Red Silk Pants","Wide Leg Red Silk Pants"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_order);
        init();
    }

    public void init()
    {
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        for(int i=0;i<=name.length-1;i++)
        {
            orderlist.add(new BuyerOrderModel(name[i],"Red","In Progress"));
        }
        buyerOrderAdapter = new BuyerOrderAdapter(BuyerOrderActivity.this, orderlist);
        buyerOrderAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(buyerOrderAdapter);
    }
}
