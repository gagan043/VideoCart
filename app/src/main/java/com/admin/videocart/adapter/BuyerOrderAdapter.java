package com.admin.videocart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.admin.videocart.R;
import com.admin.videocart.holder.BuyerBroadcastModel;
import com.admin.videocart.holder.BuyerOrderModel;
import com.admin.videocart.holder.BuyerWishlistModel;
import com.joooonho.SelectableRoundedImageView;

import java.util.List;

public class BuyerOrderAdapter extends RecyclerView.Adapter<BuyerOrderAdapter.MyViewHolder> {

    private List<BuyerOrderModel> orderList;
    Context context;
    // MxVideoPlayerWidget videoPlayerWidget;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name, color,emi,progress;
        SelectableRoundedImageView img_video;

        public MyViewHolder(View view) {
            super(view);
            // videoPlayerWidget = (MxVideoPlayerWidget) view.findViewById(R.id.videoplayer);
            name = (TextView) view.findViewById(R.id.txt_name);
            color = (TextView) view.findViewById(R.id.txt_color);
            emi = (TextView) view.findViewById(R.id.txt_emi);
            progress = (TextView) view.findViewById(R.id.txt_progress);
            img_video = (SelectableRoundedImageView) view.findViewById(R.id.img_video);
        }
    }

    public BuyerOrderAdapter(Context context,List<BuyerOrderModel> orderList) {
        this.orderList = orderList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buyer_order_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        final BuyerOrderModel order = orderList.get(position);
        holder.name.setText(order.getName());
        holder.color.setText(order.getColor());
        holder.progress.setText(order.getProgress());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}