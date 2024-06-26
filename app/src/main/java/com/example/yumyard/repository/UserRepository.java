package com.example.yumyard.repository;

import com.example.yumyard.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface UserListCallback {
        void onSuccess(List<User> users);
        void onFailure(Exception e);
    }

    public interface UsernameCheckCallback {
        void onResult(boolean isTaken);
        void onFailure(Exception e);
    }

    public void getUser(String userId, final UserCallback callback) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getUsersByReviewCount(final UserListCallback callback) {
        db.collection("users").orderBy("reviewCount", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        users.add(document.toObject(User.class));
                    }
                    callback.onSuccess(users);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void isUsernameTaken(String username, final UsernameCheckCallback callback) {
        db.collection("users").whereEqualTo("username", username).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean isTaken = !queryDocumentSnapshots.isEmpty();
                    callback.onResult(isTaken);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void updateUsername(String userId, String newUsername, final UserCallback callback) {
        db.collection("users").document(userId).update("username", newUsername)
                .addOnSuccessListener(aVoid -> getUser(userId, callback))
                .addOnFailureListener(callback::onFailure);
    }

    public void updateDarkMode(String userId, boolean isEnabled, final UserCallback callback) {
        db.collection("users").document(userId).update("darkMode", isEnabled)
                .addOnSuccessListener(aVoid -> getUser(userId, callback))
                .addOnFailureListener(callback::onFailure);
    }

    public void updateUser(User user, final UserCallback callback) {
        db.collection("users").document(user.getUserId()).set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                .addOnFailureListener(callback::onFailure);
    }
}
