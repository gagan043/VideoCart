package com.admin.videocart.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.admin.videocart.R;
import com.admin.videocart.utils.CommonUtils;
import com.admin.videocart.utils.PreferenceUtilis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends Activity
{
    PreferenceUtilis preference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preference=new PreferenceUtilis(this);
        allPermission();
    }

    public void allPermission() {

        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        String coarselocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
        String networkPermission = Manifest.permission.ACCESS_NETWORK_STATE;
        String wstorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String rstorage = Manifest.permission.READ_EXTERNAL_STORAGE;

       /* String cameraPermission = Manifest.permission.CAMERA;

        String readContactPermission = Manifest.permission.READ_CONTACTS;*/

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasFinePermission = SplashActivity.this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarsePermission = SplashActivity.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasaccessnetworkState = SplashActivity.this.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
            int haswstorage = SplashActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasrstorage = SplashActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

           /* int hascameraPermission = SplashActivity.this.checkSelfPermission(Manifest.permission.CAMERA);

            int hasReadContact = SplashActivity.this.checkSelfPermission(Manifest.permission.READ_CONTACTS);
*/

            List<String> permissions = new ArrayList<String>();
            if (hasFinePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(locationPermission);
            }
            if (hasCoarsePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(coarselocationPermission);
            }
            if (hasaccessnetworkState != PackageManager.PERMISSION_GRANTED) {
                permissions.add(networkPermission);
            }
            if (haswstorage != PackageManager.PERMISSION_GRANTED) {
                permissions.add(wstorage);
            }
            if (hasrstorage != PackageManager.PERMISSION_GRANTED) {
                permissions.add(rstorage);
            }
           /* if (hascameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(cameraPermission);
            }


            if (hasReadContact != PackageManager.PERMISSION_GRANTED) {
                permissions.add(readContactPermission);
            }*/
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, 0);

            } else {



                splashThread();
            }
        } else {



            splashThread();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

               /* perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);*/

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

                        /*&& perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED*/) {
                    // All Permissions Granted



                    splashThread();

                } else {
                    // Permission Denied
                    Toast.makeText(SplashActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT);

                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void splashThread() {
        //hashkey();
        if(CommonUtils.getConnectivityStatus(SplashActivity.this)) {

            if (preference.getUserId().equalsIgnoreCase("")) {
                Handler splashhandler = new Handler();
                splashhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                        startActivity(i);
                        finish();


                    }
                }, 2000);
            } else {
                Handler splashhandler = new Handler();
                splashhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent i = new Intent(SplashActivity.this, BuyerTabActivity.class);
                        startActivity(i);
                        finish();


                    }
                }, 2000);

            }
        }else{
            CommonUtils.openInternetDialog(SplashActivity.this);
        }

    }
    public void hashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.admin.videocart", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                //textInstructionsOrLink = (TextView)findViewById(R.id.textstring);
                //textInstructionsOrLink.setText(sign);
                Log.e("Hash Key", sign);
                Toast.makeText(getApplicationContext(), sign, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("nope", "nope");
        } catch (NoSuchAlgorithmException e) {
        }
    }
}
