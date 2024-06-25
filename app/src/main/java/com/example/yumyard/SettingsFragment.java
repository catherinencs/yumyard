package com.example.yumyard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.yumyard.model.User;
import com.example.yumyard.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private TextView emailTextView, usernameTextView;
    private Button changeUsernameButton;
    private Switch darkModeSwitch;
    private UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        emailTextView = view.findViewById(R.id.emailTextView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        changeUsernameButton = view.findViewById(R.id.changeUsernameButton);
        darkModeSwitch = view.findViewById(R.id.darkModeSwitch);

        userRepository = new UserRepository();
        loadUserProfile();

        changeUsernameButton.setOnClickListener(v -> showChangeUsernameDialog());
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> toggleDarkMode(isChecked));

        return view;
    }

    private void loadUserProfile() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUser(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                emailTextView.setText(user.getEmail());
                usernameTextView.setText(user.getUsername());
                darkModeSwitch.setChecked(user.isDarkMode());
            }

            @Override
            public void onFailure(Exception e) {
                // Handle error
            }
        });
    }

    private void showChangeUsernameDialog() {
        ChangeUsernameDialogFragment dialogFragment = new ChangeUsernameDialogFragment();
        dialogFragment.setTargetFragment(SettingsFragment.this, 0);
        dialogFragment.show(getParentFragmentManager(), "ChangeUsernameDialogFragment");
    }

    private void toggleDarkMode(boolean isEnabled) {
        // Update user's dark mode preference in the database
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.updateDarkMode(userId, isEnabled, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                // Apply dark mode change to the app
                AppCompatDelegate.setDefaultNightMode(isEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

                // Save preference locally
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("dark_mode", isEnabled);
                editor.apply();
            }

            @Override
            public void onFailure(Exception e) {
                // Handle error
            }
        });
    }

    public void updateUsernameInView(String newUsername) {
        if (usernameTextView != null) {
            usernameTextView.setText(newUsername);
        }
    }
}
