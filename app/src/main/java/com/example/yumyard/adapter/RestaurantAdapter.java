package com.example.yumyard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yumyard.R;
import com.example.yumyard.model.Restaurant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    private Context context;
    private List<Restaurant> restaurantList;
    private OnItemClickListener onItemClickListener;
    private FirebaseFirestore db;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.onItemClickListener = onItemClickListener;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantAddress.setText(restaurant.getAddress());
        Glide.with(context)
                .load(restaurant.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.restaurantImage);

        // Fetch and display rating and review count
        fetchRatingAndReviews(holder, restaurant.getRestaurantId());

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(restaurant));
    }

    private void fetchRatingAndReviews(RestaurantViewHolder holder, String restaurantId) {
        db.collection("reviews")
                .whereEqualTo("restaurantId", restaurantId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        double sumRating = 0;
                        int reviewCount = queryDocumentSnapshots.size();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Double rating = document.getDouble("rating");
                            if (rating != null) {
                                sumRating += rating;
                            }
                        }

                        double averageRating = sumRating / reviewCount;
                        holder.ratingBar.setRating((float) Math.round(averageRating));
                        holder.ratingBar.setVisibility(View.VISIBLE);
                        holder.reviewCountTextView.setText(String.format("(%d)", reviewCount));
                        holder.reviewCountTextView.setVisibility(View.VISIBLE);
                        holder.noReviewsTextView.setVisibility(View.GONE);
                    } else {
                        holder.ratingBar.setVisibility(View.GONE);
                        holder.reviewCountTextView.setVisibility(View.GONE);
                        holder.noReviewsTextView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    holder.ratingBar.setVisibility(View.GONE);
                    holder.reviewCountTextView.setVisibility(View.GONE);
                    holder.noReviewsTextView.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public void updateData(List<Restaurant> restaurants) {
        this.restaurantList = restaurants;
        notifyDataSetChanged();
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        TextView restaurantAddress;
        ImageView restaurantImage;
        RatingBar ratingBar;
        TextView reviewCountTextView;
        TextView noReviewsTextView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
            restaurantImage = itemView.findViewById(R.id.restaurant_image);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            reviewCountTextView = itemView.findViewById(R.id.review_count);
            noReviewsTextView = itemView.findViewById(R.id.no_reviews_text);
        }
    }
}
