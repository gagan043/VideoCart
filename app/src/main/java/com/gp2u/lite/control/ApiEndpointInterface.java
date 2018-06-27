package com.gp2u.lite.control;

import com.google.gson.JsonObject;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiEndpointInterface {

    @GET("/rpc/user_from_room")
    Observable<JsonObject> getUserName(@Query("room") String room);

    @FormUrlEncoded
    @POST("/rpc/connection_log")
    Observable<JsonObject> logConnection(@Field("room") String room ,@Field("username") String username ,@Field("peer_count") String peerCount ,@Field("status") String status ,@Field("error_message") String errorMessage);
}
