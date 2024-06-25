package com.example.yumyard;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.yumyard.model.User;
import com.example.yumyard.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class ChangeUsernameDialogFragment extends DialogFragment {

    private EditText newUsernameEditText;
    private UserRepository userRepository;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        userRepository = new UserRepository();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_username, null);

        newUsernameEditText = view.findViewById(R.id.newUsernameEditText);

        builder.setView(view)
                .setTitle("Change Username")
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newUsername = newUsernameEditText.getText().toString().trim();
                        if (TextUtils.isEmpty(newUsername)) {
                            showToast("Username cannot be empty");
                            return;
                        }

                        changeUsername(newUsername);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ChangeUsernameDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    private void changeUsername(String newUsername) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.isUsernameTaken(newUsername, new UserRepository.UsernameCheckCallback() {
            @Override
            public void onResult(boolean isTaken) {
                if (isTaken) {
                    showToast("Username is already taken");
                } else {
                    userRepository.updateUsername(userId, newUsername, new UserRepository.UserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            showToast("Username updated successfully");
                            if (getTargetFragment() instanceof SettingsFragment) {
                                ((SettingsFragment) getTargetFragment()).updateUsernameInView(newUsername);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            showToast("Username update failed");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                showToast("Failed to check username availability");
            }
        });
    }

    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
