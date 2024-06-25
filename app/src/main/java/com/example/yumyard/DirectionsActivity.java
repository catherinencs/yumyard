package com.example.yumyard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.yumyard.api.DirectionsApiService;
import com.example.yumyard.api.DirectionsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "DirectionsActivity";
    private GoogleMap mMap;
    private LatLng homeLocation;
    private LatLng restaurantLocation;
    private DirectionsApiService directionsApiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String homeLocationString = prefs.getString("HOME_LOCATION", null);
        if (homeLocationString != null) {
            String[] parts = homeLocationString.split(",");
            homeLocation = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
        } else {
            Log.e(TAG, "Home location not found in SharedPreferences");
            Toast.makeText(this, "Home location not set", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Retrieve coordinates from intent
        double restaurantLat = getIntent().getDoubleExtra("RESTAURANT_LAT", 0);
        double restaurantLng = getIntent().getDoubleExtra("RESTAURANT_LNG", 0);
        if (restaurantLat != 0 && restaurantLng != 0) {
            restaurantLocation = new LatLng(restaurantLat, restaurantLng);
        } else {
            Log.e(TAG, "Restaurant coordinates not found in intent");
            Toast.makeText(this, "Restaurant coordinates not provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initialize Retrofit for Directions API
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        directionsApiService = retrofit.create(DirectionsApiService.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (homeLocation != null && restaurantLocation != null) {
            // Add markers for home and restaurant
            mMap.addMarker(new MarkerOptions().position(homeLocation).title("Your Location"));
            mMap.addMarker(new MarkerOptions().position(restaurantLocation).title("Restaurant"));

            // Zoom to fit both markers
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(homeLocation);
            builder.include(restaurantLocation);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

            // Show routes
            showRoutes("driving");
            showRoutes("walking");
        } else {
            Log.e(TAG, "Invalid home or restaurant location");
        }
    }

    private void showRoutes(String mode) {
        Call<DirectionsResponse> call = directionsApiService.getDirections(
                homeLocation.latitude + "," + homeLocation.longitude,
                restaurantLocation.latitude + "," + restaurantLocation.longitude,
                mode,
                getString(R.string.google_maps_key)
        );

        call.enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DirectionsResponse.Route> routes = response.body().getRoutes();
                    if (!routes.isEmpty()) {
                        DirectionsResponse.Route route = routes.get(0);
                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.color(mode.equals("driving") ? 0xFF00FF00 : 0xFFFF0000); // Green for driving, red for walking
                        polylineOptions.width(10);

                        List<LatLng> decodedPath = com.google.maps.android.PolyUtil.decode(route.getOverviewPolyline().getPoints());
                        polylineOptions.addAll(decodedPath);

                        mMap.addPolyline(polylineOptions);
                    }
                } else {
                    Log.e(TAG, "Directions API response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching directions", t);
            }
        });
    }
}
