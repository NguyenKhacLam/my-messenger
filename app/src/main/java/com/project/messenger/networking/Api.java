package com.project.messenger.networking;

import com.project.messenger.models.ApiResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface Api {

    @POST("send")
    Call<String> sendRemoteMessage(@HeaderMap HashMap<String, String> headers, @Body String remoteBody);

}
