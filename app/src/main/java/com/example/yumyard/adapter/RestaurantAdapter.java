package com.example.yumyard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yumyard.R;
import com.example.yumyard.model.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    private Context context;
    private List<Restaurant> restaurantList;
    private OnItemClickListener onItemClickListener;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantAddress.setText(restaurant.getAddress());
        Glide.with(context)
                .load(restaurant.getImageUrl())
                .placeholder(R.drawable.placeholder_image) // add a placeholder image in your drawable resources
                .into(holder.restaurantImage);

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(restaurant));
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public void updateData(List<Restaurant> restaurants) {
        this.restaurantList = restaurants;
        notifyDataSetChanged();
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        TextView restaurantAddress;
        ImageView restaurantImage;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
            restaurantImage = itemView.findViewById(R.id.restaurant_image);
        }
    }
}
