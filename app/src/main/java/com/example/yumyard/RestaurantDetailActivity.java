package com.example.yumyard;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.yumyard.adapter.ImagePagerAdapter;
import com.example.yumyard.api.YelpApiService;
import com.example.yumyard.api.YelpBusinessDetail;
import com.example.yumyard.model.Review;
import com.example.yumyard.adapter.ReviewAdapter;

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

public class RestaurantDetailActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantDetailActivity";
    private YelpApiService yelpApiService;
    private TextView restaurantName;
    private TextView restaurantAddress;
    private TextView restaurantPhone;
    private TextView restaurantPrice;
    private ViewPager imagePager;
    private RecyclerView reviewsRecyclerView;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        restaurantName = findViewById(R.id.restaurant_name);
        restaurantAddress = findViewById(R.id.restaurant_address);
        restaurantPhone = findViewById(R.id.restaurant_phone);
        restaurantPrice = findViewById(R.id.restaurant_price);
        imagePager = findViewById(R.id.image_pager);
        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view);

        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        reviewsRecyclerView.setAdapter(reviewAdapter);

        String restaurantId = getIntent().getStringExtra("RESTAURANT_ID");

        if (restaurantId != null) {
            fetchRestaurantDetails(restaurantId);
            fetchRestaurantReviews(restaurantId);
        } else {
            Log.e(TAG, "No restaurant ID provided.");
        }
    }

    private void fetchRestaurantDetails(String restaurantId) {
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

        String bearerToken = "Bearer QBGT8Eq52mLCSGJj0xr_5jXALbUtLaiko1mDijx68pCsT40634LbVSSQCuNdFulX9mkJJcG_avpRSzIeOfX4eXDnuRGVJ1EAv8eYvgUjgSmQWsZLddmeiFI0nkR0ZnYx"; // Replace with your Yelp API key

        Call<YelpBusinessDetail> call = yelpApiService.getBusinessDetails(bearerToken, restaurantId);
        call.enqueue(new Callback<YelpBusinessDetail>() {
            @Override
            public void onResponse(Call<YelpBusinessDetail> call, Response<YelpBusinessDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    YelpBusinessDetail businessDetail = response.body();
                    restaurantName.setText(businessDetail.getName());
                    restaurantAddress.setText(businessDetail.getLocation().getAddress1());
                    restaurantPhone.setText(businessDetail.getPhone());
                    restaurantPrice.setText(businessDetail.getPrice());

                    ImagePagerAdapter adapter = new ImagePagerAdapter(RestaurantDetailActivity.this, businessDetail.getPhotos());
                    imagePager.setAdapter(adapter);
                } else {
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Yelp API response error: " + errorResponse);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<YelpBusinessDetail> call, Throwable t) {
                Log.e(TAG, "Error fetching restaurant details from Yelp", t);
            }
        });
    }

    private void fetchRestaurantReviews(String restaurantId) {
        // TODO: Implement review fetching from database
    }
}
