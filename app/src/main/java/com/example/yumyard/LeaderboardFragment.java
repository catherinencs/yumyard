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
import com.example.yumyard.adapter.UserAdapter;
import com.example.yumyard.model.User;
import com.example.yumyard.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userAdapter = new UserAdapter(new ArrayList<>());
        recyclerView.setAdapter(userAdapter);

        userRepository = new UserRepository();
        fetchLeaderboard();

        return view;
    }

    private void fetchLeaderboard() {
        userRepository.getUsersByReviewCount(new UserRepository.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                userAdapter.updateData(users);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle error
            }
        });
    }
}
