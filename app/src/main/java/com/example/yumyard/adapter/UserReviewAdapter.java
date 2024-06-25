package com.example.yumyard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yumyard.R;
import com.example.yumyard.api.YelpApiService;
import com.example.yumyard.model.Review;
import com.example.yumyard.api.YelpBusinessDetail;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class UserReviewAdapter extends RecyclerView.Adapter<UserReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews;
    private YelpApiService yelpApiService;

    public UserReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
        initYelpApiService();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.ratingBar.setRating((float) review.getRating()); // Ensure the rating is cast to float
        holder.descriptionTextView.setText(review.getDescription());

        // Fetch restaurant name
        fetchRestaurantName(review.getRestaurantId(), holder.restaurantNameTextView);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateData(List<Review> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantNameTextView;
        RatingBar ratingBar;
        TextView descriptionTextView;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantNameTextView = itemView.findViewById(R.id.usernameTextView); // Reuse TextView for restaurant name
            ratingBar = itemView.findViewById(R.id.ratingBar);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }

    private void initYelpApiService() {
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

    private void fetchRestaurantName(String restaurantId, TextView textView) {
        String bearerToken = "Bearer QBGT8Eq52mLCSGJj0xr_5jXALbUtLaiko1mDijx68pCsT40634LbVSSQCuNdFulX9mkJJcG_avpRSzIeOfX4eXDnuRGVJ1EAv8eYvgUjgSmQWsZLddmeiFI0nkR0ZnYx";

        Call<YelpBusinessDetail> detailCall = yelpApiService.getBusinessDetails(bearerToken, restaurantId);
        detailCall.enqueue(new Callback<YelpBusinessDetail>() {
            @Override
            public void onResponse(Call<YelpBusinessDetail> call, Response<YelpBusinessDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    YelpBusinessDetail businessDetail = response.body();
                    textView.setText(businessDetail.getName());
                } else {
                    textView.setText("Unknown Restaurant");
                }
            }

            @Override
            public void onFailure(Call<YelpBusinessDetail> call, Throwable t) {
                textView.setText("Unknown Restaurant");
            }
        });
    }
}
