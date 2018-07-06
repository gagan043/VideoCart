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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import retrofit2.Call;

import io.fabric.sdk.android.Fabric;

import static com.admin.videocart.R.layout.activity_sign_in;

public class SignInActivity extends Activity implements OnClickListener {
    ImageView img_back;
    RelativeLayout rel_fbView, rel_twitterView, rel_create, rel_googleView;
    EditText ed_passwordView, ed_emailView;
    Button btn_signin;
    TextView txt_forgot;
    String email_mString = "", pass_mString = "";
    CustomProgressDialog mDialog;
    PreferenceUtilis preference;
    Global global;
    //----------FACEBOOK VARIABLE
    CallbackManager callbackManager;
    LoginButton Login_TV;
    String token;
    String name_mString = "", image_mString = "", id_mString = "";

    //----------TWITER VARIABLE----------

    TwitterLoginButton twitter_sign_in_button;
    //-------------Google variable----------
    SignInButton mGoogleSignInButton;
    private static final int RC_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_sign_in);

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

        rel_googleView = (RelativeLayout) findViewById(R.id.rel_googleView);
        img_back = (ImageView) findViewById(R.id.img_back);
        rel_fbView = (RelativeLayout) findViewById(R.id.rel_fbView);
        rel_twitterView = (RelativeLayout) findViewById(R.id.rel_twitterView);
        rel_create = (RelativeLayout) findViewById(R.id.rel_create);
        ed_passwordView = (EditText) findViewById(R.id.ed_passwordView);
        ed_emailView = (EditText) findViewById(R.id.ed_emailView);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        txt_forgot = (TextView) findViewById(R.id.txt_forgot);
        callTwitterLogin();

    }

    public void setListener() {
        img_back.setOnClickListener(this);
        rel_create.setOnClickListener(this);
        btn_signin.setOnClickListener(this);
        txt_forgot.setOnClickListener(this);
        rel_fbView.setOnClickListener(this);
        rel_twitterView.setOnClickListener(this);
        rel_googleView.setOnClickListener(this);
    }

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
                if (CommonUtils.getConnectivityStatus(this)) {
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
                } else {
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
            case R.id.rel_twitterView:
                if (CommonUtils.getConnectivityStatus(SignInActivity.this)) {

                    twitter_sign_in_button.performClick();


                } else {
                    CommonUtils.openInternetDialog(SignInActivity.this);

                }
                break;
            case R.id.rel_googleView:
                if (CommonUtils.getConnectivityStatus(SignInActivity.this)) {
                    signInWithGoogle();

                    // mGoogleSignInButton.performClick();

                } else {
                    CommonUtils.openInternetDialog(SignInActivity.this);

                }
                break;
        }

    }

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(TwitterException e) {
                // handleSignInResult(...);
                final Uri marketUri = Uri.parse("market://details?id=com.twitter.android");
                startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
                //Toast.makeText(SignInActivity.this,"Please install first twitter app",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openActivity(Class c) {
        Intent i = new Intent(this, c);
        startActivity(i);


    }

    //-------------------------Login api method------------------------
    public void login(final JSONObject json1) {
        String URL = UrlConstants.LOGINURL;
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
                        String name = jsonobj.getString("name");

                        preference.setUserId(id);
                        preference.setName(name);

                        openActivity(SignUpActivity.class);


                    }

                    mDialog.dismissDialog();
                    Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //handleSignInResult(...)
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
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            Log.e("error", "BUNDLE Exception : " + e.toString());
        }


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
                        openActivity(SignUpActivity.class);


                    }

                    mDialog.dismissDialog();
                    Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();

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
        RequestQueue rQueue = Volley.newRequestQueue(SignInActivity.this);
        rQueue.add(request);
    }

}
