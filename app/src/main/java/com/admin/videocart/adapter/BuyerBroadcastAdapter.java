package com.admin.videocart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.videocart.R;
import com.admin.videocart.holder.BuyerBroadcastModel;
import com.admin.videocart.holder.BuyerOrderModel;
import com.joooonho.SelectableRoundedImageView;

import java.util.List;

public class BuyerBroadcastAdapter extends RecyclerView.Adapter<BuyerBroadcastAdapter.MyViewHolder>
{
    private List<BuyerBroadcastModel> moviesList;
    Context context;
    // MxVideoPlayerWidget videoPlayerWidget;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name;
        ImageView img_video;

        public MyViewHolder(View view)
        {
            super(view);
            // videoPlayerWidget = (MxVideoPlayerWidget) view.findViewById(R.id.videoplayer);
            name = (TextView) view.findViewById(R.id.txt_name);
            img_video=(ImageView)view.findViewById(R.id.img_video);
        }
    }

    public BuyerBroadcastAdapter(Context context,List<BuyerBroadcastModel> moviesList)
    {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buyer_broadcast_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        final BuyerBroadcastModel broad = moviesList.get(position);
        holder.name.setText(broad.getName());
    }

    @Override
    public int getItemCount()
    {
        return moviesList.size();
    }
}