package com.example.yumyard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yumyard.adapter.ReviewAdapter;
import com.example.yumyard.model.Review;
import com.example.yumyard.repository.ReviewRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private ReviewRepository reviewRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        recyclerView.setAdapter(reviewAdapter);

        reviewRepository = new ReviewRepository();
        fetchUserReviews();

        return view;
    }

    private void fetchUserReviews() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reviewRepository.getReviewsByUser(userId, new ReviewRepository.ReviewListCallback() {
            @Override
            public void onSuccess(List<Review> reviews) {
                reviewAdapter.updateData(reviews);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle error
            }
        });
    }
}
