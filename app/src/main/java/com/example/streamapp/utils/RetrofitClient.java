package com.example.streamapp.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Avani
 */
public class RetrofitClient {
    private static RetrofitClient instance = null;
    public static String BASE_URL = "https://groundbreakers.loca.lt";
    public static String VIDEO_BASE_URL = "https://3910-2405-201-2010-2065-3d88-112f-4441-1988.ngrok-free.app/";
    private Api myApi;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(Api.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public Api getMyApi() {
        return myApi;
    }

    public static String getVideoUrl(String id, String fileName){
       return RetrofitClient.VIDEO_BASE_URL + id+"/"+fileName;
    }
}
