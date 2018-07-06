package com.admin.videocart.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.admin.videocart.R;

public class BuyerSendFeedbackActivity extends Activity implements View.OnClickListener
{
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_send_feedback);
        init();
        setListeners();
    }

    public void init()
    {
        btn_submit=(Button)findViewById(R.id.btn_submit);
    }

    public void setListeners()
    {
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
    switch (v.getId())
    {
        case R.id.btn_submit:
            thankyouDialog();
            break;
    }
    }

    public void thankyouDialog()
    {
        @SuppressLint("ResourceType") final Dialog openDialog = new Dialog(BuyerSendFeedbackActivity.this, getResources().getColor(R.color.white));
       // openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.));
        openDialog.setContentView(R.layout.buyer_fdb_thankyou_dialog);
        Button btn_close= (Button) openDialog.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openDialog.dismiss();
            }
        });
        openDialog.show();
    }
}
