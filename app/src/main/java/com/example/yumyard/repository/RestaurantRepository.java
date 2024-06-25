package com.example.yumyard.repository;

import android.util.Log;

import com.example.yumyard.api.YelpApiService;
import com.example.yumyard.api.YelpSearchResult;
import com.example.yumyard.api.YelpBusinessDetail;
import com.example.yumyard.model.Restaurant;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantRepository {
    private static final String TAG = "RestaurantRepository";
    private FirebaseFirestore db;
    private YelpApiService yelpApiService;

    public interface RestaurantCallback {
        void onSuccess(Restaurant restaurant);
        void onFailure(Exception e);
    }

    public interface RestaurantListCallback {
        void onSuccess(List<Restaurant> restaurants);
        void onFailure(Exception e);
    }

    public RestaurantRepository() {
        db = FirebaseFirestore.getInstance();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.yelp.com/v3/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        yelpApiService = retrofit.create(YelpApiService.class);
    }

    public void searchRestaurants(String location, String term, String price, String categories, final RestaurantListCallback callback) {
        String bearerToken = "Bearer QBGT8Eq52mLCSGJj0xr_5jXALbUtLaiko1mDijx68pCsT40634LbVSSQCuNdFulX9mkJJcG_avpRSzIeOfX4eXDnuRGVJ1EAv8eYvgUjgSmQWsZLddmeiFI0nkR0ZnYx"; // Replace with your Yelp API key

        Log.d(TAG, "Querying Yelp API with Location: " + location + ", Term: " + term + ", Price: " + price + ", Categories: " + categories);

        Call<YelpSearchResult> call = yelpApiService.searchBusinesses(
                bearerToken, location, term, "best_match", 300, categories.isEmpty() ? "restaurants" : categories, price.isEmpty() ? null : price);

        call.enqueue(new Callback<YelpSearchResult>() {
            @Override
            public void onResponse(Call<YelpSearchResult> call, Response<YelpSearchResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Restaurant> restaurants = new ArrayList<>();
                    for (YelpSearchResult.Business business : response.body().businesses) {
                        Restaurant restaurant = new Restaurant();
                        restaurant.setRestaurantId(business.id);
                        restaurant.setName(business.name);
                        restaurant.setImageUrl(business.image_url); // Set the image URL
                        restaurant.setAddress(business.location.address1);
                        restaurants.add(restaurant);
                    }
                    Log.d(TAG, "Yelp API response successful: " + restaurants.size() + " restaurants found.");
                    callback.onSuccess(restaurants);
                } else {
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Yelp API response error: " + errorResponse);
                        callback.onFailure(new Exception("Yelp API call failed: " + errorResponse));
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error response", e);
                        callback.onFailure(new Exception("Yelp API call failed: Unable to read error response"));
                    }
                }
            }

            @Override
            public void onFailure(Call<YelpSearchResult> call, Throwable t) {
                Log.e(TAG, "Error fetching restaurants from Yelp", t);
                callback.onFailure(new Exception("Error fetching restaurants from Yelp", t));
            }
        });
    }

    public void getRestaurantDetailsFromYelp(String restaurantId, final RestaurantCallback callback) {
        String bearerToken = "Bearer QBGT8Eq52mLCSGJj0xr_5jXALbUtLaiko1mDijx68pCsT40634LbVSSQCuNdFulX9mkJJcG_avpRSzIeOfX4eXDnuRGVJ1EAv8eYvgUjgSmQWsZLddmeiFI0nkR0ZnYx";

        Call<YelpBusinessDetail> call = yelpApiService.getBusinessDetails(bearerToken, restaurantId);
        call.enqueue(new Callback<YelpBusinessDetail>() {
            @Override
            public void onResponse(Call<YelpBusinessDetail> call, Response<YelpBusinessDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    YelpBusinessDetail businessDetail = response.body();
                    Restaurant restaurant = new Restaurant();
                    restaurant.setRestaurantId(businessDetail.getId());
                    restaurant.setName(businessDetail.getName());
                    restaurant.setAddress(businessDetail.getLocation().getAddress1());
                    callback.onSuccess(restaurant);
                } else {
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Yelp API response error: " + errorResponse);
                        callback.onFailure(new Exception("Yelp API call failed: " + errorResponse));
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error response", e);
                        callback.onFailure(new Exception("Yelp API call failed: Unable to read error response"));
                    }
                }
            }

            @Override
            public void onFailure(Call<YelpBusinessDetail> call, Throwable t) {
                Log.e(TAG, "Error fetching restaurant details from Yelp", t);
                callback.onFailure(new Exception("Error fetching restaurant details from Yelp", t));
            }
        });
    }
}
