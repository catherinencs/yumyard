package com.example.yumyard.repository;

import com.example.yumyard.model.Review;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface ReviewCallback {
        void onSuccess(Review review);
        void onFailure(Exception e);
    }

    public interface ReviewListCallback {
        void onSuccess(List<Review> reviews);
        void onFailure(Exception e);
    }

    public void getReviewsByRestaurant(String restaurantId, final ReviewListCallback callback) {
        db.collection("reviews").whereEqualTo("restaurantId", restaurantId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Review> reviews = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        reviews.add(document.toObject(Review.class));
                    }
                    callback.onSuccess(reviews);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getReviewsByUser(String userId, final ReviewListCallback callback) {
        db.collection("reviews").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Review> reviews = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        reviews.add(document.toObject(Review.class));
                    }
                    callback.onSuccess(reviews);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void addReview(Review review) {
        db.collection("reviews").document(review.getReviewId()).set(review.toMap());
    }

    public void updateReview(Review review) {
        db.collection("reviews").document(review.getReviewId()).set(review.toMap());
    }

    public void deleteReview(String reviewId) {
        db.collection("reviews").document(reviewId).delete();
    }
}
