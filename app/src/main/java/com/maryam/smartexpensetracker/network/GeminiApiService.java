package com.maryam.smartexpensetracker.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GeminiApiService {

    @POST("v1beta/models/{model}:generateContent")
    Call<ResponseBody> generateContent(
            @Path("model") String model,
            @Header("x-goog-api-key") String apiKey,
            @Body GeminiRequest request
    );
}