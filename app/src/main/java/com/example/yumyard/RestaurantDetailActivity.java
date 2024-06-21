package com.example.yumyard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.yumyard.adapter.ImagePagerAdapter;
import com.example.yumyard.adapter.ReviewAdapter;
import com.example.yumyard.api.YelpApiService;
import com.example.yumyard.api.YelpBusinessDetail;
import com.example.yumyard.model.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private FirebaseFirestore db;
    private Button heartButton;
    private boolean isFavorite = false;
    private TextView noReviewsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        db = FirebaseFirestore.getInstance();

        restaurantName = findViewById(R.id.restaurant_name);
        restaurantAddress = findViewById(R.id.restaurant_address);
        restaurantPhone = findViewById(R.id.restaurant_phone);
        restaurantPrice = findViewById(R.id.restaurant_price);
        imagePager = findViewById(R.id.image_pager);
        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view);
        Button addReviewButton = findViewById(R.id.add_review_button);
        heartButton = findViewById(R.id.heart_button);
        noReviewsTextView = findViewById(R.id.no_reviews_text);

        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        reviewsRecyclerView.setAdapter(reviewAdapter);

        String restaurantId = getIntent().getStringExtra("RESTAURANT_ID");

        if (restaurantId != null) {
            fetchRestaurantDetails(restaurantId);
            fetchRestaurantReviews(restaurantId);
            checkIfFavorite(restaurantId);
        } else {
            Log.e(TAG, "No restaurant ID provided.");
        }

        addReviewButton.setOnClickListener(v -> showAddReviewDialog(restaurantId));
        heartButton.setOnClickListener(v -> toggleFavoriteStatus(restaurantId));
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
        db.collection("reviews")
                .whereEqualTo("restaurantId", restaurantId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Review> reviews = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Review review = document.toObject(Review.class);
                        reviews.add(review);
                    }

                    if (reviews.isEmpty()) {
                        noReviewsTextView.setVisibility(View.VISIBLE);
                        reviewsRecyclerView.setVisibility(View.GONE);
                    } else {
                        noReviewsTextView.setVisibility(View.GONE);
                        reviewsRecyclerView.setVisibility(View.VISIBLE);
                        reviewAdapter.updateData(reviews);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching reviews", e));
    }

    private void showAddReviewDialog(String restaurantId) {
        AddReviewDialogFragment dialog = new AddReviewDialogFragment();
        dialog.setAddReviewListener((description, rating) -> {
            // Handle review submission
            if (rating < 1 || rating > 5) {
                Toast.makeText(this, "Rating must be between 1 and 5", Toast.LENGTH_SHORT).show();
            } else {
                submitReview(restaurantId, description, rating);
            }
        });
        dialog.show(getSupportFragmentManager(), "AddReviewDialogFragment");
    }

    private void submitReview(String restaurantId, String description, float rating) {
        // Create a new review object
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Review review = new Review();
        review.setRestaurantId(restaurantId);
        review.setUserId(userId);
        review.setDescription(description);
        review.setRating(rating);

        // Save to Firestore
        db.collection("reviews").add(review.toMap())
                .addOnSuccessListener(documentReference -> {
                    String reviewId = documentReference.getId(); // Get the generated review ID
                    review.setReviewId(reviewId); // Set it to the review object

                    // Update the review with the reviewId
                    documentReference.update("reviewId", reviewId)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Review ID updated in the review document"))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to update review ID in the review document", e));

                    Log.d(TAG, "Review added with ID: " + reviewId);
                    Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show();
                    // Refresh reviews list
                    fetchRestaurantReviews(restaurantId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding review", e);
                    Toast.makeText(this, "Failed to add review. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleFavoriteStatus(String restaurantId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoriteRestaurants = (List<String>) documentSnapshot.get("favoriteRestaurants");
                        if (favoriteRestaurants == null) {
                            favoriteRestaurants = new ArrayList<>();
                        }
                        if (isFavorite) {
                            favoriteRestaurants.remove(restaurantId);
                            heartButton.setText("♡");
                            isFavorite = false;
                        } else {
                            favoriteRestaurants.add(restaurantId);
                            heartButton.setText("♥");
                            isFavorite = true;
                        }
                        db.collection("users").document(userId)
                                .update("favoriteRestaurants", favoriteRestaurants)
                                .addOnSuccessListener(aVoid -> {
                                    String message = isFavorite ? "Added to favorites" : "Removed from favorites";
                                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error updating favorites", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching user data", e));
    }

    private void checkIfFavorite(String restaurantId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoriteRestaurants = (List<String>) documentSnapshot.get("favoriteRestaurants");
                        if (favoriteRestaurants != null && favoriteRestaurants.contains(restaurantId)) {
                            heartButton.setText("♥");
                            isFavorite = true;
                        } else {
                            heartButton.setText("♡");
                            isFavorite = false;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking if favorite", e));
    }
}
