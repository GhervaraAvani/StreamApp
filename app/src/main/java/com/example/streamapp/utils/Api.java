package com.example.streamapp.utils;

import com.example.streamapp.model.MediaObject;
import com.example.streamapp.model.Time;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Avani
 */
public interface Api {


    @GET("video")
    Call<ArrayList<MediaObject>> getAllVideoList();

    @POST("video/{id}/history")
    Call<Response> cacheHistory(@Path("id")String id, @Body Time objTime);
}
