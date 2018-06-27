package com.gp2u.lite.control;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.gp2u.lite.model.Config;
import com.pixplicity.easyprefs.library.Prefs;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class APIService {

    private static APIService ourInstance = new APIService();
    private ApiEndpointInterface apiService;
    private APICallback callback;

    public static APIService getInstance() {
        return ourInstance;
    }

    private APIService()
    {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.MAIN_DOMAIN + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxAdapter)
                .build();

        apiService = retrofit.create(ApiEndpointInterface.class);
    }

    public void setOnCallback(APICallback callback)
    {
        this.callback = callback;
    }


    public Subscription getUserName(String room)
    {
        final Observable<JsonObject> call = apiService.getUserName(room);
        return call
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                        callback.doCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
                        Log.e("error" ,e.getLocalizedMessage());
                        callback.doError(e);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {

                        Log.d("response" ,jsonObject.toString());
                        callback.doNext(jsonObject);
                    }
                });

    }

    public Subscription logConnection(int peerCount ,String status ,String error)
    {
        String room = Prefs.getString(Config.ROOM_NAME ,"");
        String username = Prefs.getString(Config.USER_NAME ,"");
        final Observable<JsonObject> call = apiService.logConnection(room ,username ,String.valueOf(peerCount) ,status ,error);
        return call
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {

                        Log.d("response" ,jsonObject.toString());
                    }
                });

    }
}
