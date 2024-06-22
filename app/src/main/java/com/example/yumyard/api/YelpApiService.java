package com.example.yumyard.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YelpApiService {
    @GET("businesses/search")
    Call<YelpSearchResult> searchBusinesses(
            @Header("Authorization") String authHeader,
            @Query("location") String location,
            @Query("term") String term,
            @Query("sort_by") String sortBy,
            @Query("radius") int radius,
            @Query("categories") String categories,
            @Query("price") String price
    );

    @GET("businesses/{id}")
    Call<YelpBusinessDetail> getBusinessDetails(
            @Header("Authorization") String authHeader,
            @Path("id") String id
    );
}
