package com.example.yumyard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yumyard.R;
import com.example.yumyard.model.Restaurant;

import java.util.List;

public class FavoriteRestaurantAdapter extends RecyclerView.Adapter<FavoriteRestaurantAdapter.FavoriteRestaurantViewHolder> {

    private List<Restaurant> favoriteRestaurants;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    public FavoriteRestaurantAdapter(List<Restaurant> favoriteRestaurants, OnItemClickListener onItemClickListener) {
        this.favoriteRestaurants = favoriteRestaurants;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public FavoriteRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_restaurant, parent, false);
        return new FavoriteRestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteRestaurantViewHolder holder, int position) {
        Restaurant restaurant = favoriteRestaurants.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantAddress.setText(restaurant.getAddress());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(restaurant));
    }

    @Override
    public int getItemCount() {
        return favoriteRestaurants.size();
    }

    public void updateData(List<Restaurant> newFavoriteRestaurants) {
        this.favoriteRestaurants = newFavoriteRestaurants;
        notifyDataSetChanged();
    }

    public Restaurant getRestaurantAtPosition(int position) {
        return favoriteRestaurants.get(position);
    }

    public void removeRestaurantAtPosition(int position) {
        favoriteRestaurants.remove(position);
    }

    static class FavoriteRestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        TextView restaurantAddress;

        FavoriteRestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
        }
    }
}
