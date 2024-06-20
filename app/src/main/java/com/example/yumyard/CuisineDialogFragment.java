package com.example.yumyard;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

public class CuisineDialogFragment extends DialogFragment {

    private String[] cuisines = {"American", "Chinese", "Japanese", "Korean", "Indian", "Mexican", "Mediterranean", "Italian"};
    private boolean[] selectedCuisines;
    private CuisineSelectionListener listener;

    public interface CuisineSelectionListener {
        void onCuisineSelected(String selectedCuisines);
    }

    public void setCuisineSelectionListener(CuisineSelectionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        selectedCuisines = new boolean[cuisines.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select Cuisines")
                .setMultiChoiceItems(cuisines, selectedCuisines, (dialog, which, isChecked) -> {
                    selectedCuisines[which] = isChecked;
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder selected = new StringBuilder();
                    for (int i = 0; i < cuisines.length; i++) {
                        if (selectedCuisines[i]) {
                            selected.append(convertToCategory(cuisines[i])).append(",");
                        }
                    }
                    if (selected.length() > 0) {
                        selected.setLength(selected.length() - 1); // Remove trailing comma
                    }
                    if (listener != null) {
                        listener.onCuisineSelected(selected.toString());
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private String convertToCategory(String cuisine) {
        switch (cuisine) {
            case "American":
                return "newamerican";
            case "Chinese":
                return "chinese";
            case "Japanese":
                return "japanese";
            case "Korean":
                return "korean";
            case "Indian":
                return "indpak";
            case "Mexican":
                return "mexican";
            case "Mediterranean":
                return "mediterranean";
            case "Italian":
                return "italian";
            default:
                return "";
        }
    }
}
