package com.example.yumyard;

import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class PriceRangeDialogFragment extends DialogFragment {
    private boolean[] selectedPrices = new boolean[4]; // To track selected price ranges
    private String[] priceOptions = {"$", "$$", "$$$", "$$$$"};
    private PriceRangeSelectionListener listener;

    public interface PriceRangeSelectionListener {
        void onPriceRangeSelected(String selectedPrices);
    }

    public void setPriceRangeSelectionListener(PriceRangeSelectionListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Price Range")
                .setMultiChoiceItems(priceOptions, selectedPrices, (dialog, which, isChecked) -> {
                    selectedPrices[which] = isChecked;
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder selectedPricesString = new StringBuilder();
                    for (int i = 0; i < selectedPrices.length; i++) {
                        if (selectedPrices[i]) {
                            if (selectedPricesString.length() > 0) {
                                selectedPricesString.append(",");
                            }
                            selectedPricesString.append(i + 1); // Yelp uses 1, 2, 3, 4 for price levels
                        }
                    }
                    if (listener != null) {
                        listener.onPriceRangeSelected(selectedPricesString.toString());
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder.create();
    }
}
