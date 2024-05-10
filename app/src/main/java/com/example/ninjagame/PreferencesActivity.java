package com.example.ninjagame;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;

public class PreferencesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.settingsFragment, PreferencesFragment.class, null)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.iSettings) {
            // do nothing
            return true;
        }
        if (item.getItemId() == R.id.iInfo) {
            showDialogInfo();
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    private void showDialogInfo() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_info_title));
        builder.setMessage(getString(R.string.dialog_info_body));

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_info_positive), (dialog, which) -> {
            // Do nothing, just close dialog box
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
