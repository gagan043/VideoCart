package com.gp2u.lite.control;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiEndpointInterface {

    @GET("/rpc/user_from_room")
    Observable<JsonObject> getUserName(@Query("room") String room);

}
