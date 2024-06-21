package com.example.yumyard;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddReviewDialogFragment extends DialogFragment {

    private EditText reviewDescription;
    private RatingBar reviewRating;
    private Button submitReviewButton;
    private ReviewListener reviewListener;

    public interface ReviewListener {
        void onReviewSubmitted(String description, float rating);
    }

    public void setAddReviewListener(ReviewListener listener) {
        this.reviewListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_review_dialog, container, false);

        reviewDescription = view.findViewById(R.id.review_description);
        reviewRating = view.findViewById(R.id.review_rating);
        submitReviewButton = view.findViewById(R.id.submit_review_button);

        submitReviewButton.setOnClickListener(v -> {
            String description = reviewDescription.getText().toString();
            float rating = reviewRating.getRating();

            // Error handling for invalid rating
            if (rating < 1 || rating > 5) {
                Toast.makeText(getContext(), "Add a rating", Toast.LENGTH_SHORT).show();
            } else {
                if (reviewListener != null) {
                    reviewListener.onReviewSubmitted(description, rating);
                }
                dismiss();
            }
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Add a Review");
        return dialog;
    }
}
