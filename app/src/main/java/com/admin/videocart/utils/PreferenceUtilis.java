package com.admin.videocart.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.admin.videocart.GlobalConstant.ConstantClass;
import com.admin.videocart.R;

public class PreferenceUtilis {
    Context c;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    public PreferenceUtilis(Context c) {
        this.c=c;
        sp=c.getSharedPreferences(c.getResources().getString(R.string.app_name),Context.MODE_PRIVATE);
        ed=sp.edit();
    }
    public  void setUserId(String user_id){
        ed.putString(ConstantClass.USERID,user_id);
        ed.commit();
    }
    public  String getUserId(){

        return sp.getString(ConstantClass.USERID,"");
    }

    public  void setName(String name){
        ed.putString(ConstantClass.NAME,name);
        ed.commit();
    }
    public  String getName(){

        return sp.getString(ConstantClass.NAME,"");
    }
}
