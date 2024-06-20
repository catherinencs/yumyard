package com.example.yumyard.model;

import java.util.HashMap;
import java.util.Map;

public class Restaurant {
    private String restaurantId;
    private String name;
    private String imageUrl;
    private String address;
    private int reviewCount;
    private double averageRating;

    public Restaurant() {
        // Default constructor required for calls to DataSnapshot.getValue(Restaurant.class)
    }

    public Restaurant(String restaurantId, String name, String imageUrl, String address, int reviewCount, double averageRating) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.address = address;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("restaurantId", restaurantId);
        result.put("name", name);
        result.put("imageUrl", imageUrl);
        result.put("address", address);
        result.put("reviewCount", reviewCount);
        result.put("averageRating", averageRating);
        return result;
    }
}
