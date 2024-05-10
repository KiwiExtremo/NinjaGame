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
        ListPreference chosenNinjaPreference = findPreference("chosenNinja");

        // make sure the keyboard shown is only numeric
        if (numberEnemiesPreference != null) {
            numberEnemiesPreference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        }

        // show toast whenever a new ninja is chosen
        if (chosenNinjaPreference != null) {
            chosenNinjaPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                int index = chosenNinjaPreference.findIndexOfValue(newValue.toString());

                if (index != -1) {
                    CharSequence[] entries = chosenNinjaPreference.getEntries();
                    showToastChosenNinja(entries[index]);
                }
                return true;
            });
        }
    }

    private void showToastChosenNinja(CharSequence chosenNinja) {
        Toast.makeText(getActivity(), getString(R.string.toast_chosen_ninja, chosenNinja.toString()), Toast.LENGTH_SHORT).show();
    }
}
