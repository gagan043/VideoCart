package com.admin.videocart.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.admin.videocart.R;
import com.admin.videocart.holder.BuyerWishlistModel;
import com.joooonho.SelectableRoundedImageView;

import java.io.File;
import java.util.List;

public class BuyerWishlistAdapter extends RecyclerView.Adapter<BuyerWishlistAdapter.MyViewHolder> {

    private List<BuyerWishlistModel> moviesList;
    Context context;
    // MxVideoPlayerWidget videoPlayerWidget;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, time,emi,price;
        SelectableRoundedImageView img_video;

        public MyViewHolder(View view) {
            super(view);
            // videoPlayerWidget = (MxVideoPlayerWidget) view.findViewById(R.id.videoplayer);
            name = (TextView) view.findViewById(R.id.txt_name);
            time = (TextView) view.findViewById(R.id.txt_color);
            emi = (TextView) view.findViewById(R.id.txt_emi);
            price = (TextView) view.findViewById(R.id.txt_price);
            img_video = (SelectableRoundedImageView) view.findViewById(R.id.img_video);
        }
    }


    public BuyerWishlistAdapter(Context context,List<BuyerWishlistModel> moviesList) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buyer_wishlist_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}