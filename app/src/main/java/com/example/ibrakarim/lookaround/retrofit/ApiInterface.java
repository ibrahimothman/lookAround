package com.example.ibrakarim.lookaround.retrofit;

import com.example.ibrakarim.lookaround.model.NearbyPlaces;
import com.example.ibrakarim.lookaround.model.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET
    public Call<NearbyPlaces> getNearbyPlaces (@Url String url);

    @GET
    public Call<PlaceDetail> getPlaceDetail (@Url String url);
}
