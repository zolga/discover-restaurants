package com.olgazelenko.doordash.network;

import com.olgazelenko.doordash.model.Restaurant;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface GetDataService {

    @GET("restaurant/")
    Call<List<Restaurant>> getRestaurantList(
            @QueryMap Map<String, Object> map
    );
}