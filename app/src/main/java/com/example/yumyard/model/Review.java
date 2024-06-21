package com.example.yumyard.model;

import java.util.HashMap;
import java.util.Map;

public class Review {
    private String reviewId;
    private String userId;
    private String restaurantId;
    private double rating;
    private String description;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public Review(String reviewId, String userId, String restaurantId, double rating, String description) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.description = description;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("reviewId", reviewId);
        result.put("userId", userId);
        result.put("restaurantId", restaurantId);
        result.put("rating", rating);
        result.put("description", description);
        return result;
    }
}
