package com.example.yumyard;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.List;

public class CuisineDialogFragment extends DialogFragment {

    private static final String TAG = "CuisineDialogFragment";
    private CuisineSelectionListener listener;
    private boolean[] selectedItems;
    private String[] cuisines = {"American", "Asian", "European", "Latin American", "Middle Eastern", "African"};
    private String[] cuisineCategories = {
            "newamerican,tradamerican,bbq,burgers,southern,hotdog", // American
            "chinese,japanese,sushi,thai,korean,vietnamese,indpak,asianfusion,ramen,dimsum,bubbletea", // Asian
            "italian,french,mediterranean,greek,spanish,german,british,portuguese", // European
            "mexican,latin,brazilian,argentine,cuban,peruvian,caribbean", // Latin American
            "middleeastern,lebanese,persian,turkish", // Middle Eastern
            "ethiopian,moroccan" // African
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        selectedItems = new boolean[cuisines.length];
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Cuisines")
                .setMultiChoiceItems(cuisines, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        selectedItems[which] = isChecked;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> selectedCuisines = new ArrayList<>();
                        for (int i = 0; i < selectedItems.length; i++) {
                            if (selectedItems[i]) {
                                selectedCuisines.add(cuisineCategories[i]);
                            }
                        }
                        if (listener != null) {
                            listener.onCuisineSelected(String.join(",", selectedCuisines));
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    public void setCuisineSelectionListener(CuisineSelectionListener listener) {
        this.listener = listener;
    }

    public interface CuisineSelectionListener {
        void onCuisineSelected(String selectedCuisines);
    }
}
