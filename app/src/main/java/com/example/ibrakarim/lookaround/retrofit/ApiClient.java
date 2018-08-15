package com.example.ibrakarim.lookaround.retrofit;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://maps.googleapis.com/";
    public static final String PLACE_DETAIL_BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static Retrofit retrofit = null;

    public static Retrofit getApiClient(){
        if(retrofit == null){
            Log.d("fromApiClient","getApiClient");
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}
