package com.project.messenger.networking;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiBuilder {
    private static Api api;
    public static Api getInstance(){

        if (api == null){
            api = new Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
                    .create(Api.class);
        }
        return api;
    }
}
