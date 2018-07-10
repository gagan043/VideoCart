package com.admin.videocart.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends Activity implements View.OnClickListener
{
    ImageView img_back,img_seller,img_buyer;
    RelativeLayout rel_fbView, rel_twitterView, rel_googleView,rel_sel,rel_buy,rel_signin;
    EditText ed_passwordView, ed_emailView,ed_nameView,ed_phoneView;
    Button btn_signup;

    String email_mString = "", pass_mString = "",name_mString="", image_mString="", id_mString="",phone_mString="";
    CustomProgressDialog mDialog;
    PreferenceUtilis preference;
    Global global;
    //----------FACEBOOK VARIABLE
    CallbackManager callbackManager;
    LoginButton Login_TV;
    String token;


    //----------TWITER VARIABLE----------

    TwitterLoginButton twitter_sign_in_button;
    //-------------Google variable----------
    SignInButton mGoogleSignInButton;
    private static final int RC_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_sign_up);

        callbackManager = CallbackManager.Factory.create();


        // client = new TwitterAuthClient();
        init();
        setListener();
    }

    public void init() {
        Login_TV = (LoginButton) findViewById(R.id.Fb_Login);
        Login_TV.setReadPermissions(Arrays.asList("public_profile, email"));
        fbMethod();

        global = (Global) getApplicationContext();
        mDialog = new CustomProgressDialog(this);
        preference = new PreferenceUtilis(this);
        twitter_sign_in_button = (TwitterLoginButton) findViewById(R.id.twitter_sign_in_button);
        rel_signin=(RelativeLayout) findViewById(R.id.rel_signin);
        rel_googleView = (RelativeLayout) findViewById(R.id.rel_googleView);
        img_back = (ImageView) findViewById(R.id.img_back);
        rel_fbView = (RelativeLayout) findViewById(R.id.rel_fbView);
        rel_twitterView = (RelativeLayout) findViewById(R.id.rel_twitterView);
        rel_sel= (RelativeLayout) findViewById(R.id.rel_sel);
        rel_buy= (RelativeLayout) findViewById(R.id.rel_buy);
        ed_passwordView = (EditText) findViewById(R.id.ed_passwordView);
        ed_emailView = (EditText) findViewById(R.id.ed_emailView);
        ed_nameView= (EditText) findViewById(R.id.ed_nameView);
        ed_phoneView=(EditText)findViewById(R.id.ed_phoneView);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        img_seller=(ImageView)findViewById(R.id.img_seller) ;
        img_buyer=(ImageView)findViewById(R.id.img_buyer) ;
        callTwitterLogin();

    }

    public void setListener() {
        img_back.setOnClickListener(this);

        btn_signup.setOnClickListener(this);

        rel_fbView.setOnClickListener(this);
        rel_twitterView.setOnClickListener(this);
        rel_googleView.setOnClickListener(this);
        rel_sel.setOnClickListener(this);
        rel_buy.setOnClickListener(this);
        rel_signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_signin:

                finish();
                break;
            case R.id.rel_sel:
                img_seller.setImageResource(R.drawable.selleractive);
                img_buyer.setImageResource(R.drawable.buyer);

                break;
            case R.id.rel_buy:
                img_seller.setImageResource(R.drawable.seller);
                img_buyer.setImageResource(R.drawable.buyeractive);

                break;
            case R.id.img_back:
                finish();
                break;
           
            case R.id.btn_signup:
                email_mString = ed_emailView.getText().toString();
                pass_mString = ed_passwordView.getText().toString();
                name_mString=ed_nameView.getText().toString();
                phone_mString=ed_phoneView.getText().toString();
                if (CommonUtils.getConnectivityStatus(this)) {
                    if (name_mString.length() == 0) {
                        ed_nameView.setError("Please enter name");

                    }else if (email_mString.length() == 0) {
                        ed_emailView.setError("Please enter email");
                    }
                     else if (pass_mString.length() == 0) {
                        ed_passwordView.setError("Please enter password");

                    } else if (phone_mString.length() == 0) {
                        ed_phoneView.setError("Please enter phone number");

                    } else if (!CommonUtils.isEmailValid(email_mString)) {
                        ed_emailView.setError("Please enter correct email");

                    }
                    else {
                        mDialog.setUpDialog();
                        sendImageWithName();
                    }
                } else {
                    CommonUtils.openInternetDialog(this);
                }
                break;

            case R.id.rel_fbView:
                if (CommonUtils.getConnectivityStatus(SignUpActivity.this)) {
                    Login_TV.performClick();
                } else {
                    CommonUtils.openInternetDialog(SignUpActivity.this);

                }
                break;
            case R.id.rel_twitterView:
                if (CommonUtils.getConnectivityStatus(SignUpActivity.this)) {

                    twitter_sign_in_button.performClick();


                } else {
                    CommonUtils.openInternetDialog(SignUpActivity.this);

                }
                break;
            case R.id.rel_googleView:
                if (CommonUtils.getConnectivityStatus(SignUpActivity.this)) {
                    signInWithGoogle();

                    // mGoogleSignInButton.performClick();

                } else {
                    CommonUtils.openInternetDialog(SignUpActivity.this);

                }
                break;
        }

    }
    
    //-----------Google method--------------
    private void signInWithGoogle() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //------------Twitter login method----------
    public void callTwitterLogin() {
        twitter_sign_in_button.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> result) {
                // handleSignInResult(...);
                JSONObject js = new JSONObject();
                try {
                    js.put(ConstantClass.EMAIL, "");
                    js.put(ConstantClass.NAME, result.data.getUserName());
                    js.put(ConstantClass.SOCIALTYPE, "twitter");
                    js.put(ConstantClass.IMAGE, image_mString);
                    js.put(ConstantClass.DATEOFBIRTH, "");
                    js.put(ConstantClass.TOKEN, result.data.getAuthToken());

                    mDialog.setUpDialog();
                    Sociallogin(js);
                    preference.setEmail("");
                    preference.setName(result.data.getUserName());
                    preference.setLoginType("twitter");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(TwitterException e) {
                // handleSignInResult(...);
                final Uri marketUri = Uri.parse("market://details?id=com.twitter.android");
                startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
            }
        });
    }

    public void openActivity(Class c) {
        Intent i = new Intent(this, c);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(i);
        

    }



    //---------------------------facebook method------------------------------
    public void fbMethod() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                token = loginResult.getAccessToken().getToken();
                Log.e("token", token);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        // Application code
                        Log.e("respone", response.toString());
                        getFacebookData(object);


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "picture.type(large),id,name,gender,email");
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
        if (TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE == requestCode) {
            twitter_sign_in_button.onActivityResult(requestCode, resultCode, data);
        }
        // Pass the activity result to the twitterAuthClient.
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                final GoogleApiClient client = mGoogleApiClient;
                Log.e("data", "" + result.getSignInAccount().getPhotoUrl());

                JSONObject js = new JSONObject();
                try {
                    js.put(ConstantClass.EMAIL, result.getSignInAccount().getEmail());
                    js.put(ConstantClass.NAME, result.getSignInAccount().getDisplayName());
                    js.put(ConstantClass.SOCIALTYPE, "google");
                    js.put(ConstantClass.IMAGE, result.getSignInAccount().getPhotoUrl());
                    js.put(ConstantClass.DATEOFBIRTH, "");
                    js.put(ConstantClass.TOKEN, result.getSignInAccount().getId());

                    mDialog.setUpDialog();
                    Sociallogin(js);
                    preference.setEmail(result.getSignInAccount().getEmail());
                    preference.setName(result.getSignInAccount().getDisplayName());
                    preference.setLoginType("gmail");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //handleSignInResult(...);
            }
        } else {
            // Handle other values for requestCode
        }
    }


    //-----------------------social data method---------------
    private void getFacebookData(JSONObject object) {


        try {
            String id = object.getString("id");
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");

            } catch (MalformedURLException e) {
                e.printStackTrace();

            }
            Log.e("print", object.toString());
            name_mString = object.getString("name");
            id_mString = object.getString("id");


            if (object.has("email")) {
                email_mString = object.getString("email");

            } else {
                email_mString = "";
            }

            JSONObject js = new JSONObject();
            try {
                js.put(ConstantClass.EMAIL, email_mString);
                js.put(ConstantClass.NAME, name_mString);
                js.put(ConstantClass.SOCIALTYPE, "facebook");
                js.put(ConstantClass.IMAGE, image_mString);
                js.put(ConstantClass.DATEOFBIRTH, "");
                js.put(ConstantClass.TOKEN, token);

                mDialog.setUpDialog();
                Sociallogin(js);
                preference.setEmail(email_mString);
                preference.setName(name_mString);
                preference.setLoginType("facebook");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("error", "BUNDLE Exception : " + e.toString());
        }


    }

    public void sendImageWithName() {

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, UrlConstants.SIGNUPURl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("status", response);
                        try {
                            JSONObject s=new JSONObject(response);
                            String status = s.getString("status");
                            String message = s.getString("message");
                            if (status.equalsIgnoreCase("1")) {

                                JSONObject jsonobj = s.getJSONObject("data");
                                String id = jsonobj.getString("id");
                                preference.setName(jsonobj.getString(ConstantClass.NAME));
                                preference.setEmail(jsonobj.getString(ConstantClass.EMAIL));
                                preference.setLoginType("");
                                openActivity(BuyerTabActivity.class);


                            }

                            mDialog.dismissDialog();
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {

                            e.printStackTrace();
                            mDialog.dismissDialog();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismissDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                Log.e("headers", headers.toString());
                return headers;

            }
        };
        smr.addStringParam(ConstantClass.NAME, name_mString);
        smr.addFile(ConstantClass.IMAGE, image_mString);
        smr.addStringParam(ConstantClass.EMAIL, email_mString);
        smr.addStringParam(ConstantClass.PASSWORD, pass_mString);
        smr.addStringParam(ConstantClass.DATEOFBIRTH, phone_mString);


        smr.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(smr);

    }

    //-------------------------Social Login api method------------------------
    public void Sociallogin(final JSONObject json1) {
        String URL = UrlConstants.SOCIALLOGIN;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json1, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject s) {
                Log.e("send response", String.valueOf(s));
                try {
                    String status = s.getString("status");
                    String message = s.getString("message");
                    if (status.equalsIgnoreCase("1")) {

                        JSONObject jsonobj = s.getJSONObject("data");
                        String id = jsonobj.getString("id");

                        preference.setUserId(id);
                        openActivity(BuyerTabActivity.class);


                    }

                    mDialog.dismissDialog();
                    Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mDialog.dismissDialog();
                        Log.e("Error: ", volleyError.toString() + json1);
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
        RequestQueue rQueue = Volley.newRequestQueue(SignUpActivity.this);
        rQueue.add(request);
    }

}
