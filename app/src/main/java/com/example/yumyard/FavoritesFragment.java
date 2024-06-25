package com.example.yumyard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yumyard.adapter.FavoriteRestaurantAdapter;
import com.example.yumyard.model.Restaurant;
import com.example.yumyard.model.User;
import com.example.yumyard.repository.RestaurantRepository;
import com.example.yumyard.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements FavoriteRestaurantAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private FavoriteRestaurantAdapter favoriteRestaurantAdapter;
    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;
    private static final String TAG = "FavoritesFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        favoriteRestaurantAdapter = new FavoriteRestaurantAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(favoriteRestaurantAdapter);

        userRepository = new UserRepository();
        restaurantRepository = new RestaurantRepository();
        fetchFavorites();

        setupItemTouchHelper();

        return view;
    }

    private void fetchFavorites() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUser(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                List<String> favoriteIds = user.getFavoriteRestaurants();
                List<Restaurant> favoriteRestaurants = new ArrayList<>();

                for (String restaurantId : favoriteIds) {
                    restaurantRepository.getRestaurantDetailsFromYelp(restaurantId, new RestaurantRepository.RestaurantCallback() {
                        @Override
                        public void onSuccess(Restaurant restaurant) {
                            favoriteRestaurants.add(restaurant);
                            // Update the adapter data
                            favoriteRestaurantAdapter.updateData(new ArrayList<>(favoriteRestaurants));
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "Failed to fetch restaurant details", e);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to fetch user details", e);
            }
        });
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Restaurant restaurant = favoriteRestaurantAdapter.getRestaurantAtPosition(position);
                showRemoveConfirmationDialog(restaurant, position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showRemoveConfirmationDialog(Restaurant restaurant, int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Remove Favorite")
                .setMessage("Are you sure you want to remove this restaurant from your favorites?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> removeRestaurantFromFavorites(restaurant, position))
                .setNegativeButton(android.R.string.no, (dialog, which) -> favoriteRestaurantAdapter.notifyItemChanged(position))
                .show();
    }

    private void removeRestaurantFromFavorites(Restaurant restaurant, int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUser(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                List<String> favoriteIds = user.getFavoriteRestaurants();
                favoriteIds.remove(restaurant.getRestaurantId());
                user.setFavoriteRestaurants(favoriteIds);

                userRepository.updateUser(user, new UserRepository.UserCallback() {
                    @Override
                    public void onSuccess(User updatedUser) {
                        favoriteRestaurantAdapter.removeRestaurantAtPosition(position);
                        favoriteRestaurantAdapter.notifyItemRemoved(position);
                        favoriteRestaurantAdapter.notifyItemRangeChanged(position, favoriteRestaurantAdapter.getItemCount());
                        Log.d(TAG, "Restaurant removed from favorites");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to update user", e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to fetch user", e);
            }
        });
    }

    @Override
    public void onItemClick(Restaurant restaurant) {
        Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
        intent.putExtra("RESTAURANT_ID", restaurant.getRestaurantId());
        intent.putExtra("RESTAURANT_IMAGE_URL", restaurant.getImageUrl()); // Pass image URL
        startActivity(intent);
    }
}
