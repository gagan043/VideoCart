package com.admin.videocart.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.admin.videocart.R;
import com.admin.videocart.utils.CommonUtils;

public class WelcomeActivity extends Activity implements View.OnClickListener {
    Button btn_signin, btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
        setListner();
    }

    public void init() {
        btn_signin = (Button) findViewById(R.id.btn_signin);
        btn_signup = (Button) findViewById(R.id.btn_signup);
    }

    public void setListner() {
        btn_signin.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:
                openActivity(SignInActivity.class);
                break;
            case R.id.btn_signup:
                openActivity(SignUpActivity.class);
                break;
        }
    }

    public void openActivity(Class c){
        if(CommonUtils.getConnectivityStatus(this)) {
            Intent i = new Intent(this, c);
            startActivity(i);
        }else{
            CommonUtils.openInternetDialog(this);
        }
    }
}
