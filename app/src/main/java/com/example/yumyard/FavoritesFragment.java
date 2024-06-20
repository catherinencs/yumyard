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
import com.example.yumyard.adapter.RestaurantAdapter;
import com.example.yumyard.model.Restaurant;
import com.example.yumyard.model.User;
import com.example.yumyard.repository.RestaurantRepository;
import com.example.yumyard.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        restaurantAdapter = new RestaurantAdapter(new ArrayList<>());
        recyclerView.setAdapter(restaurantAdapter);

        userRepository = new UserRepository();
        restaurantRepository = new RestaurantRepository();
        fetchFavorites();

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
                    restaurantRepository.getRestaurant(restaurantId, new RestaurantRepository.RestaurantCallback() {
                        @Override
                        public void onSuccess(Restaurant restaurant) {
                            favoriteRestaurants.add(restaurant);
                            restaurantAdapter.updateData(favoriteRestaurants);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle error
            }
        });
    }
}
