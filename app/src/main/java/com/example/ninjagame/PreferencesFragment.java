package com.example.ninjagame;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        EditTextPreference numberEnemiesPreference = findPreference("chosenEnemies");
        EditTextPreference numberSmallEnemiesPreference = findPreference("chosenSmallEnemies");
        ListPreference chosenNinjaPreference = findPreference("chosenNinja");

        // make sure the keyboard shown is only numeric
        if (numberEnemiesPreference != null) {
            numberEnemiesPreference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));

            // Check number of enemies input
            numberEnemiesPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // Check if input is null
                if ("".equals((String) newValue)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_chosen_enemies_empty), Toast.LENGTH_LONG).show();

                    return false;
                }

                int numberEnemies = Integer.parseInt((String) newValue);
                int minNumber = 1;
                int maxNumber = 25;

                // Check if input is under 1 or over 25
                if (numberEnemies < minNumber || numberEnemies > maxNumber) {
                    Toast.makeText(getActivity(), getString(R.string.toast_chosen_enemies_out_of_bounds, minNumber, maxNumber), Toast.LENGTH_LONG).show();

                    return false;
                }

                Toast.makeText(getActivity(), getResources().getQuantityString(R.plurals.toast_chosen_enemies_correct, numberEnemies, numberEnemies), Toast.LENGTH_LONG).show();

                return true;
            });
        }

        // make sure the keyboard shown is only numeric
        if (numberSmallEnemiesPreference != null) {
            numberSmallEnemiesPreference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));

            // Check number of enemies input
            numberSmallEnemiesPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // Check if input is null
                if ("".equals((String) newValue)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_chosen_enemies_empty), Toast.LENGTH_LONG).show();

                    return false;
                }

                int numberEnemies = Integer.parseInt((String) newValue);
                int minNumber = 1;
                int maxNumber = 10;

                // Check if input is under 1 or over 10
                if (numberEnemies < minNumber || numberEnemies > maxNumber) {
                    Toast.makeText(getActivity(), getString(R.string.toast_chosen_enemies_out_of_bounds, minNumber, maxNumber), Toast.LENGTH_LONG).show();

                    return false;
                }

                Toast.makeText(getActivity(), getResources().getQuantityString(R.plurals.toast_chosen_small_enemies_correct, numberEnemies, numberEnemies), Toast.LENGTH_LONG).show();

                return true;
            });
        }

        // show toast whenever a new ninja is chosen
        if (chosenNinjaPreference != null) {
            chosenNinjaPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                int index = chosenNinjaPreference.findIndexOfValue(newValue.toString());

                if (index != -1) {
                    CharSequence[] entries = chosenNinjaPreference.getEntries();
                    showToastChosenNinja(entries[index]);

                    return true;
                }
                return false;
            });
        }
    }

    private void showToastChosenNinja(CharSequence chosenNinja) {
        Toast.makeText(getActivity(), getString(R.string.toast_chosen_ninja, chosenNinja.toString()), Toast.LENGTH_LONG).show();
    }


}
