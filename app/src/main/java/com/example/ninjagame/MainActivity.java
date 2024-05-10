package com.example.ninjagame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private boolean isMusic;
    private String ninjaSprite;
    private ImageView ivNinja;
    private AnimatorSet rotateSet;
    private SharedPreferences pref, scoresPref;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchAndInitialize();

        updateBackgroundMusic();
        updateNinjaSprite();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBackgroundMusic();
        updateNinjaSprite();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mp != null && mp.isPlaying()) {
            mp.pause();
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
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);
        }
        if (item.getItemId() == R.id.iInfo) {
            showDialogInfo();
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    public void bPlayGame(View view) {
        showDialogInputUsername();
    }

    public void bShowLeaderboards(View view) {
        showDialogLeaderboards();
    }

    public void bCloseApp(View view) {
        showDialogCloseApp();
    }

    private void startGameActivity(String username) {
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("username", username);

        startActivity(gameIntent);
    }

    private void updateBackgroundMusic() {
        isMusic = pref.getBoolean("checkMusic", true);

        if (isMusic) {
            mp.setLooping(true);
            mp.start();

        } else if (mp != null && mp.isPlaying()) {
            mp.pause();
        }


    }

    private void updateNinjaSprite() {
        ninjaSprite = pref.getString("chosenNinja", getString(R.string.preference_default_ninja_drawable));
        int ninjaID = getResources().getIdentifier(ninjaSprite, "drawable", this.getPackageName());

        ivNinja.setImageResource(ninjaID);

        TimeInterpolator inter = new LinearInterpolator();
        ValueAnimator rotateImage;

        rotateImage = ObjectAnimator.ofFloat(ivNinja, "rotation", 0, 360);
        rotateImage.setDuration(3000);
        rotateImage.setRepeatCount(ValueAnimator.INFINITE);
        rotateImage.setInterpolator(inter);

        rotateSet.play(rotateImage);
        rotateSet.start();
    }

    private LinkedHashMap<String, String> getScoresFromPreferences() {
        int i = 1;
        boolean empty = false;

        HashMap<String, Integer> mPlayerScores = new HashMap<>();

        while (!empty) {
            if ((scoresPref.getString("player" + i, getString(R.string.preference_default_player_name)).equals(getString(R.string.preference_default_player_name)))) {
                if (mPlayerScores.size() == 0) {
                    mPlayerScores.put(scoresPref.getString("player" + i, getString(R.string.preference_default_player_name)), scoresPref.getInt("score" + i, -1));
                }

                empty = true;

            } else {
                mPlayerScores.put(scoresPref.getString("player" + i, getString(R.string.preference_default_player_name)), scoresPref.getInt("score" + i, -1));
                i++;
            }
        }

        return sortMapByValue(mPlayerScores);
    }

    private LinkedHashMap<String, String> sortMapByValue(HashMap<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());

        list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : list) {
            // convert int score to string "X points"
            String value = "";

            if (entry.getValue() == -1) {
                value = getString(R.string.preference_default_player_score);
            } else {
                value = getString(R.string.preference_default_player_points, entry.getValue());
            }

            sortedMap.put(entry.getKey(), value);
        }

        return sortedMap;
    }
    private LinearLayout createWrappedLayout(EditText editText) {
        LinearLayout linearLayout = new LinearLayout(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;

        editText.setLayoutParams(layoutParams);

        // insert the editText into the linearLayout
        linearLayout.addView(editText);
        linearLayout.setPadding(60, 0, 60, 0);

        return linearLayout;
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

    private void showDialogLeaderboards() {
        // Inflate the leaderboards xml
        View vLeaderboards = getLayoutInflater().inflate(R.layout.activity_leaderboard, null);
        // TODO set max height of listView to avoid filling the whole screen (tutorial on stackoverflow)

        // Get leaderboards' data from shared preferences
        LinkedHashMap<String, String> mPlayerScores = getScoresFromPreferences();

        LeaderboardAdapter scoresAdapter = new LeaderboardAdapter(mPlayerScores);

        ListView lvScores = vLeaderboards.findViewById(R.id.lvScores);
        lvScores.setAdapter(scoresAdapter);

        // set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_leaderboards_title));
        builder.setMessage(getString(R.string.dialog_leaderboards_body));

        // set up the max size of the view containing the listView
        setViewMaxSize(scoresAdapter, lvScores);
        builder.setView(vLeaderboards);

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_leaderboards_positive), (dialog, which) -> {
            // Do nothing, just close dialog box
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setViewMaxSize(LeaderboardAdapter lvScores, ListView scores) {
        // set max size if there are more than 5 items inside the listView
        if (lvScores.getCount() > 5) {
            // grab the size of any given item
            View item = lvScores.getView(0, null, scores);
            item.measure(0, 0);

            // set the size of the listView to 5.7 times the item size
            // TODO change width with guidelines programmatically (stackoverflow)
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(0, (int) (5.7 * item.getMeasuredHeight()));
            scores.setLayoutParams(params);
        }
    }

    private void showDialogInputUsername() {
        // TODO shorten code / extract methods
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_username_title));

        // create an editText
        EditText etUsername = new EditText(this);
        etUsername.setSingleLine();
        etUsername.setHint(getString(R.string.dialog_username_hint));

        // create a linearLayout to wrap the editText
        LinearLayout llUsername = createWrappedLayout(etUsername);

        builder.setView(llUsername);

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_username_positive), (dialog, which) -> {
            // do nothing here, since we will override this onClickListener later
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // override positive button's onClick to prevent it from closing automatically
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String username = etUsername.getText().toString();

            if ("".equals(username)) {
                etUsername.setError(getString(R.string.editText_error));

            } else {
                startGameActivity(username);
                dialog.dismiss();
            }
        });
    }

    private void showDialogCloseApp() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dialog_close_body));

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_close_positive), (dialog, which) -> {
            finish();
            System.exit(0);
        });

        builder.setNegativeButton(getString(R.string.dialog_close_negative), (dialog, which) -> {
            // Do nothing, just close dialog box
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        showDialogCloseApp();
    }

    private void fetchAndInitialize() {
        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);

        rotateSet = new AnimatorSet();

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        scoresPref = getSharedPreferences(getString(R.string.preference_scores_name), Context.MODE_PRIVATE);

        mp = MediaPlayer.create(MainActivity.this, R.raw.background_music);

        ivNinja = findViewById(R.id.ivNinja);
    }
}