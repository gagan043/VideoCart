package com.gp2u.lite.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface APICallback {

    public void doNext(JsonObject jsonObject);
    public void doNext(JsonArray jsonObject);
    public void doNext(String str);

    public void doCompleted();
    public void doError(Throwable e);
}
