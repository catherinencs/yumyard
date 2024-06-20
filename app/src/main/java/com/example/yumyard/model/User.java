package com.example.yumyard.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String userId;
    private String username;
    private String email;
    private List<String> favoriteRestaurants;
    private int reviewCount;
    private boolean darkMode;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String username, String email, List<String> favoriteRestaurants, int reviewCount, boolean darkMode) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.favoriteRestaurants = favoriteRestaurants;
        this.reviewCount = reviewCount;
        this.darkMode = darkMode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    public void setFavoriteRestaurants(List<String> favoriteRestaurants) {
        this.favoriteRestaurants = favoriteRestaurants;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", username);
        result.put("email", email);
        result.put("favoriteRestaurants", favoriteRestaurants);
        result.put("reviewCount", reviewCount);
        result.put("darkMode", darkMode);
        return result;
    }
}
