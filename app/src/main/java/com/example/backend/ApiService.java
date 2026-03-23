package com.example.backend;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("/")
    Call<ResponseBody> checkApi();

    @POST("/register")
    Call<ResponseMessage> register(@Body User user);

    @POST("/login")
    Call<ResponseMessage> login(@Body User user);
}
