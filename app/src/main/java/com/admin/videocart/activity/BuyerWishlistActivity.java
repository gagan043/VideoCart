package com.admin.videocart.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.admin.videocart.R;
import com.admin.videocart.adapter.BuyerOrderAdapter;
import com.admin.videocart.adapter.BuyerWishlistAdapter;
import com.admin.videocart.holder.BuyerOrderModel;
import com.admin.videocart.holder.BuyerWishlistModel;

import java.util.ArrayList;

public class BuyerWishlistActivity extends Activity
{
    RecyclerView recyclerView;
    BuyerWishlistAdapter buyerWishlistAdapter;
    ArrayList<BuyerWishlistModel> wishlist=new ArrayList<>();
    String name []={"Wide Leg Red Silk Pants","Wide Leg Red Silk Pants"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_wishlist);
        init();
    }

    public void init()
    {
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        for(int i=0;i<=name.length-1;i++)
        {
            wishlist.add(new BuyerWishlistModel(name[i],"Red","EMI","$197.0"));
        }
        buyerWishlistAdapter = new BuyerWishlistAdapter(BuyerWishlistActivity.this, wishlist);
        buyerWishlistAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(buyerWishlistAdapter);
    }

}
