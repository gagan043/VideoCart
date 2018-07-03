package com.admin.videocart.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.videocart.GlobalConstant.ConstantClass;
import com.admin.videocart.GlobalConstant.UrlConstants;
import com.admin.videocart.R;
import com.admin.videocart.utils.CommonUtils;
import com.admin.videocart.utils.PreferenceUtilis;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.admin.videocart.R.layout.activity_sign_in;

public class SignInActivity extends Activity implements OnClickListener {
    ImageView img_back;
    RelativeLayout rel_fbView, rel_googleView, rel_create;
    EditText ed_passwordView, ed_emailView;
    Button btn_signin;
    TextView txt_forgot;
    String email_mString = "", pass_mString = "";
    CustomProgressDialog mDialog;
    PreferenceUtilis preference;
    CallbackManager callbackManager;
    LoginButton Login_TV;
    String token;
    String name_mString,image_mString,id_mString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_sign_in);
        callbackManager = CallbackManager.Factory.create();
        init();
        setListener();
    }

    public void init() {
        Login_TV = (LoginButton) findViewById(R.id.Fb_Login);
        Login_TV.setReadPermissions(Arrays.asList("public_profile, email,user_birthday"));
        fbMethod();
        mDialog=new CustomProgressDialog(this);
        preference=new PreferenceUtilis(this);
        img_back = (ImageView) findViewById(R.id.img_back);
        rel_fbView = (RelativeLayout) findViewById(R.id.rel_fbView);
        rel_googleView = (RelativeLayout) findViewById(R.id.rel_googleView);
        rel_create = (RelativeLayout) findViewById(R.id.rel_create);
        ed_passwordView = (EditText) findViewById(R.id.ed_passwordView);
        ed_emailView = (EditText) findViewById(R.id.ed_emailView);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        txt_forgot = (TextView) findViewById(R.id.txt_forgot);


    }

    public void setListener() {
        img_back.setOnClickListener(this);
        rel_create.setOnClickListener(this);
        btn_signin.setOnClickListener(this);
        txt_forgot.setOnClickListener(this);
        rel_fbView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_create:
                openActivity(SignUpActivity.class);
                break;
            case R.id.btn_signin:
                email_mString = ed_emailView.getText().toString();
                pass_mString = ed_passwordView.getText().toString();
                if(CommonUtils.getConnectivityStatus(this)) {
                    if (email_mString.length() == 0) {
                        ed_emailView.setError("Please enter phone or email");
                    } else if (pass_mString.length() == 0) {
                        ed_passwordView.setError("Please enter password");

                    } /*else if (pass_mString.length() < 6) {
                        ed_passwordView.setError("Password lenght must be six character");

                    }*/ else {
                        JSONObject js = new JSONObject();
                        try {
                            js.put(ConstantClass.EMAIL, email_mString);
                            js.put(ConstantClass.PASSWORD, pass_mString);
                            mDialog.setUpDialog();
                            login(js);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }else{
                    CommonUtils.openInternetDialog(this);
                }
                break;
            case R.id.txt_forgot:
                openActivity(ForgetActivity.class);
                break;
            case R.id.rel_fbView:
                if (CommonUtils.getConnectivityStatus(SignInActivity.this)) {
                    Login_TV.performClick();
                } else {
                    CommonUtils.openInternetDialog(SignInActivity.this);

                }
                break;
        }

    }

    public void openActivity(Class c) {
        Intent i = new Intent(this, c);
        startActivity(i);
        finish();

    }

    //-------------------------Login api method------------------------
    public void login(final JSONObject json1)
    {
        String URL= UrlConstants.LOGINURL;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,json1, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject s)
            {
                Log.e("send response", String.valueOf(s));
                try {
                    String status = s.getString("status");
                    String message=s.getString("message");
                    if (status.equalsIgnoreCase("1"))
                    {

                        JSONObject jsonobj=s.getJSONObject("data");
                        String id=jsonobj.getString("id");

                       preference.setUserId(id);
                       openActivity(SignUpActivity.class);


                    }

                    mDialog.dismissDialog();
                    Toast.makeText(SignInActivity.this,message, Toast.LENGTH_SHORT).show();

                }
                catch (JSONException e)
                {

                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mDialog.dismissDialog();
                        Log.e("Error: ",volleyError.toString()+json1 );
                    }


                }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rQueue = Volley.newRequestQueue(SignInActivity.this);
        rQueue.add(request);
    }

    //---------------------------facebook method------------------------------
    public void fbMethod() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                token = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        // Application code
                        Log.e("print", object.toString());

                        try {

                            name_mString = object.getString("name");
                            id_mString = object.getString("id");



                            if (object.has("email")) {
                                email_mString = object.getString("email");

                            } else {
                                email_mString = "";
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "picture.type(large),id,email,name,gender");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }


}
