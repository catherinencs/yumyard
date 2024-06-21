package com.example.yumyard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yumyard.R;
import com.example.yumyard.model.Review;
import com.example.yumyard.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews;
    private Map<String, String> usernameCache = new HashMap<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
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

        // Fetch username and display it
        String userId = review.getUserId();
        if (usernameCache.containsKey(userId)) {
            holder.usernameTextView.setText(usernameCache.get(userId));
        } else {
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                String username = user.getUsername();
                                usernameCache.put(userId, username);
                                holder.usernameTextView.setText(username);
                            }
                        }
                    })
                    .addOnFailureListener(e -> holder.usernameTextView.setText("Unknown"));
        }

        holder.ratingBar.setRating((float) review.getRating());
        holder.descriptionTextView.setText(review.getDescription());
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
        TextView usernameTextView;
        RatingBar ratingBar;
        TextView descriptionTextView;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
