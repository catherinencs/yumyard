package com.example.yumyard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yumyard.adapter.RestaurantAdapter;
import com.example.yumyard.model.Restaurant;
import com.example.yumyard.repository.RestaurantRepository;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements PriceRangeDialogFragment.PriceRangeSelectionListener, CuisineDialogFragment.CuisineSelectionListener, RestaurantAdapter.OnItemClickListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private RestaurantRepository restaurantRepository;
    private EditText searchBar;
    private TextView locationPrompt;
    private Button priceRangeButton, cuisineButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String selectedLocation;
    private String selectedPriceRange = ""; // To store the selected price range
    private String selectedCuisines = ""; // To store the selected cuisines

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize components
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        restaurantAdapter = new RestaurantAdapter(getActivity(), new ArrayList<>(), this);
        recyclerView.setAdapter(restaurantAdapter);
        searchBar = view.findViewById(R.id.search_bar);
        locationPrompt = view.findViewById(R.id.location_prompt);
        priceRangeButton = view.findViewById(R.id.price_range_button);
        cuisineButton = view.findViewById(R.id.cuisine_button);

        // Initialize repository
        restaurantRepository = new RestaurantRepository();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Initialize Places
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), getString(R.string.google_maps_key));
        }

        // Setup AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                selectedLocation = place.getLatLng().latitude + "," + place.getLatLng().longitude;
                Log.d(TAG, "Selected Location: " + selectedLocation);
                locationPrompt.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                searchRestaurants(selectedLocation, searchBar.getText().toString());

                // Save selected location to SharedPreferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putString("HOME_LOCATION", selectedLocation).apply();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        // Price range button functionality
        priceRangeButton.setOnClickListener(v -> showPriceRangeDialog());

        // Cuisine button functionality
        cuisineButton.setOnClickListener(v -> showCuisineDialog());

        // Search functionality
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (selectedLocation != null) {
                Log.d(TAG, "Search Term: " + searchBar.getText().toString());
                searchRestaurants(selectedLocation, searchBar.getText().toString());
            } else {
                Toast.makeText(getActivity(), "Please select a location first.", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        return view;
    }

    private void searchRestaurants(String location, String term) {
        Log.d(TAG, "Searching Restaurants: Location=" + location + ", Term=" + term + ", Price=" + selectedPriceRange + ", Cuisines=" + selectedCuisines);
        restaurantRepository.searchRestaurants(location, term, selectedPriceRange, selectedCuisines, new RestaurantRepository.RestaurantListCallback() {
            @Override
            public void onSuccess(List<Restaurant> restaurants) {
                if (restaurants.isEmpty()) {
                    Log.d(TAG, "No restaurants found.");
                    Toast.makeText(getActivity(), "No restaurants found.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Restaurants found: " + restaurants.size());
                    restaurantAdapter.updateData(restaurants);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to load restaurants.", e);
                Toast.makeText(getActivity(), "Failed to load restaurants.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPriceRangeDialog() {
        PriceRangeDialogFragment dialog = new PriceRangeDialogFragment();
        dialog.setPriceRangeSelectionListener(this);
        dialog.show(getParentFragmentManager(), "priceRangeDialog");
    }

    private void showCuisineDialog() {
        CuisineDialogFragment dialog = new CuisineDialogFragment();
        dialog.setCuisineSelectionListener(this);
        dialog.show(getParentFragmentManager(), "cuisineDialog");
    }

    @Override
    public void onPriceRangeSelected(String selectedPrices) {
        selectedPriceRange = selectedPrices;
        Log.d(TAG, "Selected Price Range: " + selectedPrices);

        // Update the button text with the selected price ranges
        if (selectedPrices.isEmpty()) {
            priceRangeButton.setText("Price Range");
        } else {
            priceRangeButton.setText(convertPricesToDollarSigns(selectedPrices));
        }

        // Trigger a new search with the updated price range
        if (selectedLocation != null) {
            searchRestaurants(selectedLocation, searchBar.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please select a location first.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCuisineSelected(String selectedCuisines) {
        this.selectedCuisines = selectedCuisines;
        Log.d(TAG, "Selected Cuisines: " + selectedCuisines);

        // Update the button text with the selected cuisines
        if (selectedCuisines.isEmpty()) {
            cuisineButton.setText("Cuisine");
        } else {
            cuisineButton.setText(convertCuisinesToDisplay(selectedCuisines));
        }

        // Trigger a new search with the updated cuisines
        if (selectedLocation != null) {
            searchRestaurants(selectedLocation, searchBar.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please select a location first.", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertPricesToDollarSigns(String prices) {
        // Convert the numeric price string to dollar signs
        StringBuilder dollarSigns = new StringBuilder();
        for (char c : prices.toCharArray()) {
            if (c == '1') {
                dollarSigns.append("$, ");
            } else if (c == '2') {
                dollarSigns.append("$$, ");
            } else if (c == '3') {
                dollarSigns.append("$$$, ");
            } else if (c == '4') {
                dollarSigns.append("$$$$, ");
            }
        }
        if (dollarSigns.length() > 0) {
            dollarSigns.setLength(dollarSigns.length() - 2); // Remove trailing comma and space
        }
        return dollarSigns.toString();
    }

    private String convertCuisinesToDisplay(String cuisines) {
        // Convert the categories to a displayable string
        StringBuilder displayCuisines = new StringBuilder();
        for (String cuisine : cuisines.split(",")) {
            switch (cuisine) {
                case "newamerican":
                    displayCuisines.append("American, ");
                    break;
                case "chinese":
                    displayCuisines.append("Chinese, ");
                    break;
                case "japanese":
                    displayCuisines.append("Japanese, ");
                    break;
                case "korean":
                    displayCuisines.append("Korean, ");
                    break;
                case "indpak":
                    displayCuisines.append("Indian, ");
                    break;
                case "mexican":
                    displayCuisines.append("Mexican, ");
                    break;
                case "mediterranean":
                    displayCuisines.append("Mediterranean, ");
                    break;
                case "italian":
                    displayCuisines.append("Italian, ");
                    break;
            }
        }
        if (displayCuisines.length() > 0) {
            displayCuisines.setLength(displayCuisines.length() - 2); // Remove trailing comma and space
        }
        return displayCuisines.toString();
    }

    @Override
    public void onItemClick(Restaurant restaurant) {
        Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
        intent.putExtra("RESTAURANT_ID", restaurant.getRestaurantId());
        intent.putExtra("RESTAURANT_IMAGE_URL", restaurant.getImageUrl()); // Pass image URL
        startActivity(intent);
    }
}
