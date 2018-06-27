package com.gp2u.lite.control;

import com.google.gson.JsonObject;

public interface APICallback {

    public void doNext(JsonObject jsonObject);
    public void doCompleted();
    public void doError(Throwable e);
}
